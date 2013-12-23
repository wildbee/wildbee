package controllers

import play.api._
import play.api.mvc._
import models._
import java.util.UUID
import play.api.data._
import play.api.data.Forms._
import observers.TestObserver

object PackagesController extends Controller {

  val packageForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "task" -> nonEmptyText,
      "creator" -> nonEmptyText,
      "assignee" -> nonEmptyText,
      "ccList" -> text,
      "status"-> text,
      "osVersion" -> nonEmptyText)(NewPackage.apply)(NewPackage.unapply))

  def index = Action { implicit request =>
    Ok(views.html.packages.index(Packages.findAll, packageForm))
  }

  def newPackage = Action { implicit request =>
    Ok(views.html.packages.newEntity(packageForm))
  }

  def create = Action { implicit request =>
    packageForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.packages.newEntity(formWithErrors)),
      pack => {
        val uuid = Packages.insert(pack)
        val newPack = Packages.find(uuid)
        Redirect(routes.PackagesController.show(newPack.task.toString, newPack.name))
            .flashing("success" -> "Package Created!")
      })
  }

  def show(taskId: String, packId: String) = Action { implicit request =>
    Ok(views.html.packages.show(Packages.findByTask(taskId, packId)))
  }

  def edit(taskId: String, packId: String) = Action { implicit request =>
    val pack = Packages.mapToNew(Packages.findByTask(taskId, packId).id)
    val filledForm = packageForm.fill(pack)
    val statuses = Transitions.allowedStatuses(pack.task,pack.name)
    Ok(views.html.packages.edit(filledForm, packId, statuses))
  }

  def update(id: String) = Action { implicit request =>
    val oldPack = Packages.find(id)
    packageForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.packages.edit(formWithErrors, oldPack.id.toString)),
      updatedPack => {
        Packages.update(Packages.uuid(id), updatedPack)
        Redirect(routes.PackagesController.show(oldPack.task.toString, oldPack.name))
            .flashing("success" -> "Package Updated!")
      })
  }

  def delete(id: String) = Action { implicit request =>
    Packages.delete(Packages.uuid(id))
    Redirect(routes.PackagesController.index).flashing("success" -> "Package Deleted!")
  }

  def copy(tid: String, pid: String) = Action { implicit request =>
    val pack = Packages.mapToNew(Packages.findByTask(tid, pid).id)
    val filledForm = packageForm.fill(pack)
    val statuses = Transitions.allowedStatuses(pack.task,pack.name)
    Ok(views.html.packages.newEntity(filledForm,statuses))
  }

  def register(id: String) = Action {
    println("Registering " + id)
    Packages.addObserver(TestObserver)
    val pack = Packages.find(id)
    Redirect(routes.PackagesController.show(pack.task.toString, pack.name))
  }

}