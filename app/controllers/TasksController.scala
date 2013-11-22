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
    
  // TODO: Add workflow to this
  def newTask() = Action {
    Ok(views.html.tasks.newEntity(taskForm))
  }
  // TODO: add update task

  def show(taskName: String) = Action {
    Ok(views.html.tasks.show(Tasks.findById(taskName)))
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
    
  // TODO: Integrate workflow with this
  def delete(name: String) = Action { 
    implicit request => {
       //PackageStatuses.delete(name) //Should be a package thing
      //Workflows.delete(name)         //Delete data dependent on a task first
      Tasks.delete(name) 
    }
    
    Redirect(routes.TasksController.index)
 }
  
  // TODO: review this Dustin!
  // def updateWorkflow(name :String) = Action { implicit request =>
  //  workForm.bindFromRequest.fold(
  //    errors => BadRequest(views.html.index("Error Updating :: " + errors)),
  //    w      => database withSession { Workflows.create(name, w.stage) }
  //  )   
  //  Redirect(routes.TasksController.show(name))
  // }
}
