package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.UUID

case class Task(id: UUID, name: String, owner: UUID)

case class NewTask(name: String, owner: String)

object Tasks extends Table[Task]("tasks") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def owner = column[UUID]("owner_id")
  def ownerFk = foreignKey("owner_fk", owner, Users)(_.id)
  def uniqueName = index("idx_name", name, unique = true)

  def * = id ~ name ~ owner <>(Task, Task.unapply _)
  private def autoId = id ~ name ~ owner returning id

  def insert(name: String, owner: String) = DB.withSession {
    implicit session: Session =>
      autoId.insert(Config.pkGenerator.newKey,
                    name,
                    Config.pkGenerator.fromString(owner))
  }

  def findAll: List[Task] = DB.withSession {
    implicit session: Session =>
      Query(this).list
  }

  def getTaskMap: Map[String, String] = DB.withSession {
    implicit session: Session =>
      Query(Tasks).list.map(t => (t.id.toString, t.name)).toMap
  }

  def findByTask(taskName: String): Task = DB.withSession {
    implicit session: Session =>
      Query(Tasks).where(_.name === taskName).first
  }

}
