package models.traits

import models.Entity

trait Observer {
  val name: String = this.getClass.getSimpleName
  val fullName: String = this.getClass.getName
	def update(s: Observable): Unit
}