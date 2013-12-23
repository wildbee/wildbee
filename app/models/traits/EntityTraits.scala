package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.sql.Timestamp
import java.util.Date
import java.util.UUID
import helpers._

/**
 * Table object traits
 */

/**
 * Main trait for any entity model table.
 * @tparam T
 */
trait EntityTable[T <: Entity, Y <: NewEntity] extends Table[T] {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")

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
   * UUID string to UUID.
   */
  def uuid(id: String): UUID = {
    Config.pkGenerator.fromString(id)
  }

  /**
   * All entity table types should implement a method mapping a NewEntity class
   * to an Entity class.
   * @param item
   * @return
   */
  def mapToEntity(item: Y, nid: UUID): T

  /**
   * Must implement a method to map from an entity to a NewEntity.
   * Used to pack forms for updates.
   * @param id
   * @return
   */
  def mapToNew(id: UUID) : Y

  /**
   * This wrapper for mapToNew just handles the pattern matching
   * for passing in a string id or a UUID id.
   * @param id
   * @return
   */
  def mapToNew(id: AnyRef): Y = id match {
    case id: String => mapToNew(uuid(id))
    case id: UUID => mapToNew(id)
  }

  /**
   * Shortcut to validate uuid strings.
   * @param id
   * @return
   */
  def vidP(id: String) = Config.pkGenerator.validP(id)

  /**
   * Helper for generating a new UUID.
   */
  def newId: UUID = {
    Config.pkGenerator.newKey
  }
}

trait UniquelyNamedTable[T <: Entity, Y <: NewEntity] {
  /**
   * This trait is used by entity models with Tables of type T with EntityTable trait of type T.
   */
  self: Table[T] with EntityTable[T, Y] =>

  /**
   * Specifies that this Entity table has unique name constraint.
   * @return
   */
  def uniqueName = index("idx_name_" + idxName, name, unique = true)

  /**
   * Simply finds the suffix for idx_name by grabbing the table name
   * as a string.
   * @return
   */
  private def idxName =
    this.getClass.getName.toString.toLowerCase.
      replace("$","").replace("models.","")
}

trait MapsToIdsToNames[T <: Entity]{

  self: Table[T] =>
  /**
   * A map of UUID to name for all entities in the table. Useful for filling out
   * combo boxes in forms, for example.
   */
  def mapIdToName: Map[String, String] = DB.withSession {
    implicit session: Session =>
      Query(this).list.map(item => (item.id.toString, item.name)).toMap
  }
}

/**
 * Used by models that keep time in their respective db table.
 * @tparam T
 */
trait TimekeepingTable[T <: Entity] extends Table[T] {
  def created = column[Timestamp]("created", O.NotNull)
  def updated = column[Timestamp]("updated", O.NotNull)

  /**
   * Helper for generating a current time.
   */
  def currentTimestamp: Timestamp = {
    new Timestamp((new Date()).getTime())
  }
}
