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
}
