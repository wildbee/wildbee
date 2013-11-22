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

  def index() = Action {
    Ok(views.html.tasks.index())
  }

  def show(taskName: String) = Action {
    Ok(views.html.tasks.show(Tasks.findByName(taskName)))
  }
    
  def newTask() = Action {
    Ok(views.html.tasks.newEntity(taskForm))
  }
  
  def create = Action {
    implicit request =>
      taskForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.tasks.newEntity(formWithErrors)),
        newTask => {
          Tasks.insert(newTask.name, newTask.owner)
          //Workflows.create(newTask.name, List("Open","In Progress","Closed"))
          Redirect(routes.TasksController.show(newTask.name))
        }
      )
  }
    
  def delete(name: String) = Action { 
    implicit request => 
      Tasks.delete(name) 
    Redirect(routes.TasksController.index)
 }
}
