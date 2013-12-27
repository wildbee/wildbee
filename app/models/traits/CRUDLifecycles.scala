package models.traits

import models.{NewEntity, Entity}
import java.util.UUID

/**
 * This trait defines default lifecycle methods that will be called by the Queriables
 * CRUD operations before and after each CRUD method. By default they do nothing, but
 * can be overriden by models that need to perform extra logic before or after a
 * CRUD op.
 *
 * Supported CRUD methods are currently limited to:
 * CREATE - insert
 * READ - find
 * UPDATE - update
 * DELETE - delete and deleteAll
 * @tparam T
 * @tparam Y
 */
trait CRUDLifecycles[T <: Entity, Y <: NewEntity] {

  /**
   * Called right before an insert into DB.
   */
  def beforeInsert(instance: T) = {
    play.api.Logger.debug("beforeInsert Lifecycle Op on " + instance.name)
  }

  /**
   * Called right before an insert into DB.
   */
  def beforeInsert(id: UUID, newInstance: Y) = {
    play.api.Logger.debug("beforeInsert Lifecycle Op on " + newInstance.name)
  }

  /**
   * Called right after an insert into DB.
   */
  def afterInsert(instance: T) = {
    play.api.Logger.debug("afterInsert Lifecycle Op on " + instance.name)
  }

  /**
   * Called right after an insert into DB.
   */
  def afterInsert(id: UUID, newInstance: Y) = {
    play.api.Logger.debug("afterInsert Lifecycle Op on " + newInstance.name)
  }

  /**
   * Called right before a find from DB.
   */
  def beforeGet(id: UUID) = {
    play.api.Logger.debug("beforeFind Lifecycle Op on " + id.toString)
  }

  /**
   * Called right after a find from DB.
   */
  def afterGet(id: UUID) = {
    play.api.Logger.debug("afterFind Lifecycle Op on " + id.toString)
  }

  /**
   * Called right before an update into DB.
   */
  def beforeUpdate(id: UUID, newInstance: Y) = {
    play.api.Logger.debug("beforeUpdate Lifecycle Op on " + newInstance.name)
  }

  /**
   * Called right before an update into DB.
   */
  def beforeUpdate(instance: T) = {
    play.api.Logger.debug("beforeUpdate Lifecycle Op on " + instance.name)
  }

  /**
   * Called right after an update into DB.
   */
  def afterUpdate(instance: T) = {
    play.api.Logger.debug("afterUpdate Lifecycle Op on " + instance.name)
  }

  /**
   * Called right after an update into DB.
   */
  def afterUpdate(id: UUID, newInstance: Y) = {
    play.api.Logger.debug("afterUpdate Lifecycle Op on " + newInstance.name)
  }

  /**
   * Called right before a delete from DB.
   */
  def beforeDelete(id: UUID) = {
    play.api.Logger.debug("beforeDelete Lifecycle Op on " + id.toString)
  }

  /**
   * Called right after an delete from DB.
   */
  def afterDelete(id: UUID) = {
    play.api.Logger.debug("afterDelete Lifecycle Op on " + id.toString)
  }

  /**
   * Called right before a deleteAll.
   */
  def beforeDeleteAll = {
    play.api.Logger.debug("beforeDeleteAll Lifecycle Op" )
  }

  /**
   * Called right after a deleteAll.
   */
  def afterDeleteAll = {
    play.api.Logger.debug("afterDeleteAll Lifecycle Op")
  }

}