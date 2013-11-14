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
      "osVersion" -> nonEmptyText)(Package.apply)(Package.unapply))

  def index = Action {
    database withSession {
      val packs = Query(Packages).list
      Ok(views.html.packages.index(packs))
    }
  }

  def newPackage = Action {
    Ok(views.html.packages.new_package(packageForm))
  }

  def create = Action { implicit request =>
    packageForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.packages.new_package(formWithErrors)),
      pack => {
        database withSession {
          val uuid = Packages.insert(pack)
          Redirect(routes.PackagesController.show(uuid.toString))
        }
      })
  }

  def show(id: String) = Action {
    val uuid = Config.pkGenerator.fromString(id)

    database withSession {
      val pack = Query(Packages).where(_.id === uuid).first

      Ok(views.html.packages.show(pack))
    }
  }

  def edit(id: String) = TODO

  def update(id: String) = TODO

}