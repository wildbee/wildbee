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

  val query_email = for {
    email <- Parameters[String]
    u <- Users if u.email is email
  } yield u

  def create = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => Ok("Are you crazy?"),
      user => {
        database withSession {
          val email = Users.insert(user.name, user.email)
          Redirect(routes.UsersController.show(email))
        }
      }
    )
  }

  def index() = Action {
    database withSession {
      val q = Query(Users).list
      Ok(views.html.users.index(q))
    }
  }

  def newUser() = Action {
    Ok(views.html.users.new_user(userForm))
  }

  def edit(email: String) = Action {
    database withSession {
      val user = query_email(email).first
      val filledForm = userForm.fill(User(user._2, user._3))
      Ok(views.html.users.edit_user(user, filledForm))
    }
  }

  def update(email: String) = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => Ok("Are you crazy?"),
      user => {
        database withSession {
          val q = for {
            u <- Users if u.email === email
          } yield u

          q.update(q.first._1, user.name, user.email)
          q.updateStatement
          q.updateInvoker
          Redirect(routes.UsersController.show(user.email))
        }
      }
    )
  }

  def show(email: String) = Action {
    database withSession {
      val q = query_email(email).first
      Ok(views.html.users.show(q))
    }
  }
}
