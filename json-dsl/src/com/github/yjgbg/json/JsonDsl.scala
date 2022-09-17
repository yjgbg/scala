package com.github.yjgbg.json

import java.io.FileWriter
import java.nio.file.{Files, Path}

object JsonDsl extends JsonDsl

trait JsonDsl:

  import io.circe.{*, given}

  case class Scope(var json: JsonObject)

  private def plus(json0: Json, json1: Json): Json =
    if (json1.isNumber || json1.isBoolean || json1.isString) {
      json1
    } else if (json1.isNull) {
      json0
    } else if (json1.isArray) {
      if json0.isArray then Json.fromValues(json0.asArray.get.concat(json1.asArray.get)) else json1
    } else {
      if json0.isObject then Json.fromJsonObject(JsonObject.fromMap(json0.asObject.get.toIterable
        .concat(json1.asObject.get.toIterable)
        .groupMapReduce(_._1)(_._2)((j0, j1) => plus(j0, j1))))
      else json1
    }

  def json(closure: Scope ?=> Unit): Json = {
    val scope = Scope(JsonObject.empty)
    closure(using scope)
    Json.fromJsonObject(scope.json)
  }

  def writeJson(path: String)(closure: Scope ?=> Unit): Unit = {
    val writer = new FileWriter(path)
    writer.write(json(closure).spaces2)
    writer.flush()
    writer.close()
  }

  def writeYaml(path: String)(closure: Scope ?=> Unit): Unit = {
    val writer = new FileWriter(path)
    import io.circe.yaml.syntax.AsYaml
    writer.write(json(closure).asYaml.spaces2)
    writer.flush()
    writer.close()
  }
  import scala.annotation.targetName

  extension (key: String)
    @targetName(":=")
    def :=(using scope: Scope)(value: String | Boolean | Double | Long): Unit =
      scope.json = plus(Json.fromJsonObject(scope.json), Json.fromJsonObject(JsonObject(key -> (value match
        case str: String => Json.fromString(str)
        case bool: Boolean => Json.fromBoolean(bool)
        case double: Double => Json.fromDoubleOrNull(double)
        case long: Long => Json.fromLong(long)
        )))).asObject.get
    @targetName("::=")
    def ::=(using scope: Scope)(closure: Scope ?=> Unit): Unit =
      val x = Scope(JsonObject.empty)
      closure(using x)
      scope.json = plus(Json.fromJsonObject(scope.json), Json.fromJsonObject(JsonObject(key -> Json.fromJsonObject(x.json)))).asObject.get
    @targetName("+=")
    def +=(using scope: Scope)(value: String | Boolean | Double | Long): Unit =
      scope.json = plus(Json.fromJsonObject(scope.json), Json.fromJsonObject(JsonObject(key -> Json.fromValues(Seq(value match
        case string: String => Json.fromString(string)
        case boolean: Boolean => Json.fromBoolean(boolean)
        case double: Double => Json.fromDoubleOrNull(double)
        case long: Long => Json.fromLong(long)
      ))))).asObject.get
    @targetName("++=")
    def ++=(using scope: Scope)(closure: Scope ?=> Unit): Unit =
      val x = Scope(JsonObject.empty)
      closure(using x)
      val arr = scope.json(key).filter(_.isArray)
        .map(_.asArray.get :+ Json.fromJsonObject(x.json))
        .map(Json.fromValues(_))
        .getOrElse(Json.fromValues(Seq(Json.fromJsonObject(x.json))))
      scope.json = plus(Json.fromJsonObject(scope.json), Json.fromJsonObject(JsonObject(key -> arr))).asObject.get
