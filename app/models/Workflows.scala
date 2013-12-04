package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.Random
import java.util.UUID
import scala.language.postfixOps

case class NewWorkflow(name: String, status: List[String])
case class Workflow(id: UUID, name: String, startStatus: UUID)
object Workflows extends Table[Workflow]("workflows") with Queriable[Workflow,NewWorkflow] {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def startStatus = column[UUID]("start_status")

//  def statusFk = foreignKey("status_fk", name, AllowedStatuses)(_.workflow)
  def uniqueName = index("idx_workflow_name", name, unique = true)

  def * = id ~ name ~ startStatus <> (Workflow, Workflow.unapply _)
  def autoId = id ~ name ~ startStatus returning id

  def mapToNew(id: UUID): NewWorkflow = {
    val w = find(id)
    val transitions = Transitions.transitionMap(id)
    val statuses = (transitions.keys map (_.toString) ).toList
    NewWorkflow(w.name, statuses)
  }

  def insert(w: NewWorkflow): UUID = {
    insertWithId(newId, w)
  }

  def insertWithId(id: UUID, w: NewWorkflow): UUID = {
    Workflows.insert(
      Workflow(id, w.name, uuid(w.status(0)))
    )
  }

  def delete(name: String): Option[String] = DB.withSession {
    implicit session: Session =>
      val dependentTasks = Tasks.findAll filter ( _.workflow == name)
      if(!dependentTasks.isEmpty)
        Some(dependentTasks map (_.name) mkString("[",",","]"))
      else {
        Transitions.delete(nameToId(name))
        (Workflows filter (_.name === name)).delete
        None
      }
  }

  /** Define own deleteAll since we want to delete transistions also */
  override def deleteAll() = DB.withSession {
    implicit session: Session =>
      val allNames= this.findAll map (_.name)
      allNames map (name => Transitions.delete(nameToId(name)))
      queryToDeleteInvoker(tableToQuery(this)) delete
  }

  def updateWorkflow(id: UUID, w: NewWorkflow, o: Workflow) ={
    Transitions.create(id, w.status)
    update(id, Workflow(id, w.name, uuid(w.status(0))))
  }
}
