package com.github.yjgbg.scala.tapir.zhttp.server
import io.circe.generic.auto.*
import io.circe.syntax._
import io.circe.yaml.syntax._
import sttp.tapir.Codec._
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.endpoint
import io.circe.generic.auto._
import java.util.UUID

import sttp.tapir._
import sttp.tapir.docs.openapi._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.Info
import sttp.tapir.openapi.circe.yaml._
class Endpoints(basePath: String):
  import sttp.tapir.Schema.annotations.{description, validate}
  import sttp.tapir.Validator.*
  case class Todo(
      @description("描述")
      @validate(min(0L)) id: Long,
      @validate(maxLength(6)) name: String
  )
  lazy val add = endpoint.post
    .in(basePath / "add" / sttp.tapir.path[String]("id"))
    .out(jsonBody[Todo])
    .errorOut(stringBody)
    .description("this is a description")

  lazy val delete = endpoint.post
    .in(basePath / "delete" / sttp.tapir.path[String]("uuid"))
    .out(jsonBody[Todo])
    .errorOut(stringBody)
    .description("this is a description")
  import sttp.apispec.openapi.OpenAPI
  import sttp.tapir._
  import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
  import sttp.apispec.openapi.circe.yaml._
  lazy val doc =
    OpenAPIDocsInterpreter().toOpenAPI(Seq(add), "My Bookshop", "1.0").toYaml
