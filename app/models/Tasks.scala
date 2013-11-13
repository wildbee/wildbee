package models

import scala.slick.driver.PostgresDriver.simple._
import helpers._

import java.util.UUID
import java.sql.Timestamp
import java.util.Date

case class Tasks(name: String, owner: String)
                 
//TODO: Make a controller for the Tasks
object Tasks extends Table[(UUID, UUID, String, Timestamp, Timestamp)]("tasks") {
  def id           = column[UUID]("id", O.PrimaryKey)
  def owner        = column[UUID]("owner_id")
  def name				 = column[String]("name")
  def creationTime = column[Timestamp]("creation_time", O.NotNull)
  def lastUpdated  = column[Timestamp]("last_updated", O.NotNull)
  
  def ownerName     = foreignKey("owner_fk", owner, Users)(_.id)
  def status        = foreignKey("fk_status", task, PackageStatuses)(_.task) 
  
  def * =  id ~ owner ~ name ~ creationTime ~ lastUpdated
  def autoId = id ~ owner ~ name ~ creationTime ~ lastUpdated returning id

  /** YYYY-MM-DD HH:MM:SS.MS */
  def currentTime = { 
    def date = new java.util.Date()
    new Timestamp(date.getTime())
  }
  
  //TODO: Add validation to check if the owner exists
  def create(task: String, owner: String)(implicit session: Session) = {
    autoId.insert(Config.pkGenerator.newKey, Config.pkGenerator.fromString(owner), task, currentTime, currentTime)  
  } 

  
  def update(owner: String)(implicit session: Session) = {
    val id = Config.pkGenerator.fromString(owner)
    val task = Tasks filter (_.id === id)
    task map (_.lastUpdated) update(currentTime)
  }
  
  def delete(task: String)(implicit session: Session) = {
    val id = Config.pkGenerator.fromString(task)
    Tasks where { _.id === id } delete
  }
}
