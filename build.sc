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

  override def repositoriesTask: Task[Seq[Repository]] = T.task {
    super.repositoriesTask() ++ Seq(
      MavenRepository("https://oss.sonatype.org/content/repositories/snapshots")
    )
  }

  override def publishVersion = "0.2-SNAPSHOT"

  override def pomSettings = PomSettings(
    description = millModuleBasePath.value.last,
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
    ivy"io.circe::circe-yaml:$circeVersion",
    ivy"org.scalikejdbc::scalikejdbc:4.0.0",
    ivy"com.softwaremill.sttp.tapir::tapir-sttp-client:1.1.1"
  )
}

object `tapir-zhttp-server` extends StdScalaModule {
  override def ivyDeps = Agg(
    ivy"com.softwaremill.sttp.tapir::tapir-zio-http-server:1.1.0",
    ivy"com.softwaremill.sttp.tapir::tapir-openapi-docs:1.1.0",
    ivy"com.softwaremill.sttp.tapir::tapir-json-circe:1.1.0",
    ivy"io.circe::circe-core:$circeVersion",
    ivy"io.circe::circe-generic:$circeVersion",
    ivy"io.circe::circe-parser:$circeVersion",
    ivy"io.circe::circe-yaml:$circeVersion",
    ivy"com.softwaremill.sttp.tapir::tapir-zio:1.1.0",
    ivy"com.softwaremill.sttp.tapir::tapir-zio-http-server:1.1.0",
    ivy"com.softwaremill.sttp.tapir::tapir-openapi-circe-yaml:1.0.0-M9",
    ivy"com.softwaremill.sttp.apispec::openapi-circe-yaml:0.2.1"
  )
}

object `protobuf-example` extends ScalaPBModule with StdScalaModule {
 override def scalaPBVersion = "0.11.11"
 override def scalaPBGrpc = true
 override def scalaPBFlatPackage = true
}