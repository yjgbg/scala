package com.github.yjgbg.scala.tapir.zhttp.server
import io.circe.generic.auto.*
import io.circe.syntax._
import io.circe.yaml.syntax._
import sttp.tapir.Codec._
import sttp.tapir._

import io.circe.generic.auto._
import java.util.UUID

import sttp.tapir.docs.openapi._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.Info
import sttp.tapir.openapi.circe.yaml._
class Endpoints(basePath: String):
  import sttp.tapir.Schema.annotations.{description, validate}
  case class Todo(
      @description("描述")
      @validate(sttp.tapir.Validator.min(0L)) id: Long,
      @validate(sttp.tapir.Validator.maxLength(6)) name: String
  ) derives Schema

  lazy val add = endpoint.post
    .in(basePath / "todo" / sttp.tapir.path[String]("id"))
    .out(jsonBody[Todo])
    .errorOut(stringBody)
    .description("this is a description")

  lazy val delete = endpoint.delete
    .in(basePath / "todo" / sttp.tapir.path[String]("uuid"))
    .out(jsonBody[Todo])
    .errorOut(stringBody)
    .description("this is a description")

  lazy val list = endpoint.get
    .in(basePath / "todo")
    .out(jsonBody[Seq[Todo]])
    .errorOut(stringBody)
    .description("list all todo")
