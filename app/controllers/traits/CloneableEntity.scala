package controllers

import models.{EntityTable, Entity, NewEntity}
import play.api.db.slick.Config.driver.simple._
import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.mvc.Action
import play.api.templates.{Html}
import reflect.runtime.universe._
import models.traits.CRUDOperations

trait CloneableEntity[T <: Entity, Y <: NewEntity] {

  self: EntityController[T,Y] =>

  def copy(id: AnyRef) = Action { implicit request =>
    val entity = table.mapToNew(id)
    val filledForm = form.fill(entity)
    Ok(getViewTemplate("newEntity").apply(filledForm, session, flash).asInstanceOf[Html])
  }
}
