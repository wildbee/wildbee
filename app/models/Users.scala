package models

import scala.slick.driver.PostgresDriver.simple._
import java.util.UUID
import helpers.UUIDGenerator

case class User(name: String, email: String)

/**
 * Entity model for wildbee_user
 *
 * Note: cannot name the table as simply 'user' since it conflicts
 * with the 'user' table already created in the database by default
 */
object Users extends Table[(UUID, String, String)]("wildbee_user") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def email = column[String]("email")

  def * =  id ~ name ~ email
  def autoEmail = id ~ name ~ email returning email

  def insert(name: String, email: String)
            (implicit session: Session) = autoEmail.insert(UUID.randomUUID(), name, email)

  def insert(u: User)(implicit session: Session) = autoEmail.insert(UUIDGenerator.getUUID, u.name, u.email)
}
