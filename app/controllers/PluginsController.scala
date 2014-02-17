package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.{NewStatus, Status, Plugins, NewPlugin, Plugin}
import helpers.ObserverHelper


object PluginsController extends  EntityController[Plugin, NewPlugin] {
  val table = models.Plugins
  val modelName = "plugins"

  val form = Form(
  mapping(
    "name"   -> nonEmptyText,
    "pack"   -> optional(text)
  )(NewPlugin.apply)(NewPlugin.unapply))

  //Disabled creating a plugin from the UI
  override def create() = Action { implicit request =>
    BadRequest
  }

  def findPlugins: Map[String, String] = {
    ObserverHelper.mapIdToName()
  }
}