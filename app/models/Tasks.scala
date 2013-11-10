package models

import scala.slick.driver.PostgresDriver.simple._
import java.util.UUID

case class Task(name: String, owner: String)

object Tasks extends Table[(UUID, String, UUID)]("tasks") {
  def uuid = column[UUID]("uuid", O.PrimaryKey)
  def name = column[String]("name")
  def owner = column[UUID]("owner_uuid")
  def owner_fk = foreignKey("owner_fk", owner, Users)(_.uuid)

  def * =  uuid ~ name ~ owner
  private def autoUUID = uuid ~ name ~ owner returning uuid

  def insert(name: String, owner: String)
            (implicit session: Session) = autoUUID.insert(UUID.randomUUID(), name, UUID.fromString(owner))
}
