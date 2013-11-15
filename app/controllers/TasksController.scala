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
      "owner_id" -> nonEmptyText)(NewTask.apply)(NewTask.unapply))

  def create = Action {
    implicit request =>
      taskForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.tasks.newTask(formWithErrors)),
        newTask => {
          Tasks.insert(newTask.name, newTask.owner)
          Ok("hello")
        }
      )
  }

  def newTask() = Action {
    Ok(views.html.tasks.newTask(taskForm))
  }
}
