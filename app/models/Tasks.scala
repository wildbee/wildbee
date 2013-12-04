package models

import play.api.db.slick.Config.driver.simple._
import java.util.UUID
import java.sql.Timestamp
import scala.language.postfixOps
import models.traits.Queriable

case class NewTask(name: String, owner: String, workflow: String) extends NewEntity
case class Task(id: UUID, name: String, owner: UUID,
  created: Timestamp, workflow: UUID, updated: Timestamp) extends Entity with Timekeeping

object Tasks extends Table[Task]("tasks")
  with Queriable[Task,NewTask]
  with EntityTable[Task, NewTask]
  with TimekeepingTable[Task]
  with UniquelyNamedTable[Task,NewTask] {

  def owner = column[UUID]("owner_id")
  def ownerFk = foreignKey("owner_fk", owner, Users)(_.id)
  def workflow = column[UUID]("workflow_id")
  def * = id ~ name ~ owner ~ created ~ workflow ~ updated <> (Task, Task.unapply _)

  /**
   * Implements Queriable's mapToEntity requirement.
   * @param t
   * @param nid
   * @return
   */
  def mapToEntity(t: NewTask, nid: UUID = newId): Task = {
    Task(nid,t.name, uuid(t.owner), currentTimestamp, uuid(t.workflow),
      currentTimestamp)
  }

  /**
   * Implements Queriable's mapToNew requirement.
   * @param id
   * @return
   */
  def mapToNew(id: UUID): NewTask = {
    val t = find(id)
    NewTask(t.name, t.owner.toString, t.workflow.toString)
  }

  /**
   * Returns the UUID of the starting status for this task.
   * @param id
   * @return
   */
  def getStartingStatus(id: UUID): UUID = {
    val workflow = Tasks.find(id).workflow
    Workflows.find(workflow).startStatus
  }
}
