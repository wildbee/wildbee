package controllers

import play.api._
import play.api.mvc._
import views._

import models._
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.Database.threadLocalSession
import scala.slick.driver.PostgresDriver.simple._

// for forms
import play.api.data._
import play.api.data.Forms._

case class User(
  username: String,
  realname: Option[String],
  email: String
)

object FormTest extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())

  val userForm = Form(
    mapping(
      "username" -> nonEmptyText(8),
      "realname" -> optional(text),
      "email" -> email)(User.apply)(User.unapply))

  def createUser() = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => Redirect(routes.Application.index),
      user => Redirect(routes.Application.test)
      )
  }
  def index() = Action {
    Ok(views.html.form(userForm))
  }
}
