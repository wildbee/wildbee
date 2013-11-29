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
case class Task(id: UUID, name: String, owner: UUID,
  creationTime: Timestamp, workflow: UUID, lastUpdated: Timestamp)

object Tasks extends Table[Task]("tasks") with Queriable[Task] {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def owner = column[UUID]("owner_id")
  def creationTime = column[Timestamp]("creation_time", O.NotNull)
  def lastUpdated = column[Timestamp]("last_updated", O.NotNull)
  def ownerFk = foreignKey("owner_fk", owner, Users)(_.id)
  def workflow = column[UUID]("workflow_id")
  def uniqueName = index("idx_name", name, unique = true)

  def * = id ~ name ~ owner ~ creationTime ~ workflow ~ lastUpdated <> (Task, Task.unapply _)
  private def autoId = id ~ name ~ owner ~ creationTime ~ lastUpdated returning id

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
    Tasks.insert(Task(
      id,
      t.name,
      uuid(t.owner),
      currentTimestamp,
      uuid(t.workflow),
      currentTimestamp))
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

  /**
   * Returns a mapping of name => UUID of all allowed statuses
   * for this task.
   * @param task
   * @return
   */
  def allowedStatuses(task: AnyRef): Map[String, String] = {
    def as(t: UUID) = {
      val workflow = Tasks.findById(t).workflow
      Transitions.allowedStatusesMap(workflow)
    }
    task match {
      case task: String => as(uuid(task))
      case task: UUID => as(task)
    }
  }
}
