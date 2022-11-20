package com.github.yjgbg.json

trait KubernetesEnhenceDsl:
  self: KubernetesDsl =>
  opaque type UtilsImage = String
  given UtilsImage = "alpine:latest"
  def utilsImage(image:String):UtilsImage = image
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
  def proxy(using PodScope >> SpecScope)(
    ip:String,
    port:Int,
    localPort:Int|Null = null,
    image:String = "marcnuri/port-forward"
  )= {
    val localPort0 = (if localPort != null then localPort else port).toString()
    container(localPort0,image) {
      imagePullPolicy("IfNotPresent")
      env(
        "REMOTE_HOST" -> ip,
        "REMOTE_PORT" -> port.toString(),
        "LOCAL_PORT" -> localPort0
      )
    }
  }
  def volumeFromLiterialText(using (PodScope >> SpecScope),UtilsImage)(name:String,files:(String,String)*): Unit = {
    volumeEmptyDir(name)
    initContainer(name,summon[UtilsImage]) {
      val variables = files.distinctBy(_._1)
      env(variables:_*)
      volumeMounts(name -> "/literial")
      val cmds = for ((k,v) <- variables) yield 
        s"""echo "${"$"}{${k}}" > /literial/$k"""
      command("sh","-c",cmds.mkString("\n"))
    }
  }