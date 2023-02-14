package com.github.yjgbg.json

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
  private[KubernetesEnhenceDsl] case class ImagePathFile(key:String, image:String, path:String)
  private[KubernetesEnhenceDsl] case class LiteralTextFile(key:String, text:String)
  class VolumeCustomScope {
    private[KubernetesEnhenceDsl] var imagePathFileSeq:Seq[ImagePathFile] = Seq()
    private[KubernetesEnhenceDsl] var literalTextFileSeq:Seq[LiteralTextFile] = Seq()
  }
  /**
    * 创建一个自定义卷
    *
    * @param name 卷名
    * @param closure
    */
  def volumeCustom(using PodScope >> SpecScope)(name:String)(closure:VolumeCustomScope ?=> Unit) = {
    val vcs:VolumeCustomScope = VolumeCustomScope()
    closure(using vcs)
    volumeEmptyDir(name)
    
    val atomicInt = new java.util.concurrent.atomic.AtomicInteger(0)
    if (!vcs.literalTextFileSeq.isEmpty) initContainer(name+"-"+atomicInt.getAndAdd(1),summon[UtilityImage].image) {
      imagePullPolicy("IfNotPresent")
      volumeMounts(name -> "/literal")
      val variableNameAndLiteralTextFileSeq = vcs.literalTextFileSeq.distinctBy(_.key)
        .zipWithIndex.map((ltf,i) => ("variable_"+i.toString(),ltf))
      variableNameAndLiteralTextFileSeq.foreach{(vn,ltf) => env(vn -> ltf.text)}
      command("sh","-c",variableNameAndLiteralTextFileSeq
        .map{(vn,ltf) => s"""echo "${"$"}{$vn}" > /literal/${ltf.key};chmod 777 /literal/${ltf.key}"""}
        .mkString("\n"))
    }
    vcs.imagePathFileSeq
      .groupMap(_.image)(it => it.key -> it.path)
      .foreach{ (image,seq) => 
        initContainer(name+"-"+atomicInt.getAndAdd(1),image) {
          volumeMounts(name -> s"/tmp/vol")
          command("sh","-c",seq.map{ (key,path) => s"rm -rf /tmp/vol/$key\nmkdir -p /tmp/vol\ncp -a $path /tmp/vol/$key" }.mkString("\n"))
        }
      }
  }
  /**
    * 声明一个自定义文本文件，并且会赋予777权限
    *
    * @param fileName 文件在卷中的名字
    * @param content 文件文本内容
    */
  def fileLiteralText(using VolumeCustomScope)(fileName:String,content:String):Unit = 
    summon[VolumeCustomScope].literalTextFileSeq = 
      summon[VolumeCustomScope].literalTextFileSeq :+ LiteralTextFile(fileName,content)
  /**
    * 声明一个来自于镜像的文件
    *
    * @param fileName 文件名在卷中的名字
    * @param image 文件所在的镜像
    * @param path 文件在镜像中所在的目录
    */
  def fileImagePath(using VolumeCustomScope)(fileName:String,image:String,path:String):Unit =
    summon[VolumeCustomScope].imagePathFileSeq = 
      summon[VolumeCustomScope].imagePathFileSeq :+ ImagePathFile(fileName,image,path)
  /**
    *  创建一个到远程服务器的代理
    *
    * @param ip 远程服务器的ip地址
    * @param port 要代理的远程服务器的端口号
    * @param localPort 本地端口，可以不写，会用远程端口号
    * @param image 镜像，如果集群可以访问dockerhub也不建议写
    * @param closure 对容器的其他配置
    */
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
    image:String = "eclipse-temurin:latest",
    init:Boolean = false
  )(closure : PodScope >> SpecScope >> ContainerScope ?=> Unit ) : Unit = {
    volumeCustom(s"script-$name") {
      fileLiteralText("script.sc",script)
      val ammDownloadPath = "/root/.ammonite/download"
      val ammExecPath = s"$ammDownloadPath/${ammVersion}_$scalaVersion"
      val downloadFile = s"/tmp/ammonite-download"
      val ammDownloadUrl =
        s"https://github.com/lihaoyi/ammonite/releases/download/${ammVersion.split("-")(0)}/$scalaVersion-$ammVersion"
      fileLiteralText("amm",s"""
        |#!/usr/bin/env sh
        |set -e
        |if [ ! -x "$ammExecPath" ] ; then
        |  mkdir -p $ammDownloadPath
        |  curl --fail -L -o "$downloadFile" "$ammDownloadUrl"
        |  chmod +x "$downloadFile"
        |  mv "$downloadFile" "$ammExecPath"
        |fi
        |exec ${ammExecPath} "${"$"}@"
        |""".stripMargin.stripLeading().stripTrailing())
    }
    prepareAmmonite()
    if (!ammoniteInited.getOrElse(summon,false)) {
      volumePVC("coursier-cache")
      volumePVC("ammonite-cache")
      ammoniteInited = ammoniteInited + ((summon,true))
    }
    (if init then initContainer else container)(name,image){
      volumeMounts(s"script-${name}" -> "/workspace")
      volumeMounts("coursier-cache" -> "/root/.cache/coursier/v1")
      volumeMounts("ammonite-cache" -> "/root/.ammonite/download")
      command("sh","-c",s"""
        |chmod -R a+x /workspace/amm
        |/workspace/amm /workspace/script.sc
        |""".stripMargin.stripLeading().stripTrailing())
      closure.apply
    }
  }
  case class AMQPScope(var map:Map[String,String])
  opaque type VHostScope = Scope
  /**
    * 
    *
    * @param host
    * @param port 管理页面的端口，一般是15672，而不是5672
    * @param username
    * @param password
    * @param image
    * @param closure
    */
  def rabbitmqTopo(using PodScope >> SpecScope,UtilityImage)
  (username:String,password:String,host:String,port:Int = 15672,image:String = "python",init:Boolean = false)
  (closure:AMQPScope ?=> Unit):Unit = {
    val name = s"amqp-topo-$host-$port"
    val amqpScope = AMQPScope(Map())
    closure.apply(using amqpScope)
    volumeCustom(name) {
      fileLiteralText("amqp.conf",raw"""
        |[default]
        |hostname = $host
        |port = $port
        |username = $username
        |password = $password
        |""".stripMargin.stripLeading().stripTrailing())
      val cmd = amqpScope.map.zipWithIndex.map{(kv,i) => 
        fileLiteralText(s"$i.json",kv._2)
        s"./rabbitmqadmin -c amqp.conf -N default -V ${kv._1} import ${i}.json"
      }.mkString("\n")
      fileLiteralText("work.sh",raw"""
        |curl 'http://$host:$port/cli/rabbitmqadmin' > rabbitmqadmin
        |chmod -R a+x rabbitmqadmin
        |$cmd
        |""".stripMargin.stripLeading().stripTrailing())
    }
    (if init then initContainer else container)(s"amqp-topo-$host-$port",image) {
      volumeMounts(name -> "/workspace")
      workingDir("/workspace")
      command("sh","work.sh")
    }
  }
  def vHost(using AMQPScope)(name:String)(closure:VHostScope ?=> Unit):Unit = 
    summon[AMQPScope].map = summon[AMQPScope].map ++ Map(name -> json(closure).spaces2)
  opaque type QueueScope = Scope
  def queue(using VHostScope)(
    name:String,
    durable:Boolean = true,
    autoDelete: Boolean =false,
    arguments:Scope ?=> Unit = {} 
    // 对arguments的处理暂时是一个不够完美的方案。因为暂不支持值为多重数组的情况
    ):Unit = "queues" ++= {
      "name" := name
      "durable" := durable
      "auto_delete" := autoDelete
      "arguments" ::= arguments
    }
  def exchange(using VHostScope)(
    name:String,
    `type`:String= "direct",
    durable:Boolean = true,
    autoDelete:Boolean = false,
    internal:Boolean = false,
    arguments:Scope ?=> Unit = {}
    ):Unit = "exchanges" ++= {
      "name" := name
      "type" := `type`
      "durable" := durable
      "auto_delete" := autoDelete
      "internal" := internal
      "arguments" ::= arguments
    }
  def binding(using VHostScope)(
    source:String,
    routingKey:String,
    destination:String,
    destinationType:String = "queue",
    arguments: Scope ?=> Unit = {}
    ):Unit =
    "bindings" ++= {
      "source" := source
      "destination" := destination
      "destination_type" := destinationType
      "routing_key" := routingKey
      "arguments" ::= arguments
    }