package models.traits

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.util.UUID
import scala.language.reflectiveCalls
import models.{EntityTable, NewEntity, Entity}

trait NameIdMethods[T <: Entity, Y <: NewEntity]
  extends CRUDLifecycles[T, Y] {

  /**
   * This trait is used by entity models with Tables of type T with EntityTable trait of type T.
   */
  self: Table[T] with EntityTable[T, Y] =>

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
   * Find entity by its id.
   */
  protected def findById(id: AnyRef): T = {
    def get(id: UUID): T = DB.withSession {
      beforeGet(id)
      val item = { implicit session: Session =>
        Query(this).where(_.id === id).first
      }
      afterGet(id)
      item // return value
    }
    id match {
      case id: String => get(uuid(id))
      case id: UUID => get(id)
    }
  }

  /**
   * Find an entity by name rather then by UUID.
   */
  protected def findByName(name: String): T = findById(nameToId(name))

  /**
   * Helper that finds the UUID for some entity given its
   * name, or string UUID.
   * @param item
   * @return
   */
  def findUUID(item: AnyRef): UUID = item match {
    case item: String => {
      if (vidP(item)) {
        uuid(item)
      } else {
        findByName(item).id
      }
    }
    case item: UUID => item
  }
}