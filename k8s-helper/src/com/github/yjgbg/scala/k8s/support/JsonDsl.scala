package com.github.yjgbg.scala.k8s.support

import scala.annotation.targetName

object JsonDsl extends JsonDsl
trait JsonDsl:
  object JsonNode:
    lazy val obj: JsonNode.Obj = JsonNode.Obj(Map())

  enum JsonNode:
    case Null extends JsonNode
    case Bool(boolean: Boolean) extends JsonNode
    case Str(string: String) extends JsonNode
    case Num(number: Double) extends JsonNode
    case Obj(hash: Map[String, JsonNode]) extends JsonNode
    case Arr(seq: Seq[JsonNode]) extends JsonNode


    def toJson: String = this match
      case JsonNode.Null => "null"
      case JsonNode.Bool(bool) => bool.toString
      case JsonNode.Num(num) => num.toString
      case JsonNode.Str(str) => s"\"${str.replace("\"", "\\\"").replace("\n","\\n")}\""
      case JsonNode.Obj(hash) => hash.map((k, v) => s"\"${k.replace("\"", "\\\"")}\":${v.toJson}").mkString("{", ",", "}")
      case JsonNode.Arr(seq) => seq.map(_.toJson).mkString("[", ",", "]")

  case class Scope(var jsonObj: JsonNode.Obj)

  def write(path:String,jsonNode:JsonNode):Unit = {
    import java.nio.file.{Paths,Files}
    val p = Paths.get(path)
    if (!Files.exists(p.getParent))Files.createDirectories(p.getParent)
    Files.writeString(p, jsonNode.toJson)
  }

  def write(path: String)(closure: Scope ?=> Unit): Unit = write(path,json(closure))

  def json(closure: Scope ?=> Unit): JsonNode.Obj = {
    val scope = Scope(JsonNode.obj)
    closure.apply(using scope)
    scope.jsonObj: JsonNode.Obj
  }

  private def node(value: Boolean | String | Double| Null) = value match
    case bool: Boolean => JsonNode.Bool(bool)
    case str: String => JsonNode.Str(str)
    case num: Double => JsonNode.Num(num)
    case null => JsonNode.Null

  extension (that: String)
    @targetName("is")
    def ::=(using scope: Scope)(closure: Scope ?=> Unit): Unit = {
      val subScope = Scope(JsonNode.obj)
      closure(using subScope)
      scope.jsonObj = JsonNode.Obj(scope.jsonObj.hash.updated(that, subScope.jsonObj))
    }
    @targetName("is")
    def :=(using scope: Scope)(value: Boolean | String | Double| Null): Unit =
      scope.jsonObj = JsonNode.Obj(scope.jsonObj.hash.updated(that, node(value)))
    @targetName("add")
    def ++=(using scope: Scope)(closure: Scope ?=> Unit): Unit =
      scope.jsonObj = JsonNode.Obj(scope.jsonObj.hash.updatedWith(that)(old => (for {
        node <- old
        if node.isInstanceOf[JsonNode.Arr]
        arr = node.asInstanceOf[JsonNode.Arr]
      } yield JsonNode.Arr(arr.seq :+ {
        val scope = Scope(JsonNode.obj)
        closure(using scope)
        scope.jsonObj
      })).orElse(Some(JsonNode.Arr(Seq({
        val scope = Scope(JsonNode.obj)
        closure(using scope)
        scope.jsonObj
      }))))))

    @targetName("add")
    def +=(using scope: Scope)(value: Boolean | String | Double|Null): Unit =
      scope.jsonObj = JsonNode.Obj(scope.jsonObj.hash.updatedWith(that)(old => (for {
        jsonNode <- old
        if jsonNode.isInstanceOf[JsonNode.Arr]
        arr = jsonNode.asInstanceOf[JsonNode.Arr]
        x = JsonNode.Arr(arr.seq :+ node(value))
      } yield x).orElse(Some(JsonNode.Arr(Seq(node(value)))))))