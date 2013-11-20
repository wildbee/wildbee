package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.Database.threadLocalSession
import scala.slick.driver.PostgresDriver.simple._
import play.api.data._
import play.api.data.Forms._
import java.util.UUID
import helpers._

object PackagesController extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())

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
    Ok(views.html.packages.new_entity(packageForm))
  }

  def create = Action { implicit request =>
    packageForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.packages.new_entity(formWithErrors)),
      pack => {
        val uuid = Packages.insert(pack)
        Redirect(routes.PackagesController.show(uuid.toString))
      })
  }

  def show(id: String) = Action {
    Ok(views.html.packages.show(Packages.findById(id)))
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
        Redirect(routes.PackagesController.show(oldPack.id.toString))
      })
  }

}