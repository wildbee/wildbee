package models

import play.api.db.slick.Config.driver.simple._
import java.util.UUID
import scala.language.postfixOps

import models.traits.CRUDOperations


case class NewStatus(name: String) extends NewEntity
case class Status(id: UUID, name: String) extends Entity
object Statuses extends Table[Status]("statuses")
  with CRUDOperations[Status, NewStatus]
  with EntityTable[Status, NewStatus]
  with UniquelyNamedTable[Status, NewStatus]
  with MapsIdsToNames[Status] {

  def * = id ~ name  <> (Status, Status.unapply _)

  /**
   * Implements Queriable trait's mapToNew.
   * @param id
   * @return
   */
  def mapToNew(id: UUID): NewStatus = {
    val s = find(id)
    NewStatus(s.name)
  }

  /**
   * Implements Queriable trait's mapToEntity.
   * @param p
   * @param id
   * @return
   */
  def mapToEntity(id: UUID = newId, p: NewStatus): Status = {
    Status(id,p.name)
  }
}
