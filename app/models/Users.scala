package models

import scala.slick.driver.PostgresDriver.simple._
import java.util.UUID
import helpers._

/**
 * Entity model for wildbee_user
 *
 * Note: cannot name the table as simply 'user' since it conflicts
 * with the 'user' table already created in the database by default
 */
case class User(name: String, email: String)
object Users extends Table[(UUID, String, String)]("users") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def email = column[String]("email")
  def uniqueEmail = index("idx_email", email, unique = true)
  def * =  id ~ name ~ email
  def autoEmail = id ~ name ~ email returning email
  
  def getUserId(name: String)(implicit session: Session) = Users
      .filter(_.name === name ) //Find the corresponding user from Users table
      .map (_.id)               //Find get the user id
      .list.head                //Convert Column[UUID] into an UUID

  def insert(name: String, email: String)(implicit session: Session) = {
    autoEmail.insert(Config.pkGenerator.newKey, name, email)
  }

  def insert(u: User)(implicit session: Session) = {
    autoEmail.insert(Config.pkGenerator.newKey, u.name, u.email)
  }
}
