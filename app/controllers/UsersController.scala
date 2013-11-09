package controllers

import play.api._
import play.api.mvc._

import models._
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.Database.threadLocalSession
import scala.slick.driver.PostgresDriver.simple._

// for forms
import play.api.data._
import play.api.data.Forms._

object UsersController extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())

  val userForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> email)(User.apply)(User.unapply))

  def create = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => Ok("Are you crazy?"),
      user => {
        database withSession {
          Users.insert(user.name, user.email)
          Ok("It's working")
        }
      }
    )
  }

  def index() = Action {
    val q = Query(Users).list
    Ok(views.html.index(q))
  }

  def newUser() = Action {
    Ok(views.html.users.new_user(userForm))
  }

  def edit() = Action {
    Ok("haha")
  }

  def update() = Action {
    Ok("haha")
  }
}
