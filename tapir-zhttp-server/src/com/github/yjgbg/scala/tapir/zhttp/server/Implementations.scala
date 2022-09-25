package com.github.yjgbg.scala.tapir.zhttp.server

import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.*
class Implementations(endpoints: Endpoints):
  val server = ZioHttpInterpreter().toHttp(List(
      endpoints.add.zServerLogic(long => zio.ZIO.succeed(endpoints.Todo(1L,long)))
    ))



