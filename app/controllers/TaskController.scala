package controllers

import play.api._
import play.api.mvc._
import views._

import models._
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.Database.threadLocalSession
import scala.slick.driver.PostgresDriver.simple._

//TODO: What are the naming conventions?
//TODO: When do you decide to create a new controller?
object TaskController extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())
  
  def index = Action {
    database withSession {
      val results = for (p <- Tasks) yield p
      val tasks = results.list.toString
      val query = results.selectStatement.toString
      
      val owners = for {
        t <- Tasks
        u <- t.ownerName
      } yield u
      
      val ownerName = owners.list.toString
      Ok(views.html.tasks("Testing Grounds", tasks, ownerName))
    }
  }
  
  def updateTask() = Action { implicit request =>
    database withSession {
      Tasks.update(8, "Old")
    }
     Redirect(routes.TaskController.index)
  }
  
  def createTask() = Action { implicit request =>
    database withSession {
      Tasks.create(1, "New")
    }
    Redirect(routes.TaskController.index)
  }
  
   def deleteTask() = Action { implicit request =>
    database withSession {
      Tasks.delete(9)
    }
    Redirect(routes.TaskController.index)
  }
}