package com.github.yjgbg.scala.k8s.support

import JsonDsl.*
object K8sDsl extends K8sDsl
trait K8sDsl extends JsonDsl:
  val _apiVersion = "apiVersion"
  val _appV1 = "app/v1"
  val _kind = "kind"
  val _Deployment = "Deployment"
  val _Service = "Service"
  val _ConfigMap = "ConfigMap"
  val _Secret = "Secret"
  val _metadata = "metadata"
  val _namespace = "namespace"
  val _name = "name"
  val _spec = "spec"
  val _selector = "selector"

  def _matchLabels(using Scope)(seq: (String, String)*):Unit = "matchLabels" ++= {seq.foreach { (k, v) => k.:=(v) }}

  val _template = "template"
  val _container = "container"
  val _image = "image"

  def _env(using Scope)(seq: (String, String)*): Unit = seq.foreach { (k, v) =>
    "env" ++= {
      "key" := k
      "value" := v
    }
  }

  val _type = "type"

  def _annotations(using Scope)(seq: (String, String)*) = "annotations" ++= {seq.foreach { (k, v) => k.:=(v) }}

  def _labels(using Scope)(seq: (String, String)*) = "labels" ++= {seq.foreach { (k, v) => k.:=(v) }}

  def _commands(using Scope)(strings: String*) = strings.foreach("commands" += _)

  def _args(using Scope)(strings: String*) = strings.foreach("args" += _)
