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
 * This base abstract class represents any entity model in the db.
 */
abstract class Entity{
  def id: UUID
  def name: String
}

/**
 * This base abstract class represents the string/input form for an entity model in the db.
 */
abstract class NewEntity{
  def name: String
}

/**
 * For any entities that we wish to record times for.
 */
trait Timekeeping {
  def created: java.sql.Timestamp
  def updated: java.sql.Timestamp
}


/**
 * Table object traits
 */

/**
 * Main trait for any entity model table.
 * @tparam T
 */
trait EntityTable[T <: Entity] extends Table[T] {
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
