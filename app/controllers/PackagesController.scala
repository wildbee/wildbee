package controllers

import play.api._
import play.api.mvc._
import models._

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
      "status" -> nonEmptyText,
      "osVersion" -> nonEmptyText)(NewPackage.apply)(NewPackage.unapply))

  def index = Action {
    Ok(views.html.packages.index(Packages.findAll, packageForm))
  }

  def newPackage = Action {
    Ok(views.html.packages.newPackage(packageForm))
  }

  def create = Action { implicit request =>
    packageForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.packages.newPackage(formWithErrors)),
      pack => {
          val uuid = Packages.insert(pack)
          Redirect(routes.PackagesController.show(uuid.toString))
      })
  }

  def show(id: String) = Action {
    Ok(views.html.packages.show(Packages.findById(id)))
  }

  def edit(id: String) = TODO

  def update(id: String) = TODO

}