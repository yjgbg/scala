package com.github.yjgbg.scala.k8s.support

import scopt.{OParser, OParserBuilder}
case class Config(
                   `git-revision`:String, 
                   version:String, 
                   namespace:String = "default"
                 )
object Config:
  val parser: OParser[Unit, Config] = {
    val builder: OParserBuilder[Config] = OParser.builder[Config]
    import builder.*
    OParser.sequence(
      programName("has-k8s"),
      head("has-k8s", "1.0"),
      // option -f, --foo
      opt[String]( "git-revision")
        .action((value, c) => c.copy(`git-revision` = value))
        .text("代码的git版本号"),
      opt[String]("namespace")
        .action((v,c) => c.copy(namespace = v))
        .text("命名空间"),
      opt[String]("version")
        .action((v, c) => c.copy(version = v))
        .text("版本")
    )
  }
