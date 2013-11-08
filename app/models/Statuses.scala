package models

import scala.slick.driver.PostgresDriver.simple._

object Statuses extends Table[(Long, String)]("STATUSES") {
	val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
	
	def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
	def status = column[String]("STATUS", O.NotNull)
	
	def * = id ~ status
	def autoInc = status returning id
	
  def create()(implicit session: Session) = {
    availableStatuses map { autoInc.insert(_) }
  } 
}