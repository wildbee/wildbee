package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.Random
import java.util.UUID
import scala.language.postfixOps

case class NewWorkflow(name: String, status: List[String])
case class Workflow(id: UUID, name: String)
object Workflows extends Table[Workflow]("workflows") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")

//  def statusFk = foreignKey("status_fk", name, AllowedStatuses)(_.workflow)
  def uniqueName = index("idx_workflow_name", name, unique = true)

  def * = id ~ name  <> (Workflow, Workflow.unapply _)
  def autoId = id ~ name returning id

  def findAll: List[Workflow] = DB.withSession {
    implicit session: Session =>
      Query(this).list
  }

  def findByName(name: String): Workflow = DB.withSession {
    implicit session: Session =>
      Query(Workflows) filter (_.name === name) first
  }

  def create(name: String): Unit = DB.withSession {
    implicit session: Session =>
        autoId.insert(Config.pkGenerator.newKey, name)
  }

  def delete(name: String): Unit = DB.withSession {
    implicit session: Session =>
      Workflows filter (_.name === name) delete
  }
}
