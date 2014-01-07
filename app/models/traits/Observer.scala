package models.traits

import models.Entity
import java.util.UUID

trait Observer {
  var tracking: List[UUID] = List.empty[UUID]
  def track(id: UUID) = tracking ::= id
  def isTracking(id: UUID) = tracking.contains(id)

  val name: String = this.getClass.getSimpleName
  val fullName: String = this.getClass.getName
	def update(s: Observable): Unit
}