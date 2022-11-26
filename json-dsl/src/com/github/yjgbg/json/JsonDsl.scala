package com.github.yjgbg.json

object JsonDsl extends JsonDsl

trait JsonDsl:

  import io.circe.{*, given}

  def plus(json0: Json, json1: Json): Json =
    if (json1.isNumber || json1.isBoolean || json1.isString) {
      json1
    } else if (json1.isNull) {
      json0
    } else if (json1.isArray) {
      if json0.isArray then
        Json.fromValues(json0.asArray.get.concat(json1.asArray.get))
      else json1
    } else {
      if json0.isObject then
        Json.fromJsonObject(JsonObject.fromMap(
          json0.asObject.get.toIterable
            .concat(json1.asObject.get.toIterable)
            .groupMapReduce(_._1)(_._2)((j0, j1) => plus(j0, j1))
        ))
      else json1
    }
  import scala.annotation.targetName

  class Scope(var json: Json)
  extension (key: String)
    @targetName(":=")
    def :=(using scope: Scope)(value: String | Boolean | Double | Long): Unit =
      scope.json = plus(
        scope.json,
        Json.obj(key -> (value match
          case str: String    => Json.fromString(str)
          case bool: Boolean  => Json.fromBoolean(bool)
          case double: Double => Json.fromDoubleOrNull(double)
          case long: Long     => Json.fromLong(long)
        ))
      )
    @targetName("::=")
    def ::=(using scope: Scope)(closure: Scope ?=> Unit): Unit = {
      val x = Json.obj(key -> json(closure))
      scope.json = plus(scope.json, x)
    }
    @targetName("+=")
    def +=(using scope: Scope)(value: String | Boolean | Double | Long): Unit =
      scope.json = plus(
        scope.json,
        Json.obj(key -> Json.arr(value match
          case string: String   => Json.fromString(string)
          case boolean: Boolean => Json.fromBoolean(boolean)
          case double: Double   => Json.fromDoubleOrNull(double)
          case long: Long       => Json.fromLong(long)
        ))
      )
    @targetName("++=")
    def ++=(using scope: Scope)(closure: Scope ?=> Unit): Unit = {
      val x =  Json.obj(key -> Json.arr(json(closure)))
      scope.json = plus(scope.json,x)
    }
  def json(closure: Scope ?=> Unit): Json =
    val scope = Scope(Json.obj())
    closure(using scope)
    scope.json

  def writeJson(path: String)(closure: Scope ?=> Unit): Unit =
    writeFile(path, json(closure).spaces2)

  def writeYaml(path: String)(closure: Scope ?=> Unit): Unit =
    writeFile(path, io.circe.yaml.syntax.AsYaml(json(closure)).asYaml.spaces2)

  def writeFile(path: String, content: String): Unit = {
    import java.nio.file.{Paths,Files}
    val p = Paths.get(path)
    val parentPath = p.toAbsolutePath().getParent()
    if (parentPath!=null)Files.createDirectories(parentPath)
    val writer = new java.io.FileWriter(path)
    writer.write(content)
    writer.flush()
    writer.close()
  }
