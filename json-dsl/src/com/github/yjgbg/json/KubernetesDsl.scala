package com.github.yjgbg.json

import io.circe.Json

object KubernetesDsl extends KubernetesDsl
trait KubernetesDsl extends JsonDsl:
  val _apiVersion = "apiVersion"
  val _appV1 = "app/v1"
  val _kind = "kind"
  val _metadata = "metadata"
  val _namespace = "namespace"
  val _name = "name"
  val _spec = "spec"
  val _selector = "selector"
  def _matchLabels(using Scope)(seq: (String, String)*):Unit = "matchLabels" ::= {seq.foreach { (k, v) => k := v }}
  val _template = "template"
  val _container = "container"
  val _image = "image"
  def _env(using Scope)(seq: (String, String)*): Unit = seq.foreach { (k, v) =>"env" ++= {"key" := k; "value" := v}}
  val _type = "type"
  def _annotations(using Scope)(seq: (String, String)*): Unit = "annotations" ++= {seq.foreach { (k, v) => k.:=(v) }}
  def _labels(using Scope)(seq: (String, String)*): Unit = "labels" ::= {seq.foreach { (k, v) => k := v }}
  def _commands(using Scope)(strings: String*): Unit = strings.foreach("commands" += _)
  def _args(using Scope)(strings: String*): Unit = strings.foreach("args" += _)
  def namespace(ns:String)(closure:Interceptor ?=> Unit) :Unit = 
    interceptor{_metadata ::= {_namespace := ns}}(closure)
  def deployment(using Prefix,Interceptor)(name: String)(closure:Scope ?=> Unit): Unit =
    interceptor{_kind := "Deployment";_metadata ::= {_name := name}}{
      writeYaml(s"deployment-$name.yaml")(closure)
    }
  def configMapFromDir(using Prefix,Interceptor)(name:String,dirPath:String = null):Unit = 
    interceptor{_kind:="ConfigMap";_metadata ::={_name := name}} {
      writeYaml(s"config-map-$name.yaml"){
        _apiVersion := "v1"
        "data" ::= {
          import java.nio.file.{Files,Paths}
          val files = Files.list(Paths.get(summon[Prefix].value + (if dirPath != null then dirPath else name)))
          files.forEach(path => {
            val k = path.getFileName().toString()
            val v = Files.readString(path)
            path.getFileName().toString() := Files.readString(path)
          })
        }
      }
    }
  def configMap(using Prefix,Interceptor)(name: String)(closure: Scope ?=> Unit): Unit =
    interceptor{_kind:="ConfigMap";_metadata::={_name:=name}} {
      writeYaml(s"config-map-$name.yaml")(closure)
    }
  def cronJob(using Prefix,Interceptor)(name: String)(closure: Scope ?=> Unit): Unit =
    interceptor{_kind:="CronJob";_metadata::={_name:=name}} {
      writeYaml(s"cron-job-$name.yaml")(closure)
    }
  def service(using Prefix,Interceptor)(name: String)(closure: Scope ?=> Unit): Unit =
    interceptor{_kind:="Service";_metadata::={_name:=name}} {
      writeYaml(s"service-$name.yaml")(closure)
    }
  def gradleSubProjectSpringBootWebApp(using Prefix,Interceptor)(name:String,replicas:Long = 1,ports:Seq[Int],image:String,jarPath:String,javaVersion:8|11|17|18|19 = 17,env:Seq[(String,String)])(closure:Scope ?=> Unit):Unit = {
    configMapFromDir(name,dirPath = s"$name/src/main/resources/") // 一个configMap
    deployment(name) { // 一个deployment
      _spec ::= {
        "replicas" := replicas
        _selector ::= {
          _matchLabels("app" -> name)
        }
        _template ::= {
          _metadata ::= {
            _labels("app" -> name)
          }
          _spec ::= {
            "restartPolicy" := "Always"
            "volumes" ++= {
              _name := "jar"
              "emptyDir" ::= {}
            }
            "initContainers" ++= {
              _image := image
              _name := "prepareJar"
              "volumeMounts" ++= {
                _name := "jar"
                "mountPath" := "/workspace/app/"
              }
              _commands("cp",jarPath,s"/workspace/app/$name.jar")
            }
            "containers" ++= {
              _image := s"eclipse-temurin:$javaVersion"
              _name := name
              _commands("java","-jar",s"/workspace/app/$name.jar")
              "volumeMounts" ++= {
                _name := "jar"
                "mountPath" := "/workspace/app/"
              }
              _env(env:_*)
            }
          }
        }
      }
    }
    service(name) { // 一个service
      _apiVersion := "v1"
      _spec ::= {
        _selector ::= {
          "app" := name
        }
        ports.foreach(port => {
          "ports" ++= {
            "port":= port.toLong
            _name := s"tcp-$port"
            "protocol" := "TCP"
            "targetPort" := port.toLong
          }
        })
      }
    }
  }
    