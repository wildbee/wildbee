package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.UUID
import java.sql.Timestamp
import java.util.Date
import scala.language.postfixOps

case class NewTask(name: String, owner: String)
case class Task(id: UUID, name: String, owner: UUID,
    creationTime: Timestamp, lastUpdated: Timestamp)

object Tasks extends Table[Task]("tasks") with Queriable[Task]{
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def owner = column[UUID]("owner_id")
  def creationTime = column[Timestamp]("creation_time", O.NotNull)
  def lastUpdated  = column[Timestamp]("last_updated", O.NotNull)
  def ownerFk = foreignKey("owner_fk", owner, Users)(_.id)
  def uniqueName = index("idx_name", name, unique = true)

  def * = id ~ name ~ owner ~ creationTime ~ lastUpdated <>(Task, Task.unapply _)
  private def autoId = id ~ name ~ owner ~ creationTime ~ lastUpdated returning id

  /** YYYY-MM-DD HH:MM:SS.MS */
  def currentTime = {
    def date = new java.util.Date()
    new Timestamp(date.getTime())
  }
  
// This method is implemented in the Queriable trait.
//  def findAll: List[Task] = DB.withSession {
//    implicit session: Session =>
//      Query(this).list
//  }

// This method is implemented in the Queriable trait.  
//  def findByName(taskName: String): Task = DB.withSession {
//    implicit session: Session =>
//      Query(Tasks).where(_.name === taskName).first
//  }

  def getTaskMap: Map[String, String] = DB.withSession {
    implicit session: Session =>
      Query(Tasks).list.map(t => (t.id.toString, t.name)).toMap
  }

  def insert(name: String, owner: String) = DB.withSession {
    implicit session: Session =>
      autoId.insert(Config.pkGenerator.newKey, name,
        Config.pkGenerator.fromString(owner), currentTime, currentTime)
  }

  def update(name: String): Unit = DB.withSession {
    implicit session: Session =>
      val task = Tasks filter (_.name === name)
      task map (_.lastUpdated) update (currentTime)
  }

  def delete(task: String): Unit = DB.withSession {
    implicit session: Session =>
      Tasks where (_.name === task) delete
  }
}
