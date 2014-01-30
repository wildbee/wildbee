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

trait EntityController[T <: Entity,
  Y <: NewEntity]
  extends Controller {

  val form: Form[Y]
  val table: Table[T] with CRUDOperations[T, Y] with EntityTable[T,Y]
  val modelName: String

  val currentMirror = runtimeMirror(Play.current.classloader)
  val packageName = "views.html."


  /**
   * Use reflection to instantiate the method for some needed view template.
   * @param view "index"/"show"/"newEntity"/etc.
   * @return
   */
  protected def getViewTemplate(view: String) = {
    val template = packageName + modelName.toString.toLowerCase + "." + view
    val module = currentMirror.reflectModule(currentMirror.staticModule(template))
    val method = module.symbol.typeSignature.declaration(newTermName("apply")).asMethod
    val instance = currentMirror.reflect(module.instance)
    instance.reflectMethod(method)
  }

  def index = Action { implicit request =>
    val all = table.findAll
    val ses = session
    Ok(getViewTemplate("index").apply(table.findAll,session).asInstanceOf[Html])
  }

  def show(id: AnyRef) = Action { implicit request =>
    table.find(id) match {
      case Some(obj) => Ok(getViewTemplate("show").apply(obj, session, flash).asInstanceOf[Html])
      case None =>  BadRequest(views.html.index(s"Error finding id: $id"))
    }
  }

  def newEntity = Action { implicit request =>
    val templateMethod = getViewTemplate("newEntity")
    Ok(templateMethod.apply(form, session).asInstanceOf[Html])
  }

  def create = Action { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors =>
        BadRequest(getViewTemplate("newEntity").apply(formWithErrors,
          session+("failure"->"invalid inputs")).asInstanceOf[Html]),
      newEntity => {
        table.insert(newInstance = newEntity) match {
          case Right(id) => {
            table.find(id) match {
              case Some(newObj) =>
                Ok(getViewTemplate("show").apply(newObj, session,
                  flash.+("success"->"Created")).asInstanceOf[Html])
              case None =>  BadRequest(views.html.index(s"Error Finding X $id"))
            }

          }
          case Left(error) => {
            BadRequest(getViewTemplate("newEntity").apply(form, session,
              flash.+("failure" -> "unable to create")).asInstanceOf[Html])
          }
        }
      })
    /*
    form.bindFromRequest.fold(
      formWithErrors =>
        BadRequest(getViewTemplate("newEntity").apply(formWithErrors,
          session+("failure"->"invalid inputs")).asInstanceOf[Html]),
      newEntity => {
        table.insert(newInstance = newEntity) match {
          case Right(id) =>
            Ok(getViewTemplate("show").apply(table.find(id), session,
              flash.+("success"->"Created")).asInstanceOf[Html])
          case Left(id) =>
            BadRequest(getViewTemplate("newEntity").apply(form, session,
              flash.+("failure" -> "unable to create")).asInstanceOf[Html])
        }
      })*/
  }

  def edit(id: AnyRef) = Action { implicit request =>
    val filledForm = form.fill(table.mapToNew(id))
    Ok(getViewTemplate("edit").apply(filledForm,id, session).asInstanceOf[Html])
  }

  def update(id: AnyRef) = Action { implicit request =>
    table.find(id) match {
      case Some(oldEntity) =>
        form.bindFromRequest.fold(
          formWithErrors => BadRequest(getViewTemplate("edit").apply(formWithErrors,id,session).asInstanceOf[Html]),
          updatedEntity => {
            val entity = table.update(table.mapToEntity(oldEntity.id, updatedEntity))
            Ok(getViewTemplate("show").apply(entity, session,
              flash.+("success"->"updated")).asInstanceOf[Html])
          })
      case None =>  BadRequest(views.html.index(s"Error finding id: $id"))
    }
    /*
    val oldEntity = table.find(id)
    form.bindFromRequest.fold(
      formWithErrors =>
        BadRequest(getViewTemplate("edit").apply(formWithErrors,id,session).asInstanceOf[Html]),
      updatedEntity => {
        val entity = table.update(table.mapToEntity(oldEntity.id, updatedEntity))
        Ok(getViewTemplate("show").apply(entity, session,
          flash.+("success"->"updated")).asInstanceOf[Html])
      }
    )*/
  }

  def delete(id: AnyRef) = Action { implicit request =>
    table.delete(id) match {
      case Some(violatedDeps) =>
        BadRequest(getViewTemplate("show").apply(table.find(id), session,
          flash+("failure"->
            (s"${violatedDeps} depend on this entity."))).asInstanceOf[Html])
      case None =>
        Ok(getViewTemplate("index").apply(table.findAll,session).asInstanceOf[Html])
    }
  }
}