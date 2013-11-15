package controllers

import play.api._
import play.api.mvc._

import models._
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.Database.threadLocalSession
import scala.slick.driver.PostgresDriver.simple._

// for forms
import play.api.data._
import play.api.data.Forms._

object TasksController extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())

  val taskForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "owner_id" -> nonEmptyText)(Task.apply)(Task.unapply))

  def create = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      formWithErrors => Ok("Are you crazy?"),
      task => {
        database withSession {
          val uuid = Tasks.insert(task.name, task.owner)
          Ok("hello")
        }
      }
    )
  }

  def newTask() = Action {
    database withSession {
      val q = Query(Users).list
      val x = for (c <- q) yield c._1.toString
      val y = for (c <- q) yield c._2
      Ok(views.html.tasks.newTask(taskForm, (x zip y).toMap))
    }
  }


}
