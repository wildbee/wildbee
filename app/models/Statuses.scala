package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.Random
import java.util.UUID
import scala.language.postfixOps

case class NewStatus(name: String)
case class Status(id: UUID, name: String)
object Statuses extends Table[Status]("statuses") with Queriable[Status] {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")

  def uniqueName = index("idx_status_name", name, unique = true)

  def * = id ~ name  <> (Status, Status.unapply _)
  def autoId = id ~ name returning id

  def insert(s: NewStatus): UUID = {
    insertWithId(newId, s)
  }

  def insertWithId(id: UUID, p: NewStatus): UUID = {
    Statuses.insert(Status(id, p.name))
  }

  def mapToNewStatus(id: String): NewStatus = {
    val s = findById(id)
    NewStatus(s.name)
  }

  def updateStatus(id: UUID, s: NewStatus, o: Status) =
    update(id, Status(id, s.name))
}
