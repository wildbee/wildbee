package controllers

import models.{Entity, NewEntity}
import play.api.db.slick.Config.driver.simple._
import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.mvc.Action
import play.mvc.Controller._
import play.api.http.Writeable
import play.api.templates.Html

trait EntityController[T <: Entity,
  Y <: NewEntity,
  Z <: Table[T]]
  extends Controller {

  //var viewMap: Map[String,(Any) => Result]
  //var indexView: play.templates.BaseScalaTemplate.type
 // var indexView: Writeable[String => Html]
  val indexView: String => Html
  val form: Form[Y]


  def index = Action { implicit request =>
    //val view = viewMap("index")
    Ok(indexView)
  }

  def show(id: AnyRef) = TODO

  def newEntity = TODO

  def create = TODO

  def edit(id: AnyRef) = TODO

  def update(id: AnyRef) = TODO

  def delete = TODO

  def copy = TODO

}