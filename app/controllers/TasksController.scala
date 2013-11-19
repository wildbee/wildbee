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


object TasksController extends Controller {
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
      val tasks = for (t <- Tasks) yield t
      val joins = for {
        t <- Tasks
        u <- t.owner
        s <- t.status
      } yield (u.name, s.status)
      
      val owners   = joins map { _._1 }
      val statuses = joins map { _._2 }
      
      val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
      Ok(views.html.tasks.Index("Current Tasks", tasks.list, owners.list, statuses.list))
    }
  }
  
  def newTask = Action {
      Ok(views.html.tasks.New("New Task Form", taskForm))
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
        Redirect(routes.TasksController.show(t.name))
      }
    )    
  }
  
  def show(name: String) = Action {
    database withSession {
      val task   = for { t <- Tasks if t.name === name } yield t 
      val status = for { t <- task 
                         s <- t.status } yield s.status
      val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
      Ok(views.html.tasks.Show("Task View", workForm, availableStatuses, task.first, status.first))
    }
  }
    
  /** Move to packages, this updates the a package's status */
  def update(name: String) = Action { implicit request =>
    database withSession { 
      Tasks.update(name)
      val task = Tasks.where { _.name === name }
      PackageStatuses.update(name)
    }
    Redirect(routes.TasksController.show(name))
  }

  def delete(name: String) = Action { implicit request =>
    database withSession { 
      PackageStatuses.delete(name) //Should be a package thing
      Workflows.delete(name)       //Delete data dependent on a task first
      Tasks.delete(name) 
    }
    Redirect(routes.TasksController.index)
 }
  
  def updateWorkflow(name :String) = Action { implicit request =>
    workForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Updating :: " + errors)),
      w      => database withSession { Workflows.create(name, w.stage) }
    )   
    Redirect(routes.TasksController.show(name))
  }
}
