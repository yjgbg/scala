package com.github.yjgbg.scala.server

import zio.ZIO
import zio.schema.syntax.DiffOps

object RequestBody:
  case class DecodeException(message:String) extends RuntimeException(message,null,false,false)
  def decode(using schema:zio.schema.Schema[RequestBody[_]])(byteChunk:zio.Chunk[Byte]): ZIO[zio.schema.codec.Codec, DecodeException, RequestBody[_]] =
    ZIO.serviceWithZIO[zio.schema.codec.Codec] { codec =>
        ZIO.fromEither(codec.decode(zio.schema.Schema[RequestBody[_]]).apply(byteChunk)).mapError(msg => DecodeException(msg))
    }
  extension [A:zio.schema.Schema](a:A)
    def encode(using codec:zio.schema.codec.Codec):zio.Chunk[Byte] = codec.encode(zio.schema.Schema[A]).apply(a)

case class Pong(message:String)
enum RequestBody[Response:zio.schema.Schema] :
  type Res = Response
  case Echo extends RequestBody[Pong.type]
  case GetName(id: Long) extends RequestBody[Pong.type]
