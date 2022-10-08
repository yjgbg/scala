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
      s"kubectl config use-context ${name}".! // 切换上下文
      println(s"switch current context to ${name}")
      s"kubectl apply -k target/${name}".! // 应用yaml
      listCyclic(Path.of(s"./target/${name}")).forEach(it => s"kubectl apply -f ${it.toString()}".!)
      println(s"execute succeed")
      s"kubectl config use-context ${currentContext}".! // 切换上下文
      println(s"switch current context to ${currentContext}")
    }
  }
  private def listCyclic(path: Path): java.util.stream.Stream[Path] =
    if (!Files.isDirectory(path)) java.util.stream.Stream.of(path)
    else {
      Files.list(path).flatMap(listCyclic(_))
    }
