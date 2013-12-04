package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.UUID
import java.sql.Timestamp
import java.util.Date
import scala.language.postfixOps

case class NewTask(name: String, owner: String, workflow: String)
case class Task(id: UUID, name: String, owner: UUID, workflow: UUID,
  creationTime: Timestamp, lastUpdated: Timestamp)

object Tasks extends Table[Task]("tasks") with Queriable[Task, NewTask] {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def owner = column[UUID]("owner_id")
  def creationTime = column[Timestamp]("creation_time", O.NotNull)
  def lastUpdated = column[Timestamp]("last_updated", O.NotNull)
  def workflow = column[UUID]("workflow_id")
  def uniqueName = index("idx_name", name, unique = true)
  def ownerFk = foreignKey("owner_fk", owner, Users)(_.id)

  def * = id ~ name ~ owner ~ workflow ~ creationTime ~ lastUpdated <> (Task, Task.unapply _)
  private def autoId = id ~ name ~ owner ~ creationTime ~ lastUpdated returning id

  def mapToNew(id: UUID): NewTask = {
    val t = find(id)
    NewTask(t.name, t.owner.toString, t.workflow.toString)
  }

  /** YYYY-MM-DD HH:MM:SS.MS */
  def currentTime = {
    def date = new java.util.Date()
    new Timestamp(date.getTime())
  }

  def getTaskMap: Map[String, String] = DB.withSession {
    implicit session: Session =>
      Query(Tasks).list.map(t => (t.id.toString, t.name)).toMap
  }

  def insertWithId(id: UUID, t: NewTask): UUID = {
    Tasks.insert(Task(id, t.name, uuid(t.owner), uuid(t.workflow), currentTimestamp, currentTimestamp))
  }

  def insert(t: NewTask): UUID = {
    insertWithId(newId, t)
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

  /**
   * Returns the UUID of the starting status for this task.
   * @param id
   * @return
   */
  def getStartingStatus(id: UUID): UUID = {
    val workflow = Tasks.findById(id).workflow
    Workflows.findById(workflow).startStatus
  }
}
