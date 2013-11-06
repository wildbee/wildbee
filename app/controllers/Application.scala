package controllers

import play.api._
import play.api.mvc._
import views._

import models._
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.Database.threadLocalSession
import scala.slick.driver.PostgresDriver.simple._

object Application extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())

  def index = Action {
    database withSession {
      Users.insert("Dustino", "dcheung@redhat.com", false)
    }
    Ok(views.html.index("Your new application is ready, well maybe"))
  }
  
  def test = Action {
    database withSession {
      Tasks.create(1)
      val results = for (p <- Tasks) yield p
      val tasks = results.list.toString
      val query = results.selectStatement.toString
      
      val owners = for {
        t <- Tasks
        u <- t.ownerName
      } yield u
      val ownerName = owners.list.toString
      
      Ok(views.html.test("Testing Grounds", tasks, query, ownerName))
    }
    
  }
}
