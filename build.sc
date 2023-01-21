import mill._
import mill.scalalib._
import mill.scalajslib.ScalaJSModule
import publish._

object `json-dsl` extends ScalaModule with PublishModule {
  override def scalaVersion: T[String] = "3.2.1"
  // override def sonatypeUri = "https://nexus3.hypers.cc/repository/orca"
  // override def publishVersion = "1.3"

  override def publishVersion = "1.3-SNAPSHOT"

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
  val circeVersion = "0.14.1"
  override def ivyDeps = Agg(
    ivy"io.circe::circe-core:$circeVersion",
    ivy"io.circe::circe-generic:$circeVersion",
    ivy"io.circe::circe-parser:$circeVersion",
    ivy"io.circe::circe-yaml:$circeVersion"
  )
  // 发布命令：./mill project_name.publish --sonatypeCreds name:password --release false --signed false
}
object sjsexample0 extends ScalaModule {
  override def scalaVersion: T[String] = "3.2.1"
  def scalaJSVersion: T[String] = "1.12.0"
  // override def ivyDeps = Agg(
  //   ivy"com.yang-bo::html::3.0.0-M0"
  // )
}
object sjsexample extends ScalaJSModule {
  override def scalaVersion: T[String] = "3.2.1"
  override def scalaJSVersion: T[String] = "1.12.0"
  override def ivyDeps = Agg(
    ivy"com.yang-bo::html::3.0.0-M0"
  )
}