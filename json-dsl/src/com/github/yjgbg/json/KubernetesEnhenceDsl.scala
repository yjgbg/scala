package com.github.yjgbg.json

trait KubernetesEnhenceDsl:
  self: KubernetesDsl =>
  opaque type UtilsImage = String
  given UtilsImage = "alpine:latest"
  def utilsImage(image:String):UtilsImage = image
  def simplePVC(using interceptor:Interceptor,prefix:Prefix)
  (name:String,size:Long = 5,storageClass:String|Null = null) = // 创建一个pvc
    persistentVolumeClaim(name) {
      spec {
        accessModes("ReadWriteOnce")
        if storageClass!=null then storageClassName(storageClass)
        resources {
          storage(size)
        }
      }
    }

  def volumeFromImage(using PodScope >> SpecScope)(
    name:String,
    image:String,
    path:String):Unit = { 
      volumeEmptyDir(name)
      initContainer(name,image) {
        volumeMounts(name -> s"/tmp/vol")
        command("sh","-c",s"cp -r $path /tmp/vol")
      }
    }
  def proxy(using PodScope >> SpecScope)(
    ip:String,
    port:Int,
    localPort:Int|Null = null,
    image:String = "marcnuri/port-forward"
  )(closure: PodScope >> SpecScope >> ConfigableScope ?=> Unit) = {
    val localPort0 = (if localPort != null then localPort else port).toString()
    container(s"proxy-$localPort0",image) {
      env(
        "REMOTE_HOST" -> ip,
        "REMOTE_PORT" -> port.toString(),
        "LOCAL_PORT" -> localPort0
      )
      closure.apply
    }
  }
  def volumeFromLiterialText(using (PodScope >> SpecScope),UtilsImage)(name:String,files:(String,String)*): Unit = {
    volumeEmptyDir(name)
    initContainer(name,summon[UtilsImage]) {
      val indexAndKeyAndValues = files.distinctBy(_._1)
        .zipWithIndex.map((k,v) => (v,k))
        .map((k,v) => ("variable_"+k.toString(),v))
      imagePullPolicy("IfNotPresent")
      env(indexAndKeyAndValues.map((k,v) => (k,v._2)):_*)
      volumeMounts(name -> "/literial")
      val cmds = for ((k,v) <- indexAndKeyAndValues) yield 
        s"""echo "${"$"}{${k}}" > /literial/${v._1}"""
      command("sh","-c",cmds.mkString("\n"))
    }
  }

  def prepareAmmonite(using Prefix,Interceptor)(ammoniteSize:Long = 5,coursierSize:Long = 5,storageClass:String|Null):Unit = {
    simplePVC("ammonite-cache",ammoniteSize,storageClass)
    simplePVC("coursier-cache",coursierSize,storageClass)
  }
  def ammonite(using PodScope >> SpecScope,UtilsImage)
  (
    name:String,
    script:String,
    ammVersion:String = "2.5.5",
    scalaVersion:String = "3.2",
    image:String = "eclipse-temurin:17-jdk"
  )(closure : PodScope >> SpecScope >> ContainerScope ?=> Unit ) : Unit = {
    val ammDownloadPath = "/root/.ammonite/download"
    val ammExecPath = s"$ammDownloadPath/${ammVersion}_$scalaVersion"
    val downloadFile = s"$ammExecPath-tmp-download"
    val ammDownloadUrl =
      s"https://github.com/lihaoyi/ammonite/releases/download/${ammVersion.split("-")(0)}/$scalaVersion-$ammVersion"
    volumeFromLiterialText("scripts",
      //这个amm文件是一个wrapper 脚本，会自动从github release pages 下载指定版本的ammonite
      "amm" -> s"""
        |#!/usr/bin/env sh
        |set -e
        |if [ ! -x "$ammExecPath" ] ; then
        |  mkdir -p $ammDownloadPath
        |  curl --fail -L -o "$downloadFile" "$ammDownloadUrl"
        |  chmod +x "$downloadFile"
        |  mv "$downloadFile" "$ammExecPath"
        |fi
        |exec ${ammExecPath} "${"$"}@"
        |""".stripMargin.stripLeading().stripTrailing(),
      "script.sc" ->  script
    )
    volumePVC("coursier-cache")
    volumePVC("ammonite-cache")
    container(name,image) {
      volumeMounts("scripts" -> "/workspace")
      volumeMounts("coursier-cache" -> "/coursiercache")
      volumeMounts("ammonite-cache" -> "/root/.ammonite/download")
      env("COURSIER_CACHE" -> "/coursiercache")
      command("sh","-c",s"""
        |chmod -R a+x /workspace/amm
        |/workspace/amm /workspace/script.sc
        |""".stripMargin.stripLeading().stripTrailing())
      closure.apply
    }
  }