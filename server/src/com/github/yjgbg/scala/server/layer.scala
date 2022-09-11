package com.github.yjgbg.scala.server
import zhttp.service.{EventLoopGroup, ServerChannelFactory}
import zhttp.service.server.ServerChannelFactory
import zio.*
import zio.redis.*
import zio.schema.codec.{Codec, JsonCodec}
object layer:
  type Env = RedisConfig & Codec & RedisExecutor & Redis & ServerChannelFactory & EventLoopGroup
  type Error = RedisError.IOError
  // 对程序运行环境的抽象
  def dev: Layer[Error,Env] =
    EventLoopGroup.auto(0)  // EventLoopGroup
      >+> ServerChannelFactory.auto  // ServerChannelFactory
      >+> ZLayer.succeed(RedisConfig("localhost", 6379)) // RedisConfig
      >+> ZLayer.succeed(JsonCodec) // Codec
      >+> RedisExecutor.layer // RedisConfig ,RedisError.IOError,RedisExecutor
      >+> RedisLive.layer // Codec & RedisExecutor , Redis
  def test: Layer[Error, Env] =
    EventLoopGroup.auto(0) // EventLoopGroup
      >+> ServerChannelFactory.auto // ServerChannelFactory
      >+> ZLayer.succeed(RedisConfig("localhost", 6379)) // RedisConfig
      >+> ZLayer.succeed(JsonCodec) // Codec
      >+> RedisExecutor.layer // RedisConfig ,RedisError.IOError,RedisExecutor
      >+> RedisLive.layer // Codec & RedisExecutor , Redis
  def current: ZIO[Any, Serializable, Layer[Error, Env]] = for {
    option <- System.env("profile")
    profile <- ZIO.fromOption(option)
  } yield profile match
    case "test" => test
    case "dev" => dev