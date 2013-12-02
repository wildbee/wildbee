package controllers

import play.api._
import play.api.mvc._
import models._

import play.api.data._
import play.api.data.Forms._

object StatusesController extends Controller {

  val StatusForm = Form(
    mapping(
      "name" -> nonEmptyText
    )(NewStatus.apply)(NewStatus.unapply))

  def index = Action { implicit request =>
    Ok(views.html.statuses.index(Statuses.findAll, StatusForm))
  }

  def newStatus = Action { implicit request =>
    Ok(views.html.statuses.new_entity(StatusForm))
  }

  def create = Action { implicit request =>
    StatusForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.statuses.new_entity(formWithErrors)),
      status => {
        val uuid = Statuses.insert(status)
        Redirect(routes.WorkflowController.newWorkflow)
      })
  }

  def show(status: String) = Action { implicit request =>
    Ok(views.html.statuses.show(Statuses.find(status)))
  }

  def edit(id: String) = Action { implicit request =>
    val pack = Statuses.mapToNewStatus(id)
    val filledForm = StatusForm.fill(pack)
    Ok(views.html.statuses.edit(filledForm, id))
  }

  def update(id: String) = Action { implicit request =>
    val oldStatus = Statuses.findById(id)
    StatusForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.statuses.edit(formWithErrors, oldStatus.id.toString)),
      updatedPack => {
        Statuses.updateStatus(oldStatus.id, updatedPack, oldStatus)
        Redirect(routes.StatusesController.show(oldStatus.id.toString))
      })
  }

  def delete(status: String) = Action { implicit request =>
    val uuid = Statuses.nameToId(status)
    Statuses.delete(uuid)
    Redirect(routes.StatusesController.index)
  }
}