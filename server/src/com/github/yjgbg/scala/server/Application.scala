package com.github.yjgbg.scala.server
import com.outr.lucene4s.query.BoostedSearchTerm
import zio.ZIO
import zhttp.http.*
import zhttp.service.*
import com.outr.lucene4s.{*, given}

object Application extends zio.ZIOAppDefault {
  lazy val lucene = DirectLucene(Nil,directory = None)
  lazy val nameIndex = lucene.create.field[String]("name")
  lazy val addressIndex = lucene.create.field[String]("address")

  lazy val app: Http[Any, Nothing, Request, Response] = Http.collect[Request]{
    case Method.GET -> !! / "get"/ name =>
      Response.text(lucene.query()
        .filter(fuzzy(nameIndex(name)))
        .search()
        .results
        .map(sr => sr(addressIndex))
        .mkString(","))
    case Method.GET -> !! / "set" /name / address =>
      lucene.doc().fields(nameIndex(name),addressIndex(address)).index()
      Response.text(address)
  }
  override def run = Server.start(8080,app).exitCode
}
