package models

import scala.slick.driver.PostgresDriver.simple._

object Statuses extends Table[(Long, String)]("statuses") {
	val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
	
	def id = column[Long]("uuid", O.PrimaryKey, O.AutoInc)
	def status = column[String]("status", O.NotNull)
	
	def * = id ~ status
	def autoInc = status returning id
	
  def create()(implicit session: Session) = {
    availableStatuses map { autoInc.insert(_) }
  } 
}


object Workflows extends Table [(Long, String)]("workflows") {
	def id = column[Long]("uuid", O.PrimaryKey, O.AutoInc)
	def task = column[String]("task")
	
	def * = id ~ task
	def autoInc = task returning id
}


object AllowedStatuses extends Table [(Long, String)]("allowed_statuses") {
	def id = column[Long]("uuid", O.PrimaryKey, O.AutoInc)
	def task = column[String]("task")
	
	def * = id ~ task
	def autoInc = task returning id
}