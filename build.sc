import $ivy.`com.lihaoyi::mill-contrib-scalapblib:$MILL_VERSION`

import mill._
import mill.scalalib._
import coursier.Repository
import mill.define.Task
import coursier.maven.MavenRepository
import publish._
import mill.scalajslib.ScalaJSModule
import contrib.scalapblib._
trait StdScalaModule extends ScalaModule with PublishModule {
  override def scalaVersion: T[String] = "3.2.0"
  // override def sonatypeUri = "https://nexus3.hypers.cc/repository/orca"
  // override def publishVersion = "0.7"

  override def publishVersion = "0.7-SNAPSHOT"

  override def pomSettings = PomSettings(
    description = millModuleBasePath.value.last,
    // organization = "com.hypers.weicl",
    organization = "com.github.yjgbg",
    url = "https://github.com/yjgbg/scala",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("yjgbg", "scala"),
    developers = Seq(
      Developer("yjgbg", "Yu Jgbg", "https://github.com/yjgbg")
    )
  )
  // 发布命令：./mill project_name.publish --sonatypeCreds name:password --release false --signed false
}
val circeVersion = "0.14.1"
object `json-dsl` extends StdScalaModule {
  override def ivyDeps = Agg(
    ivy"io.circe::circe-core:$circeVersion",
    ivy"io.circe::circe-generic:$circeVersion",
    ivy"io.circe::circe-parser:$circeVersion",
    ivy"io.circe::circe-yaml:$circeVersion"
  )
}

object `api-definition` extends StdScalaModule {
  override def ivyDeps = Agg(
    ivy"com.softwaremill.sttp.tapir::tapir-openapi-docs:1.1.0",
    ivy"com.softwaremill.sttp.tapir::tapir-json-circe:1.1.0",
    ivy"com.softwaremill.sttp.tapir::tapir-openapi-circe-yaml:1.0.0-M9",
    ivy"eu.timepit::refined:0.10.1"
  )
}