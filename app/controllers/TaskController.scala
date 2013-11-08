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
    "status" -> nonEmptyText
  )(Tasks.apply)(Tasks.unapply))
      
  def index = Action {
    database withSession {
      val results = for (p <- Tasks) yield p

      val tasks = results.list
      
      val owners = for {
        t <- Tasks
        u <- t.ownerName
      } yield u.name

      Ok(views.html.tasks("Testing Grounds", taskForm, tasks, owners.list))
    }
  }
  
  def createTask() = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Creating Task :: " + errors)),
      task => {
        database withSession { Tasks.create(task.ownerId, task.status) }
        Redirect(routes.TaskController.index)
      }
    )    
  }
    
  def updateTask() = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Creating Task :: " + errors)),
      task => {
        database withSession { Tasks.update(task.ownerId, task.status) }
        Redirect(routes.TaskController.index)
      }
    )  
  }
  

  def deleteTask() = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("Error Deleting Task :: " + errors)),
      task => {
        database withSession { Tasks.delete(task.ownerId) }
        Redirect(routes.TaskController.index)
      }
    )
  }
  
  def initStatuses() = Action { implicit request =>
    database withSession { Statuses.create }
    Redirect(routes.TaskController.index)
  }
}