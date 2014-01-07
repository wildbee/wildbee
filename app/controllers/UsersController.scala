package controllers

import play.api._
import play.api.mvc._

import models._
import play.api.data._
import play.api.data.Forms._
import play.mvc.Result
import play.api.http.Writeable

object UsersController extends EntityController[User, NewUser] {

//  override val indexView: play.templates.BaseScalaTemplate = views.html.users.index
val table = models.Users
val modelName = "users"
//val indexView: views.html.users.index.type = views.html.users.index
//  val modelName: String = "users"

//  viewMap = Map[String,(Any) => Result](
//    ("index",views.html.users.index))


  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> email)(NewUser.apply)(NewUser.unapply))


//  def index() = Action { implicit request =>
//    Ok(views.html.users.index())
//  }
//
  override def show(id: AnyRef) = Action { implicit request =>
    id match {
      case id: String => Ok(views.html.users.show(Users.findByEmail(id)))
      case _ => NotFound
    }
  }
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
  def update(email: String) = Action { implicit request =>
      val user = Users.findByEmail(email)

      form.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors, user)),
        editUser => {
          Users.update(email, editUser)
          Redirect(routes.UsersController.show(user.email))
        }
      )
  }


}
