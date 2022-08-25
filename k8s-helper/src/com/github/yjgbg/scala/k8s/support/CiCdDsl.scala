package com.github.yjgbg.scala.k8s.support

object CiCdDsl extends CiCdDsl
trait CiCdDsl extends JsonDsl:
  object args:
    lazy val gitRepo: String = sys.env("GIT_REPO")
    lazy val refs: String  = sys.env("REFS")
    lazy val branch: String  = sys.env("branch")
    lazy val gitRevision: String  = sys.env("GIT_REVISION")
    lazy val pipelineId: String  = sys.env("PIPELINE_ID")
  def pipeline(closure:Scope ?=> Unit): Unit = write("gitlab-ci.json")(closure)
  val _image = "image"
  def env(using Scope)(entry:(String,String)*):Unit = entry.foreach((k,v) => "env" ++= {
    "name" := k
    "value" := v
  })
  val _command = "command"
  val _dependsOn = "dependsOn"
  def cache(using Scope)(key:String,path:String):Unit =
    "cache" ::= {
      "key" := key
      "path" := path
    }

@main def main = {
  import CiCdDsl.*
  pipeline {
    def image(app:String) = {
      s"registry.xx.xx/xxx/$app:${args.branch}-${args.pipelineId}"
    }
    val apps = Seq("app0","app1")
    apps.foreach{ app =>
      s"fetch code $app" := {
        _image := "alpine"
        _command += s"git clone ${args.gitRepo} -b ${args.branch}"
        cache(args.pipelineId, "./*")
      }
      s"build jar $app" := {
        _image := "openjdk:17"
        _command += s"./mill ${app}.assembly"
        _dependsOn += s"fetch code $app"
        cache(args.pipelineId, s"./out/$app/assembly.dist/*")
      }
      s"build image $app" := {
        _image := "kaniko-executor"
        _dependsOn += s"build jar $app"
        env(
          "tag" -> image(app)
        )
      }
    }
    // 构建一个helm chart
    s"build chart" := {
      _image := "alpine"
      apps.foreach(app => _dependsOn += s"build image $app") // 依赖于以上打包都完成
      val chartArgs = apps.map(app => s"--$app=${image(app)}").mkString(" ")
      _command += s"mill cd.run $chartArgs"
    }
  }
}
