package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.Random
import java.util.UUID
import scala.language.postfixOps
import models.traits.CRUDOperations
import play.api.db.slick.DB
import play.api.Play.current
import models.traits.Observable

case class NewWorkflow(name: String, status: List[String]) extends NewEntity
case class Workflow(id: UUID, name: String, startStatus: UUID) extends Entity
object Workflows extends Table[Workflow]("workflows")
  with CRUDOperations[Workflow,NewWorkflow]
  with EntityTable[Workflow, NewWorkflow]
  with UniquelyNamedTable[Workflow,NewWorkflow]
  with MapsIdsToNames[Workflow]{

  def startStatus = column[UUID]("start_status")

  def * = id ~ name ~ startStatus <> (Workflow, Workflow.unapply _)
  def autoId = id ~ name ~ startStatus returning id

  /**
   * Implements Queriable's mapToEntity.
   * @param w
   * @param nid
   * @return
   */
  def mapToEntity(nid: UUID = newId, w: NewWorkflow ): Workflow = {
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

  /** custom delete validator */
  override def deleteValidator(item: AnyRef): Option[String] = {
    val uid = findUUID(item)
    val dependentTasks = Tasks.findAll filter( _.workflow == uid)
    if(!dependentTasks.isEmpty)
      Some(dependentTasks map (_.name) mkString("[",",","]"))
    else {
      Transitions.delete(uid)
      None
    }
  }
}
