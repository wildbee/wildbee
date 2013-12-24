package models.traits

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.sql.Timestamp
import java.util.Date
import java.util.UUID
import helpers._
import scala.language.reflectiveCalls
import scala.reflect.runtime.universe.{TypeTag, typeOf}
import models.{EntityTable, NewEntity, Entity}

/**
 * The Entity trait can be shared by entities in our
 * models since it will define all of the basic CRUD operations
 * in a generalized way.
 *
 * T is the case class used to map from a row in the DB table to a scala object.
 * Y is the case class used to map from all string user input to a scala object.
 */
trait Queriable[T <: Entity, Y <: NewEntity]
  extends Lifecycles[T, Y]
  with Validators[T,Y]{

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
   * Returns a list of all entities for this table.
   */
  def findAll: List[T] = DB.withSession {
    implicit session: Session =>
      Query(this).list
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
   * Inserts this entity item into the database table.
   * @param item An Entity item.
   * @return The UUID of the item.
   */
  def insert(item: T): UUID = {
      beforeInsert(item)
      DB.withSession {
        implicit session: Session =>
          returnID.insert(item)
      }
      item.id
    }

  /**
   * Insert a new entity into the db table created from the values
   * in the NewEntity type item. If an UUID is not specified, an id
   * will be generated inside the mapToEntity item.
   * @param item A NewEntity containing values to insert.
   * @param nid an explicit UUID to use.
   * @return the UUID of the new item.
   */
  def insert(item: Y, nid: UUID = newId): Either[String,UUID] = {
    play.api.Logger.debug("nid: " + nid.toString)
    insertValidator(item) match {
      case Some(error) =>
        Left(error)
      case None => {
        insert(mapToEntity(item, nid))
        // TODO: refactor workflow creation so we can avoid
        // this odd hack with the NewWorkflow.status info
        afterInsert(nid, item)
        Right(nid)
      }
    }
  }

  /**
   * Same as insert above. Need to map your inputs to the correct
   * class of type T, then pass that into this method.
   */
  def update(item: T): Unit = {
    DB.withSession {
    implicit session: Session =>
      tableQueryToUpdateInvoker(
        tableToQuery(this).where(_.id === item.id)).update(item)
    }
  }

  /**
   * Update takes a NewEntity instance and its id, then
   * maps it to an Entity and updates the db.
   * @param id
   * @param item
   */
  def update(id: UUID, item: Y): Option[String] = {
    def upd(id: UUID, item: Y) = {
      beforeUpdate(id, item)
      update(mapToEntity(item, id))
      afterUpdate(id, item)
    }

    updateValidator(id, item) match {
      case Some(error) =>
         Some(error)
      case None => {
        upd(id, item)
        None
      }
    }
  }

  /**
   * Delete an entity from its table.
   */
  def delete(eid: AnyRef): Option[String] = {
    def del(id: UUID): Option[String] = DB.withSession {
        beforeDelete(id)
        implicit session: Session =>
        queryToDeleteInvoker(
          tableToQuery(this).where(_.id === id)).delete
        afterDelete(id)
        None
    }

    deleteValidator(eid) match {
      case Some(error) =>
        Some(error)
      case None =>
        eid match {
          case eid : UUID => {
            del(eid)
          }
          case eid : String => {
            if (vidP(eid)) {
              del(uuid(eid))
            } else {
              del(findByName(eid).id)
            }
          }
        }
    }
  }

  /**
   * Helper method to delete all the rows in a table
   */
  def deleteAll: Option[String] = {
    def del = DB.withSession {
      beforeDeleteAll
      implicit session: Session =>
          tableToQuery(this).delete
      afterDeleteAll
    }

    deleteAllValidator match {
      case Some(error) =>
        Some(error)
      case None => {
        del
        None
      }
    }
  }

  /**
   * Find entity by its id.
   */
  private def findById(id: AnyRef): T = {
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
  private def findByName(name: String): T = findById(nameToId(name))

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
        find(item).id
      }
    }
    case item: UUID => item
  }
}
