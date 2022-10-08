package com.github.yjgbg.json

trait KubernetesEnhenceDsl:
  self: KubernetesDsl =>
  // 会创建一个configmap用于存放脚本文件，一个cronJob会挂载configmap用于执行任务
  // 工作目录下的.cache目录是可以缓存的，缓存的键为该cronJob
  def ammoniteCronJob(using Prefix, Interceptor)(
      name: String, // 名字
      scriptPath: String, // 脚本文件路径
      schedule: String, // cron表达式
      suspend: Boolean  = false,
      image: String = "eclipse-temurin:17-jdk",
      scalaVersion: String = "3.2",
      ammVersion: String = "2.5.4-33-0af04a5b",
      successfulJobsHistoryLimit:Int = 3,
      failedJobsHistoryLimit : Int = 5,
      env:(String,String)*
  ): Unit = {
    val configMapName = s"cronjob-$name-script"
    val scriptFileName = "script.sc"
    configMap(configMapName) {
      import java.nio.file.{Files,Path}
      data(scriptFileName -> Files.readString(Path.of(scriptPath)))
      val ammDownloadPath = "/root/.ammonite/download"
      val ammExecPath = s"$ammDownloadPath/${ammVersion}_$scalaVersion"
      val shortAmmVersion = ammVersion.split("-")(0)
      val downloadFile = s"$ammExecPath-tmp-download"
      val ammDownloadUrl = s"https://github.com/lihaoyi/ammonite/releases/download/${shortAmmVersion}/$scalaVersion-$ammVersion"
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
    cronJob(name) {
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
                volumeConfigMap(scripts,configMapName)
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
