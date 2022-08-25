import mill._
import mill.scalalib._
import coursier.Repository
import mill.define.Task
import coursier.maven.MavenRepository

trait StdScalaModule extends ScalaModule {
  override def scalaVersion: T[String] = "3.1.3"

  override def repositoriesTask: Task[Seq[Repository]] = T.task {super.repositoriesTask() ++ Seq(
    MavenRepository("https://oss.sonatype.org/content/repositories/snapshots")
  )}

  override def scalaDocOptions: T[Seq[String]] = T {
    super.scalacOptions() ++ Seq("-Yexplicit-nulls", "-Ysafe-init")
  }
}

object `k8s-helper` extends StdScalaModule {
  override def compileIvyDeps: T[Agg[Dep]] = Agg(
    ivy"com.softwaremill.macwire::macros:2.5.7" // 依赖注入
  )
  override def ivyDeps: T[Agg[Dep]] = Agg(
    ivy"com.github.scopt::scopt:4.1.0" //命令行参数解析
  )
}

object sql extends StdScalaModule
object server extends StdScalaModule
