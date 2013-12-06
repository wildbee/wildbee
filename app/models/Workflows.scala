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

case class NewWorkflow(name: String, status: List[String]) extends NewEntity
case class Workflow(id: UUID, name: String, startStatus: UUID) extends Entity
object Workflows extends Table[Workflow]("workflows")
  with Queriable[Workflow,NewWorkflow]
  with EntityTable[Workflow, NewWorkflow]
  with UniquelyNamedTable[Workflow,NewWorkflow] {

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

  // Define own deleteAll since we want to delete transistions also
  /*
  override def deleteAll() = DB.withSession {
    implicit session: Session =>
      val allNames= this.findAll map (_.name)
      allNames map (name => Transitions.delete(nameToId(name)))
      queryToDeleteInvoker(tableToQuery(this)) delete
  }*/
  /*
  def updateWorkflow(id: UUID, w: NewWorkflow, o: Workflow) ={
    Transitions.create(id, w.status)
    update(id, Workflow(id, w.name, uuid(w.status(0))))
  }*/
}
