import mill._
import mill.scalalib._
import coursier.Repository
import mill.define.Task
import coursier.maven.MavenRepository
import publish._

trait StdScalaModule extends ScalaModule {
  override def scalaVersion: T[String] = "3.1.3"

  override def repositoriesTask: Task[Seq[Repository]] = T.task {
    super.repositoriesTask() ++ Seq(
      MavenRepository("https://oss.sonatype.org/content/repositories/snapshots")
    )
  }

  override def scalacOptions: T[Seq[String]] = T {
    super.scalacOptions() ++ Seq("-Yexplicit-nulls", "-Ysafe-init")
  }
}

object `json-dsl` extends StdScalaModule with PublishModule {
  val circeVersion = "0.14.1"
  override def ivyDeps = Agg(
    ivy"io.circe::circe-core:$circeVersion",
    ivy"io.circe::circe-generic:$circeVersion",
    ivy"io.circe::circe-parser:$circeVersion",
    ivy"io.circe::circe-yaml:$circeVersion"
  )
  override def publishVersion = "0.1-SNAPSHOT"
  override def pomSettings = PomSettings(
    description = "json-dsl",
    organization = "com.github.yjgbg",
    url = "https://github.com/yjgbg/scala",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("yjgbg", "scala"),
    developers = Seq(
      Developer("yjgbg", "Yu Jgbg", "https://github.com/yjgbg")
    )
  )
  // 发布命令：./mill json-dsl.publish --sonatypeCreds name:password --release false --signed false
}

object server extends StdScalaModule {
  override def ivyDeps: T[Agg[Dep]] = T {
    Agg(
      ivy"io.d11::zhttp:2.0.0-RC10",
      ivy"com.outr::lucene4s:1.11.1",
      ivy"dev.zio::zio-kafka:2.0.0",
      ivy"dev.zio::zio-schema-json:0.2.1",
      ivy"dev.zio::zio-schema-protobuf:0.2.1",
      ivy"dev.zio::zio-redis:0.0.0+433-75ba4cbd-SNAPSHOT",
      ivy"ch.qos.logback:logback-classic:1.4.0",
//      ivy"com.github.yjgbg::json-dsl:0.1-SNAPSHOT"
//      ivy"dev.zio::zio-sql-mysql:0.0.2"
    )
  }
}
