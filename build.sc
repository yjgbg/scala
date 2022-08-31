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

  override def scalacOptions: T[Seq[String]] = T {
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

object main extends StdScalaModule
object server extends StdScalaModule {
  override def ivyDeps: T[Agg[Dep]] = T{Agg(
    ivy"io.d11::zhttp:2.0.0-RC10",
    ivy"org.latestbit::circe-tagged-adt-codec:0.10.1",
    ivy"com.outr::lucene4s:1.11.1"
    )}
}
