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

  def newTask() = Action { implicit request =>
    Ok(views.html.tasks.newEntity(taskForm))
  }

  def create = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.tasks.newEntity(formWithErrors)).flashing("success" -> "Package Created!"),
      newTask => {
        Tasks.insert(newTask)
        Redirect(routes.TasksController.show(newTask.name)).flashing("success" -> "Task Created!")
      })
  }

  def delete(name: String) = Action { implicit request =>
    val dependentPackages = Packages.findAll filter (_.task == Tasks.find(name).id)
    if (!dependentPackages.isEmpty){
      Redirect(routes.TasksController.show(name))
      .flashing("failure" ->
        (s"Packages: ${
          dependentPackages map (_.name) mkString("[",",","]") } depend on this task."
        + "You must remove these packages if you would like to delete this Task."))
    }
    else {
      Tasks.delete(name)
      Redirect(routes.TasksController.index)
    }
  }
}
