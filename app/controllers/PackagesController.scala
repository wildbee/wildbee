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
import helpers.UUIDGenerator

object PackagesController extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())

  val packageForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "creator_id" -> nonEmptyText,
      "assignee_id" -> nonEmptyText,
      "cc_list" -> text,
      "status" -> nonEmptyText,
      "os_version" -> nonEmptyText)(Package.apply)(Package.unapply))

  def index = Action {
    database withSession {
      val packs = Query(Packages).list
      Ok(views.html.packages.index(packs))
    }
  }

  def newPackage = Action {
    Ok(views.html.packages.new_package(packageForm))
  }

  /**
   * def create = Action { implicit request =>
   * packageForm.bindFromRequest.fold(
   * formWithErrors => Ok("Does not compute."),
   * pack => {
   * database withSession {
   * val uuid = UUID.randomUUID()
   * Packages.insertNew(uuid, pack)
   * Redirect(routes.PackagesController.show(uuid.toString))
   * }
   * })
   * }*
   */

  def create = TODO

  /**
   * def show(id: String) = Action {
   * val uuid = UUID.fromString(id)
   *
   * database withSession {
   * // This must return a single pack item:
   * val pack = Packages.filter(p => p.id === uuid)
   *
   * Ok(views.html.packages.show(pack))
   * }
   * }*
   */

  def show(id: String) = TODO

  def edit(id: String) = TODO

  def update(id: String) = TODO

}