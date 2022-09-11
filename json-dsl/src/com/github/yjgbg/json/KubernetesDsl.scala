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
  def _labels(using Scope)(seq: (String, String)*): Unit = "labels" ++= {seq.foreach { (k, v) => k := v }}
  def _commands(using Scope)(strings: String*): Unit = strings.foreach("commands" += _)
  def _args(using Scope)(strings: String*): Unit = strings.foreach("args" += _)
  def deployment(name:String)(closure:Scope ?=> Unit): Unit =
    writeYaml(s"$name-deployment.yaml"){
      closure(using summon[Scope])
      "kind" := "Deployment"
    }
  def service(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"$name-service.yaml"){
      closure(using summon[Scope])
      "kind" := "Service"
    }
  def configMap(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"$name-config-map.yaml"){
      closure(using summon[Scope])
      "kind":= "ConfigMap"
    }
  def secrets(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"$name-secrets.yaml"){
      closure(using summon[Scope])
      "kind" := "Secret"
    }
  def cronJob(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"$name-cron-job.yaml"){
      closure(using summon[Scope])
      "kind" := "CronJob"
    }
  def statefulSet(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"$name-stateful-set.yaml"){
      closure(using summon[Scope])
      "kind" := "StatefulSet"
    }
  def daemonSet(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"$name-daemon-set.yaml"){
      closure(using summon[Scope])
      "kind" := "DaemonSet"
    }
  def persistentVolume(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"$name-persistence-volume.yaml"){
      closure(using summon[Scope])
      "kind" := "PersistentVolume"
    }
  def persistentVolumeClaim(name: String)(closure: Scope ?=> Unit): Unit =
    writeYaml(s"$name-persistence-volume-claim.yaml"){
      closure(using summon[Scope])
      "kind" := "PersistentVolumeClaim"
    }