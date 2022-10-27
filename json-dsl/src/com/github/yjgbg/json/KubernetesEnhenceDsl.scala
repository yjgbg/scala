package com.github.yjgbg.json

trait KubernetesEnhenceDsl:
  self: KubernetesDsl =>

  /**
    * 工作目录下的.cache目录是可以缓存的，缓存的键为该cronJob名字
    *
    * @param name 名字
    * @param schedule cron表达式
    * @param script 脚本内容
    * @param suspend 是否停止
    * @param image 镜像名，默认为busybox
    * @param successfulJobsHistoryLimit 保存的成功job数量，默认3
    * @param failedJobsHistoryLimit 保存的失败job数量，默认1
    */
  def shellCronJob(using Prefix,Interceptor)(
    name: String,
    schedule: String,
    script: String,
    suspend: Boolean = false,
    image: String = "busybox",
    successfulJobsHistoryLimit: Int = 3,
    failedJobsHistoryLimit: Int = 1,
  ):Unit = {
    val resourceName = s"$name-shell-cron-job"
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
                volumeHostPath("cache",s"/mnt/shellCronJob/cache/$name")
                container(resourceName,image) {
                  val workspace = "/workspace"
                  workingDir(workspace)
                  imagePullPolicy("IfNotPresent")
                  command("sh","-c",script)
                  volumeMounts("cache" -> s"$workspace/.cache")
                }
              }
            }
          }
        }
      }
    }
  }

  private def ammoniteJob(ammVersion:String,scalaVersion:String,cacheKey:String = null,
                          script:String,image:String,env:Seq[(String,String)],
                          initImage:String = "alpine:latest"):JobScope ?=> Unit = {
    spec { 
      template {
        spec {
          val ammDownloadPath = "/root/.ammonite/download"
          val ammExecPath = s"$ammDownloadPath/${ammVersion}_$scalaVersion"
          val downloadFile = s"$ammExecPath-tmp-download"
          val ammDownloadUrl =
            s"https://github.com/lihaoyi/ammonite/releases/download/${ammVersion.split("-")(0)}/$scalaVersion-$ammVersion"
          //这个amm文件是一个wrapper 脚本，会自动从github release pages 下载指定版本的ammonite
          val amm = raw"""
            |#!/usr/bin/env sh
            |if [ ! -x "$ammExecPath" ] ; then
            |  mkdir -p $ammDownloadPath
            |  curl --fail -L -o "$downloadFile" "$ammDownloadUrl"
            |  chmod +x "$downloadFile"
            |  mv "$downloadFile" "$ammExecPath"
            |fi
            |exec ${ammExecPath} "${"$"}@"
            |""".stripMargin.stripLeading.stripTrailing
          restartPolicy("Never")
          if (cacheKey!=null) volumeHostPath("cache",cacheKey)
          val scripts = "scripts"
          volumeEmptyDir(scripts)
          volumeHostPath("coursiercache","/mnt/ammonite/coursiercache")
          volumeHostPath("ammonite","/mnt/ammonite/.ammonite/download")
          initContainer("init",initImage) {
            imagePullPolicy("IfNotPresent")
            volumeMounts(scripts -> s"/$scripts")
            self.env("SCRIPT" -> script)
            self.env("AMM" -> amm)
            command("sh","-c", raw"""
            |echo "${"$"}{SCRIPT}" > /$scripts/script.sc
            |echo "${"$"}{AMM} > /$scripts/amm.sc"
            |""".stripMargin.stripLeading.stripTrailing)
          }
          container("work",image){
            val workspace = "/workspace"
            workingDir(workspace)
            imagePullPolicy("IfNotPresent")
            volumeMounts("coursiercache" -> "/coursiercache")
            volumeMounts("ammonite" -> "/root/.ammonite/download")
            self.env("COURSIER_CACHE" -> "/coursiercache")
            if(cacheKey!=null) volumeMounts("cache" -> s"$workspace/.cache")
            volumeMounts(scripts -> s"$workspace/$scripts")
            command("sh","-c",s"./${scripts}/amm /$scripts/script.sc")
          }
        }
      }
    }
  }
  /**
    * 基于ammonite和cronjob的cronJob资源
    * 会创建一个configmap用于存放脚本文件，一个cronJob会挂载configmap用于执行任务
    * 工作目录下的.cache目录是可以缓存的，缓存的键为该cronJob
    * 有着自带的系统缓存，不会重复下载ammonite二进制文件，也不会反复在互联网拉取依赖包
    *
    * @param name 名称
    * @param script 脚本内容
    * @param schedule 执行周期
    * @param suspend 是否停止
    * @param image 镜像名，默认为jdk17，取决于你的脚本期待的运行环境
    * @param scalaVersion scala版本，默认3.2，取决于你的脚本期待的运行环境
    * @param ammVersion ammonite版本，默认2.5.4，取决于你的脚本期待的运行环境
    * @param successfulJobsHistoryLimit 保存的成功job数量，默认3
    * @param failedJobsHistoryLimit 保存的失败job数量，默认1
    * @param env 环境变量
    */
  def ammoniteCronJob(using Prefix, Interceptor)(
      name: String, // 名字
      schedule: String, // cron表达式
      script: String, // 脚本内容
      suspend: Boolean = false,
      image: String = "eclipse-temurin:17-jdk",
      scalaVersion: String,
      ammVersion: String,
      successfulJobsHistoryLimit: Int = 3,
      failedJobsHistoryLimit: Int = 1,
      env: (String, String)*
  ): Unit = {
    val resourceName = s"$name-ammonite-cron-job"
    cronJob(resourceName) {
      spec {
        self.schedule(schedule)
        self.suspend(suspend)
        self.successfulJobsHistoryLimit(successfulJobsHistoryLimit)
        self.failedJobsHistoryLimit(failedJobsHistoryLimit)
        jobTemplate {
          ammoniteJob(ammVersion,scalaVersion,s"/mnt/cronJob/cache/$name",script,image,env,"alpine:latest")
        }
      }
    }
  }
  /**
    * 端口映射服务，会创建一个单独的pod，用于映射端口，用于开发时在本地访问不到环境中的各种基础服务时使用
    *
    * @param name pod名称
    * @param ip 要映射的目标的ip
    * @param local2Remote 端口号与目标服务端口号映射关系
    */
  def portForward(using Prefix, Interceptor)(name:String,ip:String,local2Remote:(Int,Int)*): Unit = 
      pod(name) {
        spec {
          for ((localPort,remotePort) <- local2Remote) 
            container(localPort.toString,"marcnuri/port-forward") {
              imagePullPolicy("IfNotPresent")
              env(
                "REMOTE_HOST" -> ip,
                "REMOTE_PORT" -> remotePort.toString(),
                "LOCAL_PORT" -> localPort.toString()
              )
            }
        }
      }

  /**
    * 简单http服务器，可以将指定镜像中的制定目录下的文件以http服务暴露出去
    *
    * @param name 资源名称
    * @param image 镜像，所要暴露的文件所在的镜像
    * @param dirPath 目录，所要暴露的文件目录在镜像中的path
    * @param runtimeImage 运行时镜像，这个是nginx，不建议更改，
    * 除非你所在的网络访问不到dockerhub无法拉取nginx镜像，可以改为你的内网docker registry以拉取镜像
    * @param init 初始化脚本，支持写一系列初始化脚本，会在容器启动的时候执行
    */
  def simpleStaticFileHttpServer(using Prefix,Interceptor)(
    name:String,
    image:String,
    dirPath:String,
    runtimeImage:String = "nginx:alpine",
    init:String*
  ): Unit = {
    val resourceName = s"$name-simple-static-file-http-server"
    val labels = "app" -> resourceName
    if (!init.isEmpty) {
      configMap(resourceName) {
        for ((script,index) <- init.zipWithIndex)
          data(s"${index}.script.sh" -> script)
      }
    }
    deployment(resourceName) {
      spec{
        replicas(1)
        selectorMatchLabels(labels)
        template{
          self.labels(labels)
          spec {
            val files = "files"
            volumeEmptyDir(files)
            if(!init.isEmpty) {
              volumeConfigMap("script" ,resourceName)
            }
            initContainer("prepare-file",image) {
              imagePullPolicy("IfNotPresent")
              volumeMounts(files -> s"/files")
              command("cp","-r", dirPath, s"/files/html")
            }
            container("app",runtimeImage) {
              imagePullPolicy("IfNotPresent")
              volumeMounts(files -> "/usr/share/nginx")
              if (!init.isEmpty) {
                volumeMounts("script" -> "/docker-entrypoint.d/40-custom/")
              }
            }
          }
        }
      }
    }
    service(resourceName) {
      spec {
        selector(labels)
        tcpPorts(80 -> 80)
      } 
    }
  }

  /**
    *
    * @param name 名字
    * @param conf 配置文件内容
    * @param image 镜像tag
    * @param initImage  如果你的网络无法拉取到该镜像，那么覆盖这个参数，否则不应该改动这个参数
    * @param ports 端口号
    */
  def nginxServer(using Prefix, Interceptor)(
    name:String,
    conf:String,
    image:String = "nginx:alpine",
    initImage:String = "alpine:latest",
    ports:Seq[Int] = Seq(80)
  ) : Unit = {
    val resourceName = s"$name-nginx-server"
    val labels = "app" -> resourceName
    deployment(resourceName) {
      spec {
        selectorMatchLabels(labels)
        template{
          self.labels(labels)
          spec {
            volumeEmptyDir("config")
            initContainer("echo",initImage) {
              imagePullPolicy("IfNotPresent")
              volumeMounts("config" -> "/config")
              env("FILE" -> conf)
              command("sh","-c", """echo "${FILE}" > /config/nginx.conf""")
            }
            container("container",image) {
              imagePullPolicy("IfNotPresent")
              volumeMounts("config" -> "/etc/nginx/conf.d/")
            }
          }
        }
      }
    }
    service(resourceName) {
      spec {
        selector(labels)
        ports.foreach(it => tcpPorts(it -> it))
      }
    }
  }

  /**
    * nacos中的一条配置文件
    * 会创建一个ammoniteCronJob资源，周期性地将数据复写到nacos中，防止被更改
    *
    * @param host nacos访问地址
    * @param username nacos用户名
    * @param password nacos密码
    * @param namespace nacos中的命名空间 
    * @param group nacos中的group
    * @param name nacos中的dataId
    * @param value 文件的值
    * @param schedule 同步时间
    */
  def nacosConfig(using Prefix, Interceptor)(
      host: String,
      username: String,
      password: String,
      namespace: String,
      group: String,
      name: String,
      value: String,
      `type`:String,
  ) = job(name +"-nacos-config"){
    val script = raw"""
        |// 登录
        |val loginResponse = requests.post(s"$host/nacos/v1/auth/login?username=$username&password=$password").text()
        |val accessToken = ujson.read(loginResponse)("accessToken").str
        |// 如果命名空间不存在，创建命名空间
        |val namespaceListResponse = requests.get(s"$host/nacos/v1/console/namespaces?accessToken=${"$"}accessToken").text()
        |val exist = ujson.read(namespaceListResponse)("data").arr
        |  .map(_("namespace").str).find(_ == ${namespace}).isDefined
        |if(!exist) requests.post(url = s"$host/nacos/v1/console/namespaces",params = Map(
        |  "accessToken" -> accessToken,
        |  "namespaceName" -> "$namespace"
        |)
        |// 将data目录下所有文件写入到目标nacos的目标命名空间
        |requests.post(url = s"$host/nacos/v1/cs/configs",
        |  params = Map("accessToken" -> accessToken),
        |  data = Seq(
        |    "tenant" -> "$namespace",
        |    "dataId" -> "$name",
        |    "group" -> "$group",
        |    "type" -> "${`type`}",
        |    "content" -> sys.env("value")
        |))
        |""".stripMargin.stripLeading.stripTrailing
    ammoniteJob("2.5.4-33-0af04a5b","3.2",null,script,"eclipse-temurin:17-jdk",Seq("value" -> value))
  }
