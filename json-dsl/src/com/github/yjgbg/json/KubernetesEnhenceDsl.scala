package com.github.yjgbg.json

trait KubernetesEnhenceDsl:
  self: KubernetesDsl =>
  // 会创建一个configmap用于存放脚本文件，一个cronJob会挂载configmap用于执行任务
  // 工作目录下的.cache目录是可以缓存的，缓存的键为该cronJob
  def ammoniteCronJob(using Prefix, Interceptor)(
      name: String, // 名字
      script: String, // 脚本内容
      schedule: String, // cron表达式
      suspend: Boolean = false,
      image: String = "eclipse-temurin:17-jdk",
      scalaVersion: String = "3.2",
      ammVersion: String = "2.5.4-33-0af04a5b",
      successfulJobsHistoryLimit: Int = 3,
      failedJobsHistoryLimit: Int = 5,
      env: (String, String)*
  ): Unit = {
    val resourceName = s"$name-ammonite-cron-job"
    val scriptFileName = "script.sc"
    configMap(resourceName) {
      import java.nio.file.{Files, Path}
      data(scriptFileName -> script)
      val ammDownloadPath = "/root/.ammonite/download"
      val ammExecPath = s"$ammDownloadPath/${ammVersion}_$scalaVersion"
      val shortAmmVersion = ammVersion.split("-")(0)
      val downloadFile = s"$ammExecPath-tmp-download"
      val ammDownloadUrl =
        s"https://github.com/lihaoyi/ammonite/releases/download/${shortAmmVersion}/$scalaVersion-$ammVersion"
      // 这个amm文件是一个wrapper 脚本，会自动从github release pages 下载指定版本的ammonite
      data("amm" -> s"""
      |#!/usr/bin/env sh
      |if [ ! -x "$ammExecPath" ] ; then
      |  mkdir -p $ammDownloadPath
      |  curl --fail -L -o "$downloadFile" "$ammDownloadUrl"
      |  chmod +x "$downloadFile"
      |  mv "$downloadFile" "$ammExecPath"
      |fi
      |exec ${ammExecPath} "${"$"}@"
      """.stripMargin)
    }
    cronJob(resourceName) {
      spec {
        self.schedule(schedule)
        self.suspend(suspend)
        self.successfulJobsHistoryLimit(successfulJobsHistoryLimit)
        self.failedJobsHistoryLimit(failedJobsHistoryLimit)
        jobTemplate {
          spec { 
            template {
              spec {
                restartPolicy("Never")
                volumeHostPath("cache",s"/mnt/cronJob/cache/$name")
                val scripts = "scripts"
                volumeConfigMap(scripts,resourceName)
                volumeHostPath("coursiercache","/mnt/ammonite/coursiercache")
                volumeHostPath("ammonite","/mnt/ammonite/.ammonite/download")
                container(name,image){
                  val workspace = "/workspace"
                  workingDir(workspace)
                  imagePullPolicy("IfNotPresent")
                  command("sh",s"./../${scripts}/amm",s"/$scripts/$scriptFileName")
                  volumeMounts("coursiercache" -> "/coursiercache")
                  volumeMounts(scripts -> s"/$scripts")
                  volumeMounts("cache" -> s"$workspace/.cache")
                  volumeMounts("ammonite" -> "/root/.ammonite/download")
                  self.env("COURSIER_CACHE" -> "/coursiercache")
                  env.foreach(self.env(_))
                }
              }
            }
          }
        }
      }
    }
  }

  // 这是一个简单的web服务器，用于做静态服务，考虑给nginxConf一个默认值
  def nginxStaticHttpApplication(using Prefix, Interceptor)(
      name: String,
      image: String,
      dirPath: String, // 静态文件存储的目录，应该以斜杠结尾，会被复制到runtimeImage的同名目录中
      nginxConf: String, // nginx 配置文件，可以使用环境变量，会经过envsubst处理
      runtimeImage: String = "nginx:latest", // 运行时镜像
      replicas: Int = 1,
      tcpPort: Int = 80,
      initScript: Seq[String] = Seq(), // 脚本，会在启动时按顺序执行
      env: (String, String)* // 环境变量
  ): Unit = {
    val resourceName = s"$name-nginx-static-http-application"
    val initScriptNameAndContent = initScript.zipWithIndex.map((script, index) => s"$index.scripts.sh" -> script)
    // 声明configmap
    val nginxConfTemplate = "nginx.conf.template"
    val labels = "app" -> resourceName
    configMap(resourceName) {
      data(nginxConfTemplate -> nginxConf)
      initScriptNameAndContent.foreach(data(_))
    }
    // 创建deployment
    deployment(resourceName) {
      spec {
        self.replicas(replicas)
        selectorMatchLabels(labels)
        template {
          self.labels(labels)
          spec {
            val www = "www"
            // 声明一个空目录，用来存放静态资源文件
            volumeEmptyDir(www)
            // 声明一个存储nginxconfig的volume
            volumeConfigMap(nginxConfTemplate, resourceName, nginxConfTemplate -> nginxConfTemplate)
            initContainer("prepare-static-file", image) {
              env.foreach((k, v) => self.env(k -> v))
              command("cp", dirPath, s"/$www/")
              volumeMounts(www -> s"/$www/")
            }
            container(resourceName, runtimeImage) {
              volumeMounts(www -> dirPath)
              volumeMounts(nginxConfTemplate -> "/etc/nginx/templates/")
              env.foreach((k, v) => self.env(k -> v))
              // 如果存在初始化脚本,则声明一个用来存储初始化脚本的卷,并挂载初始化脚本
              if (!initScriptNameAndContent.isEmpty) {
                volumeConfigMap(
                  "init-scripts",
                  resourceName,
                  initScriptNameAndContent.map((name, _) => name -> name): _*
                )
                volumeMounts("init-scripts" -> "/docker-entrypoint.d/40-custom/")
              }
            }
          }
        }
      }
    }
    service(resourceName) {
      spec {
        selectorMatchLabels(labels)
        tcpPorts(tcpPort -> tcpPort)
      }
    }
  }

  def nacosConfig(using Prefix, Interceptor)(
      host: String,
      username: String,
      password: String,
      namespace: String,
      group: String,
      name: String,
      value: String,
      schedule: String = "1/3 * * * *"
  ) = ammoniteCronJob(
    name = name +"-nacos-config",
    script = raw"""
        |// 登录
        |val loginResponse = requests.post(s"$host/nacos/v1/auth/login?username=$username&password=$password").text()
        |val accessToken = ujson.read(loginResponse)("accessToken").str
        |// 如果命名空间不存在，创建命名空间
        |val namespaceListResponse = requests.get(s"$host/nacos/v1/console/namespaces?accessToken=${"$"}accessToken").text()
        |val exist = ujson.read(namespaceListResponse)("data").arr
        |  .map(_("namespace").str).find(_ == ${namespace}).isDefined
        |if(!exist) requests.post(s"$host/nacos/v1/console/namespaces?accessToken=${"$"}accessToken&customNamespaceId=&namespaceName=$namespace&namespaceDesc=")
        |// 将data目录下所有文件写入到目标nacos的目标命名空间
        |requests.post(s"$host/nacos/v1/cs/configs?accessToken=${"$"}accessToken",data = Seq(
        |  "tenant" -> "$namespace",
        |  "dataId" -> "$name",
        |  "group" -> "$group",
        |//  "type" -> p.ext,
        |  "content" -> sys.env("value")
        |))
        """.stripMargin,
    schedule = schedule,
    env = "value" -> value
  )
