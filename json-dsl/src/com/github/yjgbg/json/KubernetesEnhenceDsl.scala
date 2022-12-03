package com.github.yjgbg.json

import javax.xml.stream.events.Namespace

trait KubernetesEnhenceDsl:
  self: KubernetesDsl =>
  case class UtilityImage(var image:String)
  given UtilityImage = UtilityImage("alpine:latest")
  def utilityImage(image:String):Unit = summon[UtilityImage].image = image
  def simplePVC(using NamespaceScope)(name:String,size:Int = 5,storageClass:String|Null = null) =
    persistentVolumeClaim(name) {
      spec {
        accessModes("ReadWriteOnce")
        if storageClass!=null then storageClassName(storageClass)
        resources {
          storage(size)
        }
      }
    }
  def volumeImage(using PodScope >> SpecScope)(
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
  )(closure: PodScope >> SpecScope >> ContainerScope ?=> Unit) = {
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
  def volumeLiteralText(using (PodScope >> SpecScope), UtilityImage)(name:String, files:(String,String)*): Unit = {
    volumeEmptyDir(name)
    initContainer(name,summon[UtilityImage].image) {
      val indexAndKeyAndValues = files.distinctBy(_._1)
        .zipWithIndex.map((k,v) => (v,k))
        .map((k,v) => ("variable_"+k.toString(),v))
      imagePullPolicy("IfNotPresent")
      env(indexAndKeyAndValues.map((k,v) => (k,v._2)):_*)
      volumeMounts(name -> "/literal")
      val cmds = for ((k,v) <- indexAndKeyAndValues) yield
        s"""echo "${"$"}{${k}}" > /literal/${v._1}"""
      command("sh","-c",cmds.mkString("\n"))
    }
  }

  // 如果需要在多个命名空间部署，则需要在每个命名空间prepareAmmonite
  private var ammonitePVCInited:Map[NamespaceScope,Boolean] = Map()
  def prepareAmmonite(using NamespaceScope)(ammoniteSize:Int = 5,coursierSize:Int = 5,storageClass:String|Null = null):Unit = {
    if (!ammonitePVCInited.getOrElse(summon,false)) {
      simplePVC("ammonite-cache",ammoniteSize,storageClass)
      simplePVC("coursier-cache",coursierSize,storageClass)
      ammonitePVCInited = ammonitePVCInited + ((summon,true))
    }
  }
  private var ammoniteInited:Map[PodScope >> SpecScope,Boolean] = Map()
  def ammonite(using NamespaceScope,PodScope >> SpecScope,UtilityImage)
  (
    name:String,
    script:String,
    ammVersion:String = "2.5.5-17-df243e14",
    scalaVersion:String = "3.2",
    image:String = "eclipse-temurin:latest"
  )(closure : PodScope >> SpecScope >> ContainerScope ?=> Unit ) : Unit = {
    val ammDownloadPath = "/root/.ammonite/download"
    val ammExecPath = s"$ammDownloadPath/${ammVersion}_$scalaVersion"
    val downloadFile = s"/tmp/ammonite-download"
    val ammDownloadUrl =
      s"https://github.com/lihaoyi/ammonite/releases/download/${ammVersion.split("-")(0)}/$scalaVersion-$ammVersion"
    volumeLiteralText(s"scripts-${name}",
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
    prepareAmmonite()
    if (!ammoniteInited.getOrElse(summon,false)) {
      volumePVC("coursier-cache")
      volumePVC("ammonite-cache")
      ammoniteInited = ammoniteInited + ((summon,true))
    }
    container(name,image) {
      volumeMounts(s"scripts-${name}" -> "/workspace")
      volumeMounts("coursier-cache" -> "/root/.cache/coursier/v1")
      volumeMounts("ammonite-cache" -> "/root/.ammonite/download")
      command("sh","-c",s"""
        |chmod -R a+x /workspace/amm
        |/workspace/amm /workspace/script.sc
        |""".stripMargin.stripLeading().stripTrailing())
      closure.apply
    }
  }
  def shell(using PodScope >> SpecScope,UtilityImage)
  (name:String,script:String,image:String = "ubuntu:latest")
  (closure:PodScope >> SpecScope >> ContainerScope ?=> Unit):Unit = {
    container(name,image) {
      command("sh","-c",script)
      closure.apply
    }
  }