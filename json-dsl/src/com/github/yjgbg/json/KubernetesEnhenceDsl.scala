package com.github.yjgbg.json

trait KubernetesEnhenceDsl:
  self: KubernetesDsl =>
  def state(using interceptor:Interceptor,prefix:Prefix)
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
