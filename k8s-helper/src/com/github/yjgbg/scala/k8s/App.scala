package com.github.yjgbg.scala.k8s

import App.args
import support.{Config, JsonDsl, K8sResource, WebApp}
import scopt.OParser
import com.softwaremill.macwire.*
object App:
  private var args: Seq[String] = Seq()
  class Init()
  lazy val init: Init = wire[Init]
  private def cfg(init: Init): Config = OParser.parse(Config.parser, args, Config(null, null, null)).get
  lazy val config: Config = wireWith(cfg _)
  lazy val set: Set[K8sResource] = wireSet[K8sResource]
  @main def main(args:String*): Unit = {
    Chart(values)
    App.args = args
    set.flatMap(it => it.all).foreach(it => JsonDsl.write(s"out/k8s/${it.name}.json",it.json))
  }
