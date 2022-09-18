import mill._
import mill.scalalib._
import coursier.Repository
import mill.define.Task
import coursier.maven.MavenRepository
import publish._
import mill.scalajslib.{ScalaJSModule,api}
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

object `scalajs-electron` extends StdScalaModule with ScalaJSModule {
  override def scalaJSVersion = "1.10.1"
  override def ivyDeps = Agg(
    ivy"io.indigoengine::tyrian-io::0.5.1",
    ivy"org.scala-js::scalajs-dom::2.3.0"
  )
  def dev() = T.command {
    compile()
    val electronPath = millModuleBasePath.value / "electron"
    os.walk(electronPath).foreach(file => os.write(
      target = file.segments.drop(electronPath.segmentCount).foldLeft(T.dest)(_ / _),
      data = os.read(file)
        .replaceAll("\\$OPT_JS", fastOpt().path.toString()),
      createFolders = true)
    )
    os.proc("open", "index.html")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    println(millModuleBasePath)
    os.proc("./mill","-w",s"${millModuleBasePath.value.last}.fastOpt")
      .call(cwd = millOuterCtx.millSourcePath,stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
  }
  def start() = T.command {
    compile()
    val electronPath = millModuleBasePath.value / "electron"
    os.walk(electronPath).foreach(file => os.write(
      target = file.segments.drop(electronPath.segmentCount).foldLeft(T.dest)(_ / _),
      data = os.read(file)
        .replaceAll("\\$OPT_JS", fastOpt().path.toString()),
      createFolders = true)
    )
    os.proc("npm", "install")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npm", "run", "start")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
  }
  def pkg() = T.command {
    compile()
    os.copy(from = fullOpt().path, to = T.dest / "fullOpt.js")
    val electronPath =
      millModuleBasePath.value / "electron"
    os.walk(electronPath).foreach(file => os.write(
      target = file.segments.drop(electronPath.segmentCount).foldLeft(T.dest)(_ / _),
      data = os.read(file)
        .replaceAll("\\$OPT_JS", "./fullOpt.js"),
      createFolders = true)
    )
    os.proc("npm", "install")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npm", "run", "package")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
  }
  def make() = T.command {
    compile()
    os.copy(from = fullOpt().path,to = T.dest / "fullOpt.js")
    val electronPath =
      millModuleBasePath.value / "electron"
    os.walk(electronPath).foreach(file => os.write(
        target = file.segments.drop(electronPath.segmentCount).foldLeft(T.dest)(_ / _),
        data = os.read(file)
          .replaceAll("\\$OPT_JS","./fullOpt.js"),
        createFolders = true)
    )
    os.proc("npm","install")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npm", "run","make")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
  }
}