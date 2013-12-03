package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.UUID
import java.sql.Timestamp
import java.util.Date
import scala.language.postfixOps

case class NewTask(name: String, owner: String, workflow: String) extends NewEntity
case class Task(id: UUID, name: String, owner: UUID,
  created: Timestamp, workflow: UUID, updated: Timestamp) extends Entity with Timekeeping

object Tasks extends Table[Task]("tasks") with Queriable[Task,NewTask] with EntityTable[Task] with TimekeepingTable[Task]{
  def owner = column[UUID]("owner_id")
  def ownerFk = foreignKey("owner_fk", owner, Users)(_.id)
  def workflow = column[UUID]("workflow_id")
  def uniqueName = index("idx_name", name, unique = true)

  def * = id ~ name ~ owner ~ created ~ workflow ~ updated <> (Task, Task.unapply _)
  private def autoId = id ~ name ~ owner ~ created ~ updated returning id


  def mapToEntity(t: NewTask, nid: UUID = newId): Task = {
    Task(
      nid,
      t.name,
      uuid(t.owner),
      currentTimestamp,
      uuid(t.workflow),
      currentTimestamp)
  }

  def mapToNew(id: UUID): NewTask = {
    val t = find(id)
    NewTask(t.name, t.owner.toString, t.workflow.toString)
  }

  /** YYYY-MM-DD HH:MM:SS.MS */
  def currentTime = {
    def date = new java.util.Date()
    new Timestamp(date.getTime())
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
      task map (_.updated) update (currentTime)
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
