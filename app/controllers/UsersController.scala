package controllers

import play.api._
import play.api.mvc._

import models._
import play.api.data._
import play.api.data.Forms._
import play.mvc.Result
import play.api.http.Writeable

object UsersController extends EntityController[User, NewUser] {
val table = models.Users
val modelName = "users"

val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> email)(NewUser.apply)(NewUser.unapply))

  /**
   * Implements its own version of show because we find users by
   * email.
   * @param id
   * @return
   */
  override def show(id: AnyRef) = Action { implicit request =>
    id match {
      case id: String => Ok(views.html.users.show(Users.findByEmail(id)))
      case _ => NotFound
    }
  }

  /**
   * Implements its own version of update because we find users
   * by email.
   * @param email
   * @return
   */
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
