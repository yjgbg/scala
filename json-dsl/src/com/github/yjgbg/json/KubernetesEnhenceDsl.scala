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
      image: String = "reg2.hypers.cc/dockerhub/library/yjgbg/ammonite:2.5.4-3.2",
      env:(String,String)*
  ): Unit = {
    val configMapName = s"cronjob-$name-script"
    val scriptFileName = "script.sc"
    configMap(configMapName) {
      import java.nio.file.{Files,Path}
      data(scriptFileName -> Files.readString(Path.of(scriptPath)))
    }
    cronJob(name) {
      spec {
        self.schedule(schedule)
        self.suspend(suspend)
        jobTemplate {
          spec { 
            template {
              spec {
                restartPolicy("Never")
                volumeHostPath("cache",s"/mnt/cronJob/cache/$name")
                volumeConfigMap("scripts",configMapName)
                volumeHostPath("coursiercache","/mnt/ammonite/coursiercache")
                container(name,image){
                  val workspace = "/workspace"
                  workingDir(workspace)
                  imagePullPolicy("IfNotPresent")
                  command("sh","-c",s"amm /script/$scriptFileName")
                  volumeMounts("coursiercache" -> "/coursiercache")
                  volumeMounts("scripts" -> workspace)
                  volumeMounts("cache" -> s"$workspace/.cache")
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
