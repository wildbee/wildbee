package controllers

import models.{Entity, NewEntity}
import play.api.db.slick.Config.driver.simple._
import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.mvc.Action
import play.mvc.Controller._

trait EntityController[T <: Entity,
  Y <: NewEntity,
  Z <: Table[T]]
  extends Controller {

  val viewMap = Map()
  val form = None

  //self: Controller =>

//  def index = Action { implicit request =>
//    val view = viewMap("index")
//    Ok(view)
//  }

  def index = TODO

  def show(id: AnyRef) = TODO

  def newEntity = TODO

  def create = TODO

  def edit(id: AnyRef) = TODO

  def update(id: AnyRef) = TODO

  def delete = TODO

  def copy = TODO

}