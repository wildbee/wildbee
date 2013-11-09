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
    Ok(views.html.index("Your new application is ready, well maybe"))
  }
  

}
