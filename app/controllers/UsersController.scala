package controllers

import play.api._
import play.api.mvc._

import models._
import play.api.data._
import play.api.data.Forms._

object UsersController extends EntityController[User, NewUser, Users.type ] {

  val userForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> email)(NewUser.apply)(NewUser.unapply))

//  def index() = Action { implicit request =>
//    Ok(views.html.users.index())
//  }
//
//  def show(email: String) = Action { implicit request =>
//    Ok(views.html.users.show(Users.findByEmail(email)))
//  }
//
//  def newUser() = Action { implicit request =>
//    Ok(views.html.users.newEntity(userForm))
//  }
//
//  def edit(email: String) = Action { implicit request =>
//    val user = Users.findByEmail(email)
//    val filledForm = userForm.fill(NewUser(user.name, user.email))
//    Ok(views.html.users.edit(filledForm, user))
//  }
//
//  def create = Action { implicit request =>
//      userForm.bindFromRequest.fold(
//        formWithErrors => BadRequest(views.html.users.newEntity(formWithErrors)),
//        newUser => {
//          val email = Users.insert(newUser.name, newUser.email)
//          Redirect(routes.UsersController.show(email))
//        }
//      )
//  }
//
//  def update(email: String) = Action { implicit request =>
//      val user = Users.findByEmail(email)
//
//      userForm.bindFromRequest.fold(
//        formWithErrors => BadRequest(views.html.users.edit(formWithErrors, user)),
//        editUser => {
//          Users.update(email, editUser)
//          Redirect(routes.UsersController.show(user.email))
//        }
//      )
//  }


}
