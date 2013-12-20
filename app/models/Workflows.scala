package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.Random
import java.util.UUID
import scala.language.postfixOps
import models.traits.Queriable
import play.api.db.slick.DB
import play.api.Play.current
import models.traits.Observable

case class NewWorkflow(name: String, status: List[String]) extends NewEntity
case class Workflow(id: UUID, name: String, startStatus: UUID) extends Entity
object Workflows extends Table[Workflow]("workflows")
  with Queriable[Workflow,NewWorkflow]
  with EntityTable[Workflow, NewWorkflow]
  with UniquelyNamedTable[Workflow,NewWorkflow]
  {

  def startStatus = column[UUID]("start_status")

  def * = id ~ name ~ startStatus <> (Workflow, Workflow.unapply _)
  def autoId = id ~ name ~ startStatus returning id

  /**
   * Implements Queriable's mapToEntity.
   * @param w
   * @param nid
   * @return
   */
  def mapToEntity(w: NewWorkflow, nid: UUID = newId): Workflow = {
    Workflow(nid, w.name, uuid(w.status(0)))
  }

  /**
   * Implements Queriable's mapToNew.
   * @param id
   * @return
   */
  def mapToNew(id: UUID): NewWorkflow = {
    val w = find(id)
    val transitions = Transitions.transitionMap(id)
    val statuses = (transitions.keys map (_.toString) ).toList
    NewWorkflow(w.name, statuses)
  }

  /** create transitions after inserting a new workflow */
  override def afterInsert(id: UUID, workflow: NewWorkflow) = {
    play.api.Logger.debug("Workflow override afterInsert Lifecycle Op on " + workflow.name)
    Transitions.create(id,  workflow.status)
  }

  /** update the transitions before doing a workflow update **/
  override def beforeUpdate(id: UUID, workflow: NewWorkflow) {
    play.api.Logger.debug("Workflow override beforeUpdate Lifecycle Op on " + workflow.name)
    Transitions.create(id, workflow.status)
  }

  /** When deleting a workflow delete its logic first */
  override def beforeDelete(id: UUID) {
    play.api.Logger.debug("Workflow override beforeDelete Lifecycle Op on " + id.toString)
    Transitions.delete(id)
  }

  /**
   * We should try to take this logic out of delete and put it into either:
   * the lifecycle beforeDelete, or create a new set of traits for input validation.
   */
  def delete(name: String): Option[String] = DB.withSession {
    implicit session: Session =>
      val dependentTasks = Tasks.findAll filter ( _.workflow == nameToId(name))
      if(!dependentTasks.isEmpty)
        Some(dependentTasks map (_.name) mkString("[",",","]"))
      else {
        Transitions.delete(nameToId(name))
        (Workflows filter (_.name === name)).delete
        None
      }
  }
}
