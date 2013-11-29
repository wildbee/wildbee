package controllers

import play.api._
import play.api.mvc._
import models._
import java.util.UUID
import play.api.data._
import play.api.data.Forms._

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
    Ok(views.html.packages.new_entity(packageForm))
  }

  def create = Action { implicit request =>
    packageForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.packages.new_entity(formWithErrors)),
      pack => {
        val uuid = Packages.insert(pack)
        val newPack = Packages.find(uuid)
        Redirect(routes.PackagesController.show(newPack.task.toString, newPack.name))
      })
  }

//  def show(id: String) = Action { implicit request =>
//    Ok(views.html.packages.show(Packages.findById(id)))
//  }

  def show(task: String, pack: String) = Action { implicit request =>
    Ok(views.html.packages.show(Packages.findByTask(task, pack)))
  }

  def edit(id: String) = Action { implicit request =>
    val pack = Packages.mapToNewPackage(id)
    val filledForm = packageForm.fill(pack)
    Ok(views.html.packages.edit(filledForm, id))
  }

  def update(id: String) = Action { implicit request =>
    val oldPack = Packages.findById(id)
    packageForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.packages.edit(formWithErrors, oldPack.id.toString)),
      updatedPack => {
        Packages.updatePackage(oldPack.id, updatedPack, oldPack)
        Redirect(routes.PackagesController.show(oldPack.task.toString, oldPack.name))
      })
  }

  def delete(id: String) = Action { implicit request =>
    Packages.delete(Packages.uuid(id))
    Redirect(routes.PackagesController.index)
  }

}