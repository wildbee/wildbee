package controllers

import play.api._
import play.api.mvc._

import models._

import play.api.data._
import play.api.data.Forms._

object TasksController extends Controller {

  val taskForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "owner" -> nonEmptyText,
      "workflow" -> nonEmptyText)(NewTask.apply)(NewTask.unapply))

  def index() = Action { implicit request =>
    Ok(views.html.tasks.index())
  }

  def show(task: String) = Action { implicit request =>
    Ok(views.html.tasks.show(Tasks.find(task)))
  }

  def newTask = Action { implicit request =>
    Ok(views.html.tasks.newEntity(taskForm))
  }

  def create = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.tasks.newEntity(formWithErrors)),
      newTask => {
        Tasks.insert(newTask)
        Redirect(routes.TasksController.show(newTask.name))
      })
  }

  def delete(name: String) = Action { implicit request =>
    Tasks.delete(name)
    Redirect(routes.TasksController.index)
  }
}
