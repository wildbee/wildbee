package models.traits

import models.Entity

trait Observer {
  val name: String
	def update(s: Observable): Unit
}