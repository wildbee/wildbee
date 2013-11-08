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
      val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
      
      Ok(views.html.tasks("Testing Grounds", taskForm, tasks, owners.list, statuses.list, availableStatuses))
    }
  }
  
  def createTask() = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Creating Task :: " + errors)),
      t => {
        database withSession { 
          Tasks.create(t.ownerId, t.task) 
          StatusStates.create(t.task)
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
          StatusStates.update(t.task) //TODO: Seems wrong should be interacing with workflows!
        }
        Redirect(routes.TaskController.index)
      }
    )  
  }
  

  def deleteTask() = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Deleting Task :: " + errors)),
      t => {
        database withSession { Tasks.delete(t.ownerId) }
        Redirect(routes.TaskController.index)
      }
    )
  }
  
  def updateStatus() = Action { implicit request =>
    Redirect(routes.TaskController.index)
  }
}