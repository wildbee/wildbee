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
import play.api.data.format.Formats._ //To get a Long in a form


object TaskController extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())
  
  val taskForm = Form(
	  mapping(
	    "owner" -> of[Long],
	    "task" -> nonEmptyText
	)(Tasks.apply)(Tasks.unapply))
  
	/** Testing stuff */
	val workForm = Form(
	    mapping(
	  "stage1" -> text,
	  "stage2" -> text,
	  "stage3" -> text
	  )(Workflows.apply)(Workflows.unapply))
	
  def index = Action {
    database withSession {
      val results = for (p <- Tasks) yield p
      val tasks = results.list
      val joins = for {
        t <- Tasks
        u <- t.ownerName
        s <- t.status
      } yield (u.name, s.status)
      
      val owners = joins   map { _._1 }
      val statuses = joins map { _._2 }
      println("Statuses :: " + statuses.list.reverse)
      println("Owners :: " + owners.list.reverse)
      
      val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
      Ok(views.html.tasks("Testing Grounds", taskForm, workForm, tasks, owners.list.reverse, statuses.list.reverse, availableStatuses))
    }
  }
  
  def createTask() = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Creating Task :: " + errors)),
      t => {
        database withSession { 
          //TODO: Change this to use uuid, this looks fragile
          Tasks.create(t.ownerId, t.task) 
          StatusStates.create(t.task, "Open")
          Workflows.create(t.task, "Open,In Progress,Closed")//Default workflow
        }
        Redirect(routes.TaskController.index)
      }
    )    
  }
    
  def updateTask() = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Creating Task :: " + errors)),
      t => {
        database withSession { 
          Tasks.update(t.ownerId)
          val task = Tasks.where { _.task === t.task }
          val taskId = (task map { _.id }).list.head
          StatusStates.update(taskId) //TODO: Seems wrong should be interacing with workflows!
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
          Tasks.delete(t.ownerId) 
          Workflows.delete(t.ownerId)
          StatusStates.delete(t.ownerId)
        }
        Redirect(routes.TaskController.index)
      }
    )
  }
  
  //TODO: Need some sort of validation
  //If you change the work flow from A -> B -> C to A -> E -> F
  //What happens if your package was in state B?
  def updateWorkflow(id :Long) = Action { implicit request =>
    workForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Creating Task :: " + errors)),
      w => {
        database withSession { Workflows.defineLogic(id, w.stage1, w.stage2, w.stage3) }
        Redirect(routes.TaskController.index)
      }
    )   
  }
}