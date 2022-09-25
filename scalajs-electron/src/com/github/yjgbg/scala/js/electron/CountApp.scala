package com.github.yjgbg.scala.js.electron
import org.scalajs.dom.document
import cats.effect.IO
import tyrian.Html.{div,button,onClick}
import tyrian.*

import scala.scalajs.js.annotation.*

object CountApp extends TyrianApp[CountApp.Msg, CountApp.Model]:
  type Msg = String
  type Model = Int
  @JSExportTopLevel("main") // 该注解的函数为主函数
  override def launch(containerId: String): Unit = super.launch(containerId)
  def init(flags: Map[String, String]): (Int, Cmd[IO, String]) = (0, Cmd.None)

  def update(model: Int): String => (Int, Cmd[IO, String]) =
    case "+" => (model + 1, Cmd.None)
    case "-" => (model - 1, Cmd.None)

  def view(model: Int): Html[String] =
    div(
      button(onClick("-"))("-"),
      div(model.toString),
      button(onClick("+"))("+")
    )

  def subscriptions(model: Int): Sub[IO, String] = Sub.None