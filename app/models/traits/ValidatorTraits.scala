package models.traits

import models.Entity;
import models.NewEntity
import java.util.UUID
;


/**
 * This trait defines default validators called before running a CRUD operation
 * for entity models. Their purpose is to prevent invalid operations.
 * By default, they will pass. If models require some specific validation
 * logic, they can override whichever specific validator needs to be
 * customized.
 *
 * Supported operations (for now):
 * UPDATE - update
 * DELETE - delete and deleteAll
 * @tparam T
 * @tparam Y
 */
trait Validators[T <: Entity, Y <: NewEntity] {

  def insertValidator(item: Y): Option[String] = None

  def updateValidator(id: UUID, item: AnyRef): Option[String] = None

  def deleteValidator(item: AnyRef): Option[String] = None

  def deleteAllValidator: Option[String] = None
}