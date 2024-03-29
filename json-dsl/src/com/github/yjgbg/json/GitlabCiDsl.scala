package com.github.yjgbg.json

import scala.concurrent.duration.Duration
import scala.annotation.implicitNotFound

object GitlabCiDsl extends GitlabCiDsl
trait GitlabCiDsl extends JsonDsl:
  @implicitNotFound(msg ="this method invoke should in JobScope") 
  opaque type JobScope = Scope
  @implicitNotFound(msg ="this method invoke should in GitlabCiScope") 
  opaque type GitlabCiScope = Scope
  @implicitNotFound(msg ="this method invoke should in ArtifactScope") 
  opaque type ArtifactScope = Scope
  @implicitNotFound(msg ="this method invoke should in CacheScope") 
  opaque type CacheScope = Scope
  val api = gitlab.api
  extension [A](a:A) private def ap[B](f:A => B):B =f(a)
  inline def gitlabCi(closure: GitlabCiScope ?=> Unit): Unit =
    writeYaml(s"${summon[sourcecode.Enclosing].value.substring("ammonite.$file.".length()).ap{x => x.substring(0,x.indexOf(".res"))}}.yml")(closure)
  def workflow(using GitlabCiScope)(name:String):Unit = 
    "workflow"::= {
      "name" := name
    }
  def default(using GitlabCiScope)(closure: JobScope ?=> Unit): Unit = job("default")(closure)
  def job(using GitlabCiScope)(name: String)(closure: JobScope ?=> Unit): Unit =
    name ::= closure
  def resourceGroup(using JobScope)(value:String):Unit = "resource_group" := value
  def tags(using JobScope)(values: String*):Unit = values.foreach("tags" += _)
  def script(using JobScope)(commands: String*) = commands.foreach("script" += _)
  def beforeScript(using JobScope)(commands: String*) = commands.foreach("before_script" += _)
  def afterScript(using JobScope)(commands: String*) = commands.foreach("script" += _)
  def image(using JobScope)(tag: String) = "image" := tag
  def needs(using JobScope)(name: String*) = name.foreach("needs" += _)
  def allowFailure(using JobScope)(boolean: Boolean = true): Unit = "allow_failure" := boolean
  def allowFailure(using JobScope)(exitCodes: Int*): Unit =
    "allow_failure" ::= { exitCodes.foreach("exit_codes" += _.toLong) }
  def artifacts(using JobScope)(closure:ArtifactScope ?=> Unit):Unit = "artifacts" ::= closure
  def expireIn(using ArtifactScope)(seconds:Int):Unit = "expire_in" := seconds.toString
  def cache(using JobScope)(key:String)(closure: CacheScope ?=> Unit):Unit = 
    "cache" ::= {"key" := key;closure.apply}
  def paths(using ArtifactScope|CacheScope)(paths:String*):Unit = paths.foreach("paths" += _)
