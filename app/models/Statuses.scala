package models

import play.api.db.slick.Config.driver.simple._
import java.util.UUID
import scala.language.postfixOps
import models.traits.CRUDOperations
import helpers.ObserverHelper


case class NewStatus(name: String) extends NewEntity
case class Status(id: UUID, name: String) extends Entity
object Statuses extends Table[Status]("statuses")
  with EntityTable[Status, NewStatus]
  with UniquelyNamedTable[Status, NewStatus] {

  def * = id ~ name  <> (Status, Status.unapply _)

  /**
   * Implements Queriable trait's mapToNew.
   * @param id
   * @return
   */
  def mapToNew(id: UUID): NewStatus = {
    find(id) match {
      case Some(s) => NewStatus(s.name)
    }
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
