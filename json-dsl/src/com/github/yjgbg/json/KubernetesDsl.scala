package com.github.yjgbg.json

object KubernetesDsl extends KubernetesDsl
trait KubernetesDsl extends JsonDsl:
  type Namespace = String
  val _apiVersion = "apiVersion"
  val _appV1 = "app/v1"
  val _kind = "kind"
  val _metadata = "metadata"
  val _namespace = "namespace"
  val _name = "name"
  val _spec = "spec"
  val _selector = "selector"
  def _matchLabels(using Scope)(seq: (String, String)*):Unit = "matchLabels" ++= {seq.foreach { (k, v) => k.:=(v) }}
  val _template = "template"
  val _container = "container"
  val _image = "image"
  def _env(using Scope)(seq: (String, String)*): Unit = seq.foreach { (k, v) =>"env" ++= {"key" := k; "value" := v}}
  val _type = "type"
  def _annotations(using Scope)(seq: (String, String)*): Unit = "annotations" ++= {seq.foreach { (k, v) => k.:=(v) }}
  def _labels(using Scope)(seq: (String, String)*): Unit = "labels" ++= {seq.foreach { (k, v) => k := v }}
  def _commands(using Scope)(strings: String*): Unit = strings.foreach("commands" += _)
  def _args(using Scope)(strings: String*): Unit = strings.foreach("args" += _)
  def namespace(ns:String)(closure:Option[Namespace] ?=> Unit) = {
    closure(using Some(ns))
  }
  def deployment(using ns:Option[Namespace])(name: String)(closure:Scope ?=> Unit): Unit =
    writeYaml(s"k8s-out-$nsValue-$name-deployment.yaml"){
      _kind := "Deployment"
      _metadata ::= {
        _name := name
        _namespace := nsValue
        _labels("app" -> name)
      }
      _spec ::= {
        _selector ::= {
          _matchLabels("app" -> name)
        }
      }
      closure(using summon[Scope])
    }
  def service(using ns:Option[Namespace])(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"k8s-out-$nsValue-$name-service.yaml"){
      _kind := "Service"
      _metadata ::= {
        _name := name
        _namespace := nsValue
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def configMap(using ns:Option[Namespace])(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"k8s-out-$nsValue-$name-config-map.yaml"){
      _kind := "ConfigMap"
      _metadata ::= {
        _name := name
        _namespace := nsValue
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def secrets(using ns:Option[Namespace])(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"k8s-out-$nsValue-$name-secrets.yaml"){
      _kind := "Secret"
      _metadata ::= {
        _name := name
        _namespace := nsValue
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def cronJob(using ns:Option[Namespace])(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"k8s-out-$nsValue-$name-cron-job.yaml"){
      _kind := "CronJob"
      _metadata ::= {
        _name := name
        _namespace := nsValue
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def statefulSet(using ns:Option[Namespace])(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"k8s-out-$nsValue-$name-stateful-set.yaml"){
      _kind := "StatefulSet"
      _metadata ::= {
        _name := name
        _namespace := nsValue
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def daemonSet(using ns:Option[Namespace])(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"k8s-out-$nsValue-$name-daemon-set.yaml"){
      _kind := "DaemonSet"
      _metadata ::= {
        _name := name
        _namespace := nsValue
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def persistentVolume(using ns:Option[Namespace])(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"k8s-out-$nsValue-$name-persistence-volume.yaml"){
      _kind := "PersistentVolume"
      _metadata ::= {
        _name := name
        _namespace := nsValue
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def persistentVolumeClaim(using ns:Option[Namespace])(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"k8s-out-$nsValue-$name-persistence-volume-claim.yaml"){
      _kind := "PersistentVolumeClaim"
      _metadata ::= {
        _name := name
        _namespace := nsValue
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  private def nsValue(using ns:Option[Namespace]):String = ns.getOrElse("default")