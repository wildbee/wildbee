package models

import scala.slick.driver.PostgresDriver.simple._
import helpers._

import java.util.UUID
import java.sql.Timestamp
import java.util.Date

case class Task(name: String, owner: String)
                 
object Tasks extends Table[(UUID, String, String, Timestamp, Timestamp)]("tasks") {
  def id           = column[UUID]("id", O.PrimaryKey)
  def ownerName    = column[String]("owner")
  def name				 = column[String]("name")
  def creationTime = column[Timestamp]("creation_time", O.NotNull)
  def lastUpdated  = column[Timestamp]("last_updated", O.NotNull)
  def uniqueName   = index("idx_name", name, unique = true)
  
  def owner         = foreignKey("owner_fk", ownerName, Users)(_.name)
  def status        = foreignKey("fk_status", name, PackageStatuses)(_.task) 
  
  def *      = id ~ ownerName ~ name ~ creationTime ~ lastUpdated
  def autoId = id ~ ownerName ~ name ~ creationTime ~ lastUpdated returning name
	      
  def getTaskId(name: String)(implicit session: Session) = Tasks
      .filter (_.name === name) //Find the corresponding task from Task table
      .map (_.id)               //Find get the task id
      .first                    //Convert Column[UUID] into an UUID
	      
  /** YYYY-MM-DD HH:MM:SS.MS */
  def currentTime = { 
    def date = new java.util.Date()
    new Timestamp(date.getTime())
  }
  
  //TODO: Add validation to check if the owner exists
  def create(task: String, owner: String)(implicit session: Session) = {	
    autoId.insert(Config.pkGenerator.newKey, owner, task, currentTime, currentTime)  
  } 
  
  def update(name: String)(implicit session: Session) = {
    val task = Tasks filter (_.name === name)
    task map (_.lastUpdated) update (currentTime)
  }
  
  def delete(task: String)(implicit session: Session) = {
    Tasks where ( _.name === task ) delete
  }
}
