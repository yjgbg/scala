package com.github.yjgbg.json

object PipelineDsl extends PipelineDsl
trait PipelineDsl extends JsonDsl:
  object args:
    lazy val gitRepo: String ="git-repo"
    lazy val refs: String  = "refs"
    lazy val branch: String  = "branch"
    lazy val gitRevision: String  = "gir-revision"
    lazy val pipelineId: String  = "pipeline-id"
  def pipeline(closure:Scope ?=> Unit): Unit = writeYaml("pipeline.yaml")(closure)
  val _image = "image"
  def env(using Scope)(entry:(String,String)*):Unit = entry.foreach((k, v) => "env" ++= {
    "name" := k
    "value" := v
  })
  val _command = "command"
  val _dependsOn = "dependsOn"
  def cache(using Scope)(key:String, path:String):Unit =
    "cache" ::= {
      "key" := key
      "path" := path
    }