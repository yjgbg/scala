package com.github.yjgbg.json

import scala.concurrent.duration.Duration

object GitlabCiDsl extends GitlabCiDsl
trait GitlabCiDsl extends JsonDsl:
  opaque type JobScope = Scope
  opaque type GitlabCiScope = Scope
  val api = gitlab.api
  def gitlabCi(closure: GitlabCiScope ?=> Unit): Unit =
    writeYaml("gitlab-ci.sc.yml")(closure)
  def default(using GitlabCiScope)(closure: JobScope ?=> Unit): Unit = job("default")(closure)
  def job(using GitlabCiScope)(name: String)(closure: JobScope ?=> Unit): Unit =
    name ::= closure
  def tags(using JobScope)(values: String*):Unit = values.foreach("tags" += _)
  def script(using JobScope)(commands: String*) = commands.foreach("script" += _)
  def beforeScript(using JobScope)(commands: String*) = commands.foreach("before_script" += _)
  def afterScript(using JobScope)(commands: String*) = commands.foreach("script" += _)
  def image(using JobScope)(tag: String) = "image" := tag
  def needs(using JobScope)(name: String*) = name.foreach("needs" += _)
  def allowFailure(using JobScope)(boolean: Boolean = true): Unit = "allow_failure" := boolean
  def allowFailure(using JobScope)(exitCodes: Int*): Unit =
    "allow_failure" ::= { exitCodes.foreach("exit_codes" += _.toLong) }
  def artifacts(using JobScope)(paths: Seq[String], expireIn: Duration): Unit =
    "artifacts" ::= { paths.foreach("paths" += _);"expire_in" := expireIn.toSeconds.toString}
  def cache(using JobScope)(key:String, paths:String*):Unit = 
    "cache" ::= {"key" := key;paths.foreach("paths" += _)}
