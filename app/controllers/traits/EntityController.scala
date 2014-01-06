package controllers

import models.{Entity, NewEntity}
import play.api.db.slick.Config.driver.simple._
import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.mvc.Action
import play.api.mvc.Results.Redirect
import play.api.templates.{Html}
import reflect.runtime.universe._
import models.traits.CRUDOperations

trait EntityController[T <: Entity,
  Y <: NewEntity]
  extends Controller {

  val form: Form[Y]
  val table: Table[T] with CRUDOperations[T, Y]
  val model: String
//  val controller: EntityController[T,Y]
  val currentMirror = runtimeMirror(Play.current.classloader)
  val packageName = "views.html."

  /**
   * Use reflection to instantiate the method for some needed view template.
   * @param view "index"/"show"/"newEntity"/etc.
   * @return
   */
  private def getViewTemplate(view: String) = {
    val template = packageName + model.toString.toLowerCase + "." + view
    val module = currentMirror.reflectModule(currentMirror.staticModule(template))
    val method = module.symbol.typeSignature.declaration(newTermName("apply")).asMethod
    val instance = currentMirror.reflect(module.instance)
    instance.reflectMethod(method)
  }

  private def getReverseRouter() = {

  }

  def index = Action { implicit request =>
    val all = table.findAll
    val templateMethod = getViewTemplate("index")
    Ok(templateMethod.apply(all,session).asInstanceOf[Html])
  }

  def show(id: AnyRef) = Action { implicit request =>
    val one = table.find(id)
    val templateMethod = getViewTemplate("show")
    Ok(templateMethod.apply(one, session, flash).asInstanceOf[Html])
  }

  def newEntity = Action { implicit request =>
    val templateMethod = getViewTemplate("newEntity")
    Ok(templateMethod.apply(form, session).asInstanceOf[Html])
  }

  def create = Action { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors =>
        BadRequest(getViewTemplate("newEntity").apply(formWithErrors,session).asInstanceOf[Html]),
      newEntity => {
        table.insert(newInstance = newEntity) match {
          case Right(id) =>
            Ok(getViewTemplate("show").apply(table.find(id), session, flash).asInstanceOf[Html])
          case Left(id) =>
            BadRequest(getViewTemplate("newEntity").apply(form, session).asInstanceOf[Html])
        }
      })
  }

  def edit(id: AnyRef) = TODO

  def update(id: AnyRef) = TODO

  def delete(id: String) = TODO

  def copy = TODO

}