package models.traits

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.util.UUID
import scala.language.reflectiveCalls
import models.{EntityTable, NewEntity, Entity}

/**
 * The Entity trait can be shared by entities in our
 * models since it will define all of the basic CRUD operations
 * in a generalized way.
 *
 * T is the case class used to map from a row in the DB table to a scala object.
 * Y is the case class used to map from all string user input to a scala object.
 */
trait CRUDOperations[T <: Entity, Y <: NewEntity]
  extends NameIdMethods[T,Y]
  with CRUDValidators[T,Y]{

  /**
   * This trait is used by entity models with Tables of type T with EntityTable trait of type T.
   */
  self: Table[T] with EntityTable[T, Y] =>

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
   * @param identifier
   * @return
   */
  def find(identifier: AnyRef): Option[T] = identifier match {
    case id: UUID => findById(identifier)
    case str: String => {
      if (isUUID(str)) findById(identifier)
      else findByName(str)
    }
  }

  /**
   * Inserts this entity instance into the database table.
   * @param instance An Entity instance.
   * @return The UUID of the instance.
   */
  def insert(instance: T): UUID = {
    beforeInsert(instance)
    DB.withSession { implicit session: Session =>
      returnID.insert(instance)
    }
    afterInsert(instance)
    instance.id
  }

  /**
   * Insert a new entity into the db table created from the values
   * in the NewEntity type instance. If an UUID is not specified, an id
   * will be generated inside the mapToEntity instance.
   * @param newInstance A NewEntity containing values to insert.
   * @param id an explicit UUID to use.
   * @return the UUID of the new instance.
   */
  def insert(id: UUID = newId, newInstance: Y): Either[String,UUID] = {
    play.api.Logger.debug("id: " + id.toString)
    insertValidator(newInstance) match {
      case Some(error) =>  Left(error)
      case None => {
        beforeInsert(id, newInstance)
        insert(mapToEntity(id,newInstance))
        afterInsert(id, newInstance)
        Right(id)
      }
    }
  }

  /**
   * Same as insert above. Need to map your inputs to the correct
   * class of type T, then pass that into this method.
   */
  def update(instance: T): T = {
    beforeUpdate(instance)
    DB.withSession {
    implicit session: Session =>
      tableQueryToUpdateInvoker(
        tableToQuery(this).where(_.id === instance.id)).update(instance)
    }
    afterUpdate(instance)
    instance
  }

  /**
   * Update takes a NewEntity instance and its id, then
   * maps it to an Entity and updates the db.
   * @param id
   * @param newInstance
   */
  def update(id: UUID, newInstance: Y): Option[String] = {
    def upd(id: UUID, item: Y) = {
      beforeUpdate(id, item)
      update(mapToEntity(id,item))
      afterUpdate(id, item)
    }

    updateValidator(id, newInstance) match {
      case Some(error) =>  Some(error)
      case None => upd(id, newInstance); None
    }
  }

  /**
   * Delete an entity from its table.
   */
  def delete(identifier: AnyRef): Option[String] = {
    def del(id: UUID): Option[String] = DB.withSession { implicit session: Session =>
        beforeDelete(id)
        queryToDeleteInvoker(tableToQuery(this).where(_.id === id)).delete
        afterDelete(id)
        None
    }

    deleteValidator(identifier) match {
      case Some(error) => Some(error)
      case None => identifier match {
        case  Some(id:UUID) => del(id)
        case  Some(str :String) => {
          if (isUUID(str)) del(uuid(str))
          else findByName(str) match {
            case Some(obj) => del(obj.id)
            case None => None
          }
        }
        case id : UUID =>  del(id)
        case str : String => {
          if (isUUID(str)) del(uuid(str))
          else findByName(str) match {
            case Some(obj) => del(obj.id)
            case None => None
          }
        }
      }
    }
  }

  /**
   * Helper method to delete all the rows in a table
   */
  def deleteAll: Option[String] = {
    def del = DB.withSession { implicit session: Session =>
      beforeDeleteAll
      tableToQuery(this).delete
      afterDeleteAll
    }

    deleteAllValidator match {
      case Some(error) => Some(error)
      case None => del; None
    }
  }
}
