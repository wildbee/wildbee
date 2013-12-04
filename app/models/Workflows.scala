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
    // TODO: Add logic to grab all statuses, not just starting one.
    NewWorkflow(w.name, List(w.startStatus.toString))
  }

  def insert(w: NewWorkflow): UUID = {
    insertWithId(newId, w)
  }

  def insertWithId(id: UUID, w: NewWorkflow): UUID = {
    Workflows.insert(
      Workflow(id, w.name, uuid(w.status(0)))
    )
  }

  def delete(name: String): Unit = DB.withSession {
    implicit session: Session =>
      Workflows filter (_.name === name) delete
  }

  def updateWorkflow(id: UUID, w: NewWorkflow, o: Workflow) =
    update(id, Workflow(id, w.name, uuid(w.status(0))))
}
