package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Plugins
import models.NewPlugin
import helpers.ObserverHelper

object PluginsController extends Controller {

  val pluginForm = Form(
  mapping(
    "name"   -> nonEmptyText,
    "pack"   -> optional(text)
  )(NewPlugin.apply)(NewPlugin.unapply))

  def newPlugin = Action { implicit request =>
    Ok(views.html.plugins.newEntity(pluginForm))
  }

  def index() = Action { implicit request =>
    Ok(views.html.plugins.index())
  }

  //For editing purposes
  def create() = Action { implicit request =>
    pluginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.plugins.newEntity(formWithErrors)),
      plugin => {
        Plugins.insert(newInstance = plugin)
        Redirect(routes.PluginsController.index())
      })
  }

  def show(name: String) = Action { implicit request =>
    Plugins.find(name) match {
      case Some(plugin) => Ok(views.html.plugins.show(plugin))
      case None =>  BadRequest(views.html.index(s"Error Finding Plugin $name"))
    }

  }

  def edit(id: String) = Action { implicit request =>
    val plugin = Plugins.mapToNew(Plugins.uuid(id))
    val filledForm = pluginForm.fill(plugin)
    Ok(views.html.plugins.edit(filledForm, id))
  }

  def update(id: String) = Action { implicit request =>
    Plugins.find(id) match {
      case Some(oldPlugin) =>
        pluginForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.plugins.edit(formWithErrors, id)),
          updatedPlugin => {
            Plugins.update(Plugins.mapToEntity(oldPlugin.id, updatedPlugin))
            Redirect(routes.PluginsController.show(id))
              .flashing("success" -> "Package Updated!")
          }
        )
      case None =>  BadRequest(views.html.index(s"Error Finding Plugin $id"))
    }
  }

  def findPlugins: Map[String, String] = {
    ObserverHelper.mapIdToName
  }
}