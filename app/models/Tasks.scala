package models

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp
import java.util.Date

case class Tasks(ownerId: Long, status: String)
                 
//TODO: Make a controller for the Tasks
object Tasks extends Table[(Long, Long, String, Timestamp, Timestamp)]("tasks") {
  def id           = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def ownerId      = column[Long]("owner")
  def status       = column[String]("status") //TODO: Maybe use a type Status rather than a String?
  def creationTime = column[Timestamp]("creation_time", O.NotNull)
  def lastUpdated  = column[Timestamp]("last_updated", O.NotNull)
  def ownerName    = foreignKey("fk_owner", ownerId, Users)(_.id)
  
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
  
  def update(taskId: Long, status: String)(implicit session: Session) = {
    val task = Tasks filter (_.id === taskId)
    task map ( _.status      ) update(status)
    task map ( _.lastUpdated ) update(currentTime)
  }
  
  def delete(taskId: Long)(implicit session: Session) = {
    Tasks where { _.id === taskId } delete
  }
}
