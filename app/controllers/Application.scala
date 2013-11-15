package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    request =>
      request.session.get("connected").map {
        user =>
          Ok(views.html.index(user))
      }.getOrElse {
        Ok(views.html.index("NOT SIGNED IN"))
      }
  }
}
