package controllers

import play.api._
import play.api.mvc._

import helpers.UUIDGenerator

import models._
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.Database.threadLocalSession
import scala.slick.driver.PostgresDriver.simple._

// for forms
import play.api.data._
import play.api.data.Forms._

import java.util.UUID

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
          val uuid = Users.insert(user)
          Redirect(routes.UsersController.show(uuid.toString))
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

  def edit(uuid_str: String) = Action {
    val uuid = UUID.fromString(uuid_str)

    database withSession {
      val q = for {
        uuid <- Parameters[UUID]
        u <- Users if u.uuid is uuid
      } yield u
      val user = q(uuid).first
      val filledForm = userForm.fill(User(user._2, user._3))
      Ok(views.html.users.edit_user(uuid_str, user, filledForm))
    }
  }

  def update(uuid_str: String) = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => Ok("Are you crazy?"),
      user => {
        database withSession {
          val uuid = UUID.fromString(uuid_str)
          val q = for {
            u <- Users if u.uuid === uuid
          } yield u
          q.update(uuid, user.name, user.email)
          q.updateStatement
          q.updateInvoker
          Redirect(routes.UsersController.show(uuid_str))
        }
      }
    )
  }

  def show(uuid_str: String) = Action {
    val uuid = UUID.fromString(uuid_str)

    database withSession {
      val q = for {
        uuid <- Parameters[UUID]
        u <- Users if u.uuid is uuid
      } yield u

      Ok(views.html.users.show(q(uuid).first))
    }
  }
}
