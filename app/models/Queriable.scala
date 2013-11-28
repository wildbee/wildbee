package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.sql.Timestamp
import java.util.Date
import java.util.UUID
import helpers._
import scala.language.reflectiveCalls
/**
 * The Entity trait can be shared by entities in our
 * models since it will define all of the basic CRUD operations
 * in a generalized way.
 */
trait Queriable[T <: AnyRef { val id: UUID; val name: String }] {
  self: Table[T] =>

  def id: Column[UUID]

  def name: Column[String]

  def * : scala.slick.lifted.ColumnBase[T]

  def returnID = * returning id

  /**
   * Return the UUID of the entity with this name in this table.
   */
  def nameToId(name: String): UUID = DB.withSession {
    implicit session: Session =>
      Query(this).where(_.name === name).first.id
  }

  /**
   * Returns the name of the entity given by the string id.
   */
  //def idToName(id: String): String = idToName(uuid(id))

  /**
   * Returns the name of the entity given by this UUID.
   */
  def idToName(id: Any): String = id match {
    case id: String => findById(uuid(id)).name
    case id: UUID => findById(id).name
    case _ => "Unknown"
  }

  /**
   * Returns a list of all entities for this table.
   */
  def findAll: List[T] = DB.withSession {
    implicit session: Session =>
      Query(this).list
  }

  /**
   * Find entity by its id.
   */
  def findById(id: Any): T = {
    def get(id: UUID): T = DB.withSession {
      implicit session: Session =>
        Query(this).where(_.id === id).first
    }
    id match {
      case id: String => get(uuid(id))
      case id: UUID => get(id)
    }
  }


  /**
   * Find an entity by name rather then by UUID.
   */
  def findByName(name: String): T = findById(nameToId(name))

  //  def findMappedById(id: String): Y = DB.withSession {
  //    implicit session: Session =>
  //      tableToQuery(this).filter(_.id === uuid(id)).map(item => mappedEntity).first
  //  }

  /**
   * To use this generalized insert trait, you need to pass
   * the correct case class T into it. That means that the model
   * needs to implement its own mapping from user inputs to
   * case class.
   */
  def insert(item: T) = DB.withSession {
    implicit session: Session =>
      returnID.insert(item)
    }

  /**
   * Same as insert above. Need to map your inputs to the correct
   * class of type T, then pass that into this method.
   */
  def update(id: UUID, item: T) = DB.withSession {
    implicit session: Session =>
      tableQueryToUpdateInvoker(
        tableToQuery(this).where(_.id === id)).update(item)
  }

  /**
   * Delete an entity from its table.
   */
  def delete(id: UUID) = DB.withSession {
    implicit session: Session =>
      queryToDeleteInvoker(
        tableToQuery(this).where(_.id === id)).delete
  }

  /**
   * A map of UUID to name. Useful for filling out
   * combo boxes in forms, for example.
   */
  def mapIdToName: Map[String, String] = DB.withSession {
    implicit session: Session =>
      Query(this).list.map(item => (item.id.toString, item.name)).toMap
  }

  /**
   * UUID string to UUID.
   */
  def uuid(id: String): UUID = {
    Config.pkGenerator.fromString(id)
  }

  /**
   * Helper for generating a current time.
   */
  def currentTimestamp: Timestamp = {
    new Timestamp((new Date()).getTime())
  }

  /**
   * Helper for generating a new UUID.
   */
  def newId: UUID = {
    Config.pkGenerator.newKey
  }

  /**
   * Helper method to delete all the rows in a table
   */
  def deleteAll: Unit = DB.withSession {
    implicit session: Session =>
        tableToQuery(this).delete
  }
}
