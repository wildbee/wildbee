package models

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp
import java.util.Date

case class Tasks (id:Long, owner:String, creationTime:Timestamp, lastUpdated:Timestamp)

object Tasks extends Table[(Long, String, Timestamp, Timestamp)]("tasks") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def owner = column[String]("OWNER")
  def creationTime = column[Timestamp]("CREATION_TIME", O.NotNull)
  def lastUpdated = column[Timestamp]("LAST_UPDATED", O.NotNull)
  def * =  id ~ owner ~ creationTime ~ lastUpdated
  
  def autoInc = owner ~ creationTime ~ lastUpdated returning id
  
  def create(owner: String)(implicit session: Session) = {
    def date = new java.util.Date()
    def time = new Timestamp(date.getTime())
    autoInc.insert(owner, time, time)  //YYYY-MM-DD HH:MM:SS.MS
  } 
}
