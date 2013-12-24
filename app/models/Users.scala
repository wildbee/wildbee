package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import java.util.UUID
import helpers._
import models.traits.CRUDOperations

case class NewUser(name: String, email: String) extends NewEntity

case class User(id: UUID, name: String, email: String) extends Entity

/**
 * Entity model for wildbee_user
 *
 * Note: cannot name the table as simply 'user' since it conflicts
 * with the 'user' table already created in the database by default
 */
object Users extends Table[User]("users")
  with CRUDOperations[User, NewUser]
  with EntityTable[User, NewUser]
  with MapsToIdsToNames[User]{
  def email = column[String]("email")
  def uniqueEmail = index("idx_email", email, unique = true)
  def * = id ~ name ~ email <>(User, User.unapply _)
  def autoEmail = id ~ name ~ email returning email

  def insert(name: String, email: String) = DB.withSession {
    implicit session: Session =>
      autoEmail.insert(Config.pkGenerator.newKey, name, email)
  }

  /**
   * Implements Queriable's mapToEntity.
   * @param u
   * @param nid
   * @return
   */
  def mapToEntity(u: NewUser, nid: UUID = newId): User = {
    User(nid, u.name, u.email)
  }

  /**
   * Implements Queriable's mapToNew.
   * @param id
   * @return
   */
  def mapToNew(id: UUID): NewUser = {
    val u = find(id)
    NewUser(u.name, u.email)
  }

 def insertWithId(id: UUID, u: NewUser): UUID = {
    Users.insert(User(id, u.name, u.email))
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
}