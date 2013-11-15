package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import java.util.UUID
import helpers._

case class NewUser(name: String, email: String)

case class User(id: UUID, name: String, email: String)

/**
 * Entity model for wildbee_user
 *
 * Note: cannot name the table as simply 'user' since it conflicts
 * with the 'user' table already created in the database by default
 */
object Users extends Table[User]("users") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def email = column[String]("email")
  def uniqueEmail = index("idx_email", email, unique = true)

  def * = id ~ name ~ email <>(User, User.unapply _)
  def autoEmail = id ~ name ~ email returning email

  def insert(name: String, email: String) = DB.withSession {
    implicit session: Session =>
      autoEmail.insert(Config.pkGenerator.newKey, name, email)
  }

  def findAll: List[User] = DB.withSession {
    implicit session: Session =>
      Query(this).list
  }

  def findByEmail(email: String): User = DB.withSession {
    implicit session: Session =>
      Query(this).where(_.email === email).first
  }

  def update(oldEmail: String, editUser: NewUser): User = DB.withSession {
    implicit session: Session =>
      val query = Query(this).where(_.email === email)
      val user = query.first
      query.update(User(user.id, editUser.name, editUser.email))
      findByEmail(editUser.email)
  }

  def getUserMap: Map[String, String] = DB.withSession {
    implicit session: Session =>
      Query(Users).list.map(u => (u.id.toString, u.name)).toMap
  }

}
