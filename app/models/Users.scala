package models

import scala.slick.driver.PostgresDriver.simple._
import java.util.UUID

case class User(name: String, email: String)

object Users extends Table[(UUID, String, String)]("users") {
  def uuid = column[UUID]("uuid", O.PrimaryKey)
  def name = column[String]("name")
  def email = column[String]("email")

  def * =  uuid ~ name ~ email
  private def autoUUID = uuid ~ name ~ email returning uuid

  def insert(name: String, email: String)
            (implicit session: Session) = autoUUID.insert(UUID.randomUUID(), name, email)

  def insert(u: User)(implicit session: Session) = autoUUID.insert(UUID.randomUUID(), u.name, u.email)
}
