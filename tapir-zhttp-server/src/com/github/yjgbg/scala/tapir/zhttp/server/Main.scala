package com.github.yjgbg.scala.tapir.zhttp.server
import sttp.tapir.server.interceptor.RequestResult.Response
import zio.ZIO

object Main extends zio.ZIOAppDefault:
  override def run = {
    val endpoints = Endpoints("api")
    val implementations = Implementations(endpoints)
    import zhttp.http.*
    val doc = zhttp.http.Http.collect[zhttp.http.Request] {
      case zhttp.http.Method.GET -> !! / "docs" => zhttp.http.Response.text(endpoints.doc)
    }
    zhttp.service.Server.start(8000, implementations.server ++ doc).exitCode
  }