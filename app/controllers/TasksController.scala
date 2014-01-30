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

  def show(name: String) = Action { implicit request =>
    Tasks.find(name) match {
      case Some(task) =>  Ok(views.html.tasks.show(task))
      case None =>  BadRequest(views.html.index(s"Error finding task $name"))
    }
  }

  def newTask = Action { implicit request =>
    Ok(views.html.tasks.newEntity(taskForm))
  }

  def create = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.tasks.newEntity(formWithErrors)),
      newTask => {
        Tasks.insert(newInstance = newTask) match {
          case Right(id) => {
            Redirect(routes.TasksController.show(newTask.name))
              .flashing("success" -> "Task Created!")
          }
          case Left(error) => {
            BadRequest(views.html.tasks.newEntity(taskForm))
              .flashing("failure" -> error)
          }
        }
      })
  }

  def delete(name: String) = Action { implicit request =>
    Tasks.delete(name) match {
      case Some(violatedDeps) =>
        Redirect(routes.TasksController.show(name))
        .flashing("failure" ->
          (s"Packages: ${violatedDeps} depend on this task."
          + "You must remove these packages if you would like to delete this Task."))
      case None => Redirect(routes.TasksController.index)
    }
  }
}
