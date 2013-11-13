package controllers

import play.api._
import play.api.mvc._
import views._

import models._
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.Database.threadLocalSession
import scala.slick.driver.PostgresDriver.simple._

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._


object TaskController extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())
  
  val taskForm = Form(
	  mapping(
	    "name" -> nonEmptyText,
	    "owner" -> nonEmptyText
	)(Task.apply)(Task.unapply))
  
	val workForm = Form(
	  mapping(
	    "stage" -> list(text)
	  )(Workflows.apply)(Workflows.unapply))
	
  def index = Action {
    database withSession {
      val results = for (p <- Tasks) yield p
      val tasks = results.list
      val joins = for {
        t <- Tasks
        u <- t.owner
        s <- t.status
      } yield (u.name, s.status)
      
      val owners = joins   map { _._1 }
      val statuses = joins map { _._2 }
      
      val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
      Ok(views.html.tasks.New("Testing Grounds", taskForm, workForm, tasks, owners.list, statuses.list, availableStatuses))
    }
  }

  def create() = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Creating Task :: " + errors)),
      t => {
        database withSession { 
          Tasks.create(t.name, t.owner) 
          PackageStatuses.create(t.name, "Open")                        //Move to package
          Workflows.create(t.name, List("Open","In Progress","Closed")) //Default workflow
        }
        Redirect(routes.TaskController.index)
      }
    )    
  }
    
  /** Move to packages, this updates the a package's status */
  def updateTask() = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Creating Task :: " + errors)),
      t => {
        database withSession { 
          Tasks.update(t.owner)
          val task = Tasks.where { _.name === t.name }
          val taskId = (task map { _.id }).list.head
          PackageStatuses.update(t.name)
        }
        Redirect(routes.TaskController.index)
      }
    )  
  }

  def deleteTask() = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Deleting Task :: " + errors)),
      t => {
        database withSession { 
          PackageStatuses.delete(t.name) //Should be a package thing
          Workflows.delete(t.name)
          Tasks.delete(t.name) 
        }
        Redirect(routes.TaskController.index)
      }
    )
  }
  
  def updateWorkflow(task :String) = Action { implicit request =>
    workForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Updating :: " + errors)),
      w => {
        database withSession { Workflows.create(task, w.stage) }
        Redirect(routes.TaskController.index)
      }
    )   
  }
}