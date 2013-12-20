package models.traits

import models.Entity

trait Observer {
	def update(s: Observable): Unit
}