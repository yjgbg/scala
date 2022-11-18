package com.github.yjgbg.json

trait KubernetesEnhenceDsl:
  self: KubernetesDsl =>
  def simplePVC(using interceptor:Interceptor,prefix:Prefix)
  (name:String,size:Long = 5,storageClass:String = null) = // 创建一个pvc
    persistenceVolumeClaim(name) {
      spec {
        accessModes("ReadWriteMany")
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
        volumeMounts(name -> s"/tmp/$name")
        command("sh","-c",s"cp -r $path /tmp/$name")
      }
    }
  def volumeFromLiterial(using PodScope >> SpecScope)(
    name:String,
    image:String = "alpine:latest",
    files:Map[String,String]):Unit = {
      volumeEmptyDir(name)
      initContainer(name,image) {
        env(files.toSeq:_*)
        volumeMounts(name -> "/literial")
        val cmds = for ((k,v) <- files) yield 
          s"""echo "${"$"}{${k}}" > /literial/$k"""
        command("sh","-c",cmds.mkString("\n"))
      }
    }
  def delegate(using PodScope >> SpecScope)(
    ip:String,
    port:Int,
    localPort:Int|Null = null,
    image:String = "marcnuri/port-forward"
  )= container(if localPort != null then localPort.toString() else port.toString(),image) {
    imagePullPolicy("IfNotPresent")
    env(
      "REMOTE_HOST" -> ip,
      "REMOTE_PORT" -> port.toString(),
      "LOCAL_PORT" -> (if localPort != null then localPort else port).toString()
    )
  }
