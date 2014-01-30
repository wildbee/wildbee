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
  def nameToId(name: String): Option[UUID] = DB.withSession {
    implicit session: Session =>
      Query(this).where(_.name === name).firstOption match {
        case Some(entity) => Some(entity.id)
        case None => None
      }
  }

  /**
   * Returns the name of the entity given by this UUID/String id.
   */
  def idToName(id: AnyRef): String = {
    val entity = id match {
      case x: String => findById(uuid(x))
      case x: UUID => findById(x)
    }

    entity match {
      case None => "Unknown"
      case Some(obj) => obj.name
    }
  }

  /**
   * Find entity by its id.
   */
  protected def findById(id: AnyRef): Option[T] = {
    def get(id: UUID): Option[T] = DB.withSession {
      beforeGet(id)
      val item = { implicit session: Session =>
        Query(this).where(_.id === id).firstOption
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
  protected def findByName(name: String): Option[T] ={
    nameToId(name) match {
      case Some(id) => findById(id)
      case None     => None
    }

  }

  /**
   * Helper that finds the UUID for some entity given its
   * name, or string UUID.
   * @param item
   * @return
   */
  def findUUID(item: AnyRef): Option[UUID] = item match {
    case None => None
    case id: UUID => Some(id)
    case str: String => {
      if (isUUID(str)) Some(uuid(str))
      else findByName(str) match {
        case None => None
        case Some(obj) => Some(obj.id)
      }
    }
  }
}