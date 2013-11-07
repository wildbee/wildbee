package models

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp
import java.util.Date

//TODO: Make a controller for the Tasks
object Tasks extends Table[(Long, Long, String, Timestamp, Timestamp)]("tasks") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def ownerId = column[Long]("OWNER")
  def status = column[String]("STATUS") //TODO: Maybe use a type Status rather than a String? Links to possible Statuses.
  def creationTime = column[Timestamp]("CREATION_TIME", O.NotNull)
  def lastUpdated = column[Timestamp]("LAST_UPDATED", O.NotNull)
  def ownerName = foreignKey("fk_usr_location", ownerId, Users)(_.id)
  
  def * =  id ~ ownerId ~ status ~ creationTime ~ lastUpdated
  def autoInc = ownerId ~ status ~ creationTime ~ lastUpdated returning id
  
  /** YYYY-MM-DD HH:MM:SS.MS */
  def currentTime = { 
    def date = new java.util.Date()
    new Timestamp(date.getTime())
  }
  
  def create(owner: Long, status: String)(implicit session: Session) = {
    autoInc.insert(owner, status, currentTime, currentTime)  
  } 
  
  def update(taskId: Long, status: String)(implicit session: Session) = { //Is this the best way?
    Tasks.where(_.id === taskId) 	// Find row with matching task
    .map(_.lastUpdated)				// Get column "lastUpdated"
    .update(currentTime)			// Update with current time
  }
  
  def delete(taskId: Long)(implicit session: Session) = {
    Tasks where { _.id === taskId } delete
  }
}
