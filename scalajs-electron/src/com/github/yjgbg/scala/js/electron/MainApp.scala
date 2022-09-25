package com.github.yjgbg.scala.js.electron

import org.scalajs.dom.document
import cats.effect.IO
import tyrian.Html.*
import tyrian.*

object MainApp extends TyrianApp[Unit,Unit]:
  override def view(model: Unit): Html[Unit] = ???

  override def init(flags: Map[String, String]): (Unit, Cmd[Nothing, Unit]) = ???

  override def update(model: Unit): Unit => (Unit, Cmd[Nothing, Unit]) = ???

  override def subscriptions(model: Unit): Sub[cats.effect.IO, Unit] = ???

  override def launch(containerId: String): Unit = super.launch(containerId)

