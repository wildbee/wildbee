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
 *
 * T is the case class used to map from a row in the DB table to a scala object.
 * Y is the case class used to map from all string user input to a scala object.
 */
trait Queriable[T <: AnyRef { val id: UUID; val name: String },
                Y <: AnyRef { val name: String }] {

  /**
   * This trait is used by entity models with Tables of type T.
   */
  self: Table[T] =>

  /**
   * The default projection for any entity model table.
   * @return
   */
  def * : scala.slick.lifted.ColumnBase[T]

  /**
   * A default projection that returns the id. Used
   * for inserting new entities.
   * @return
   */
  def returnID = * returning id

  /**
   * All entities must have both a UUID id, and a
   * String name.
   * @return
   */
  def id: Column[UUID]
  def name: Column[String]

  /**
   * This method maps an entity to its new case class.
   * Used to pack forms for updates.
   * @param id
   * @return
   */
  def mapToNew(id: UUID) : Y

  /**
   * Return the UUID of the entity with this name in this table.
   */
  def nameToId(name: String): UUID = DB.withSession {
    implicit session: Session =>
      Query(this).where(_.name === name).first.id
  }

  /**
   * Returns the name of the entity given by this UUID/String id.
   */
  def idToName(id: AnyRef): String = id match {
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
  def findById(id: AnyRef): T = {
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
   * General find query searches by id if given a
   * valid UUID (in UUID or string format), or by
   * name if given a non-uuid string.
   * @param atty
   * @return
   */
  def find(atty: AnyRef): T = atty match {
    case atty: UUID => findById(atty)
    case atty: String => {
      if (vidP(atty)) {
        findById(atty)
      } else {
        findByName(atty)
      }
    }
  }

  /**
   * Find an entity by name rather then by UUID.
   */
  def findByName(name: String): T = findById(nameToId(name))

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

  def deleteAll() = DB.withSession {
    implicit session: Session =>
      queryToDeleteInvoker(tableToQuery(this)) delete
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
   * Shortcut to validate uuid strings.
   * @param id
   * @return
   */
  def vidP(id: String) = Config.pkGenerator.validP(id)

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
}
