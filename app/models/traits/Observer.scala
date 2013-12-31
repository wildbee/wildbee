package models.traits

import models.Entity

trait Observer {
  val name: String = this.getClass.getSimpleName
	def update(s: Observable): Unit
}