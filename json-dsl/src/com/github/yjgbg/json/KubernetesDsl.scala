package com.github.yjgbg.json

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
  def _matchLabels(using Scope)(seq: (String, String)*):Unit = "matchLabels" ++= {seq.foreach { (k, v) => k.:=(v) }}
  val _template = "template"
  val _container = "container"
  val _image = "image"
  def _env(using Scope)(seq: (String, String)*): Unit = seq.foreach { (k, v) =>"env" ++= {"key" := k; "value" := v}}
  val _type = "type"
  def _annotations(using Scope)(seq: (String, String)*): Unit = "annotations" ++= {seq.foreach { (k, v) => k.:=(v) }}
  def _labels(using Scope)(seq: (String, String)*): Unit = "labels" ::= {seq.foreach { (k, v) => k := v }}
  def _commands(using Scope)(strings: String*): Unit = strings.foreach("commands" += _)
  def _args(using Scope)(strings: String*): Unit = strings.foreach("args" += _)
  case class Dir(value:String)
  given Dir = Dir(".")
  def dir(dir:String)(closure:Dir ?=> Unit):Unit = {
    import java.nio.file.{Files,Paths}
    if (Files.notExists(Paths.get(dir))) Files.createDirectory(Paths.get(dir))
    closure(using Dir(dir))
  }
  case class Namespace(value: String)
  given Namespace = Namespace("default")
  def namespace(ns:String)(closure:Namespace ?=> Unit): Unit = closure(using Namespace(ns))
  def writeK8SResource(dir:Dir,ns:Namespace,kind:String,name:String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"${dir.value}/${ns.value}-$kind-$name.yaml")(closure)
  def deployment(using dir:Dir)(using ns:Namespace)(name: String)(closure:Scope ?=> Unit): Unit =
    writeK8SResource(dir,ns,"deployment",name){
      _kind := "Deployment"
      _metadata ::= {
        _name := name
        _namespace := ns.value
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def service(using dir:Dir)(using ns:Namespace)(name: String)(closure: Scope ?=> Unit): Unit =
    writeK8SResource(dir,ns,"service",name){
      _kind := "Service"
      _metadata ::= {
        _name := name
        _namespace := ns.value
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def configMap(using dir:Dir)(using ns:Namespace)(name: String)(closure: Scope ?=> Unit): Unit =
    writeK8SResource(dir,ns,"configMap",name){
      _kind := "ConfigMap"
      _metadata ::= {
        _name := name
        _namespace := ns.value
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def secrets(using dir:Dir)(using ns:Namespace)(name: String)(closure: Scope ?=> Unit): Unit =
    writeK8SResource(dir,ns,"secrets",name){
      _kind := "Secret"
      _metadata ::= {
        _name := name
        _namespace := ns.value
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def cronJob(using dir:Dir)(using ns:Namespace)(name: String)(closure: Scope ?=> Unit): Unit =
    writeK8SResource(dir,ns,"cronJob",name){
      _kind := "CronJob"
      _metadata ::= {
        _name := name
        _namespace := ns.value
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def statefulSet(using dir:Dir)(using ns:Namespace)(name: String)(closure: Scope ?=> Unit): Unit =
    writeK8SResource(dir,ns,"statefulSet",name) {
      _kind := "StatefulSet"
      _metadata ::= {
        _name := name
        _namespace := ns.value
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def daemonSet(using dir:Dir)(using ns:Namespace)(name: String)(closure: Scope ?=> Unit): Unit =
    writeK8SResource(dir,ns,"daemonSet",name) {
      _kind := "DaemonSet"
      _metadata ::= {
        _name := name
        _namespace := ns.value
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def persistentVolume(using dir:Dir)(using ns:Namespace)(name: String)(closure: Scope ?=> Unit): Unit =
    writeK8SResource(dir,ns,"persistentVolume",name) {
      _kind := "PersistentVolume"
      _metadata ::= {
        _name := name
        _namespace := ns.value
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }
  def persistentVolumeClaim(using dir:Dir)(using ns:Namespace)(name: String)(closure: Scope ?=> Unit): Unit =
    writeK8SResource(dir,ns,"persistentVolumeClaim",name) {
      _kind := "PersistentVolumeClaim"
      _metadata ::= {
        _name := name
        _namespace := ns.value
        _labels("app" -> name)
      }
      closure(using summon[Scope])
    }