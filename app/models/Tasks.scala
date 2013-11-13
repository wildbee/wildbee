package models

import scala.slick.driver.PostgresDriver.simple._
import helpers._
import java.util.UUID

case class Task(name: String, owner: String)

object Tasks extends Table[(UUID, String, UUID)]("tasks") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def owner = column[UUID]("owner_id")
  def ownerFk = foreignKey("owner_fk", owner, Users)(_.id)
  def uniqueName = index("idx_name", name, unique = true)

  def * =  id ~ name ~ owner
  private def autoId = id ~ name ~ owner returning id

  def insert(name: String, owner: String)
            (implicit session: Session) = autoId.insert(Config.pkGenerator.newKey, name, Config.pkGenerator.fromString(owner))
}
