package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.Database.threadLocalSession
import scala.slick.driver.PostgresDriver.simple._
import play.api.data._
import play.api.data.Forms._
import java.util.UUID
import helpers.UUIDGenerator

object PackagesController extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())
  
  /**
  val packageForm = Form(
		  mapping(
		      "name" -> nonEmptyText,
		      "owner" -> of[UUID],
		      "assignee" -> of[UUID],
		      "cc_list"-> of[List[String]],
		      "status" -> of[String],
		      "os_version" -> of[String]
		      ) (Package.apply)(Package.unapply)
      )**/
  def index = TODO
  
  def show(id: String) = TODO
  
  def edit(id: String) = TODO 
  
  def update(id: String) = TODO
  
  def create = TODO
  
}