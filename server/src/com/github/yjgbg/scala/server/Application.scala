package com.github.yjgbg.scala.server

import com.github.yjgbg.scala.server.layer.{Env, Error}
import zio.{*, given}
import zhttp.http.*
import zhttp.service.{Server, *}
import zio.redis.{*, given}
import zio.schema.Schema
import zio.schema.codec.{Codec, JsonCodec}

import scala.Tuple.Union
import scala.collection.SortedSet
import scala.concurrent.duration.*

object Application extends zio.ZIOAppDefault {
  import zio.json.{*, given}
  import RequestBody.*
  def app[A,B](a:RequestBody[_]):ZIO[A,B,a.Res] = a match
    case RequestBody.Echo => ???
    case RequestBody.GetName(id) => ???

  override def run: ZIO[Any, RedisError.IOError, ExitCode] = Server
    .start(9999,Http.collectZIO[Request](req => for {
      body <- req.body
      requestBody <- RequestBody.decode(body)
      res <- app(requestBody)
      chunk <- ZIO.serviceWith[Codec]{codec => codec.encode(requestBody.responseSchema)(res)}
    } yield Response(
      headers = Headers.contentType(HeaderValues.applicationJson),
      data = HttpData.fromChunk(chunk)
    )).provideLayer(layer.dev ++ Scope.default))
    .exitCode
}
