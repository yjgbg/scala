package com.github.yjgbg.json

object GitlabCiDsl extends GitlabCiDsl
trait GitlabCiDsl extends JsonDsl:
  def gitlabCi(closure:Scope ?=> Unit):Unit = writeYaml(".gitlab-ci.yml")(closure)