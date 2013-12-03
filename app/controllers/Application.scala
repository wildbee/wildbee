package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import models._

case class Authenticate(email: String)

object Application extends Controller {

  val authenticationForm = Form(
    mapping("email" -> text)(Authenticate.apply)(Authenticate.unapply))

  def index = Action { implicit request =>
      request.session.get("connected").map {
        user =>
          Ok(views.html.index(user))
      }.getOrElse {
        Ok(views.html.index("NOT SIGNED IN"))
      }
  }

  def signIn = Action {implicit request =>
    authenticationForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index("You need haha")),
      authenticate => {
        if (Users.findByEmail(authenticate.email).email == authenticate.email) {
          Redirect(routes.Application.index).withSession("connected" -> authenticate.email)
        } else {
          Ok("failed")
        }
      })
  }

  def logout = Action {
    Redirect(routes.Application.index).withNewSession
  }

}
