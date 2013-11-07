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
  val username: String,
  val realname: String,
  val email: String
)

object FormTest extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())

  val userForm = Form(
    mapping(
      "username" -> nonEmptyText(8),
      "realname" -> nonEmptyText,
      "email" -> email)(User.apply)(User.unapply))

  def createUser() = Action { implicit request =>
    val user: User = userForm.bindFromRequest.fold(
      formWithErrors => null,
      user => user
      )
    /*
     * Users.insert(user.realname, user.email, false)
     */
    Ok("Haha " + user.realname)
  }
  def index() = Action {
    Ok(views.html.form(userForm))
  }
}
