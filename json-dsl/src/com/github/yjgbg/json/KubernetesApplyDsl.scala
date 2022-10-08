package com.github.yjgbg.json

import java.nio.file.Path
import java.nio.file.Files

trait KubernetesApplyDsl:
  self: KubernetesDsl =>

  def context(name: String, apply: Boolean = false)(closure: Prefix ?=> Unit) = {
    import sys.process._
    s"rm -rf target/$name/".! // 清理掉工作区
    prefix(s"target/${name}/")(closure) // 生成文件
    if (apply) {
      val currentContext = "kubectl config get-contexts".!!.lines()
        .filter(it => it.contains("*"))
        .findAny()
        .orElseThrow()
        .split(" ")
        .filter(!_.isBlank())(1) // 获取当前context
      if (currentContext != name) s"kubectl config use-context ${name}".! // 切换上下文
      s"kubectl apply -f target/${name}".! // 应用yaml
      if (currentContext != name) s"kubectl config use-context ${currentContext}".! // 切换上下文
    }
  }
