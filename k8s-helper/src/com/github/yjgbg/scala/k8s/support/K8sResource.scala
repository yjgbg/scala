package com.github.yjgbg.scala.k8s.support

import JsonDsl.{JsonNode, Scope}
trait K8sResource(name:String,json:JsonNode)
object K8sResource:
  def apply(name:String)(closure:Scope?=>Unit):K8sResource = new K8sResource(name,JsonDsl.json(closure))
