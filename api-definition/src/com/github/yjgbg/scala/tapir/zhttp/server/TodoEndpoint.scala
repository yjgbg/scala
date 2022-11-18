package com.github.yjgbg.scala.tapir.zhttp.server

import sttp.tapir.Schema.annotations.{description, validate}
import sttp.tapir.Schema
import sttp.tapir.endpoint
import sttp.tapir.stringToPath
import sttp.tapir.json.circe.jsonBody

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.string.StartsWith


trait TodoEndpoint(basepath:String):
  object todo:
    import scala.languageFeature.implicitConversions
    import scala.compiletime.byName
    case class Todo(
      id: Int Refined Positive,
      name: String
    )
    import eu.timepit.refined.RefineSyntax
    val a:Int Refined Positive = 1
    val x = Todo(1,"alice")
    lazy val add = endpoint.post
      .in(basepath / "todo" / sttp.tapir.path[String]("id"))
      // .out(jsonBody[Todo])
      .errorOut(stringBody)
      .description("this is a description")