package models.traits

import models.Entity
import java.util.UUID

/** Observer Description
 *  """Tracking"""
 *  Observers may track entries of a model by knowing its UUID.
 *  You may add UUIDs to track one at a time, or by passing in a list of UUIDs.
 *
 *  Name: Refers to the name of the plugin file
 *  Path: Refers to the path to the observer
 *  Update: Is invoked to notify the observer of a change
 */
trait Observer {
  var tracking: List[UUID] = List.empty[UUID]
  def track(id: UUID): Unit = tracking ::= id
  def track(ids: List[UUID]): Unit = ids foreach( track(_) )
  def untrack(id: UUID): Unit = tracking = tracking filterNot (_ == id)
  def isTracking(id: UUID) = tracking.contains(id)

  val name: String = this.getClass.getSimpleName
  val path: String = this.getClass.getName
	def update(s: Observable, id: UUID, command: ObserverCommand): Unit
}