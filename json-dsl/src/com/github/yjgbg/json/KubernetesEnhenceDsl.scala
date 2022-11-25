package com.github.yjgbg.json

trait KubernetesEnhenceDsl:
  self: KubernetesDsl =>
  opaque type UtilsImage = String
  given UtilsImage = "alpine:latest"
  def utilsImage(image:String):UtilsImage = image
  def simplePVC(using interceptor:Interceptor,prefix:Prefix)
  (name:String,size:Long = 5,storageClass:String = null) = // 创建一个pvc
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
        volumeMounts(name -> s"/tmp/$name")
        command("sh","-c",s"cp -r $path /tmp/$name")
      }
    }
  def proxy(using PodScope >> SpecScope)(
    ip:String,
    port:Int,
    localPort:Int|Null = null,
    image:String = "marcnuri/port-forward"
  )(closure: PodScope >> SpecScope >> ConfigableScope ?=> Unit) = {
    val localPort0 = (if localPort != null then localPort else port).toString()
    container(localPort0,image) {
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