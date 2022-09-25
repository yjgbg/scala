// Mill 0.10.7
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

  override def publishVersion = "0.1-SNAPSHOT"

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
    os.proc("npm","i","-D","electron@latest")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npm","i","--save-dev","@electron-forge/cli")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npx","electron-forge","import")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npm", "install")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npm", "run", "start")
      .call(cwd = T.dest, stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
  }
  def pkg() = T.command {
    compile()
    os.copy(from = fullOpt().path, to = T.dest / "tmp" / "fullOpt.js",createFolders = true)
    val electronPath =
      millModuleBasePath.value / "electron"
    os.walk(electronPath).foreach(file => os.write(
      target = file.segments.drop(electronPath.segmentCount).foldLeft(T.dest / "tmp")(_ / _),
      data = os.read(file)
        .replaceAll("\\$OPT_JS", "./fullOpt.js"),
      createFolders = true)
    )
    os.proc("npm", "i", "-D", "electron@latest")
      .call(cwd = T.dest / "tmp", stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npm", "i", "--save-dev", "@electron-forge/cli")
      .call(cwd = T.dest / "tmp", stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npx", "electron-forge", "import")
      .call(cwd = T.dest / "tmp", stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npm", "run", "package")
      .call(cwd = T.dest / "tmp", stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.walk(T.dest / "tmp" / "out")
      .filter(it => it.segmentCount - T.dest.segmentCount == 3)
      .foreach(p => os.copy.into(p,T.dest))
    os.remove.all(T.dest / "tmp")
  }
  def make() = T.command {
    compile()
    os.copy(from = fullOpt().path,to = T.dest / "tmp" / "fullOpt.js",createFolders = true)
    val electronPath =
      millModuleBasePath.value / "electron"
    os.walk(electronPath).foreach(file => os.write(
        target = file.segments.drop(electronPath.segmentCount).foldLeft(T.dest / "tmp")(_ / _),
        data = os.read(file)
          .replaceAll("\\$OPT_JS","./fullOpt.js"),
        createFolders = true)
    )
    os.proc("npm", "i", "-D", "electron@latest")
      .call(cwd = T.dest / "tmp", stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npm", "i", "--save-dev", "@electron-forge/cli")
      .call(cwd = T.dest / "tmp", stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npx", "electron-forge", "import")
      .call(cwd = T.dest / "tmp", stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.proc("npm", "run","make")
      .call(cwd = T.dest / "tmp", stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    os.walk(T.dest / "tmp" / "out" / "make")
      .filter(it => it.segmentCount - T.dest.segmentCount == 4)
      .foreach(p => os.copy.into(p, T.dest))
    os.remove.all(T.dest / "tmp")
  }
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