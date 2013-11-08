package models

import scala.slick.driver.PostgresDriver.simple._

trait Status {
	val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
	def nextState (status: String) = { //This is a bad implementation CHANGE!
		val idx = (availableStatuses.indexOf(status) + 1) % availableStatuses.size //....
		availableStatuses(idx) //The horror......
	}
}

object Workflows extends Table [(Long, String)]("workflows") with Status {
	def id = column[Long]("uuid", O.PrimaryKey, O.AutoInc)
	def task = column[String]("task")
	def ownerName    = foreignKey("fk_owner", task, StatusStates)(_.task)
	
	def * = id ~ task
	def autoInc = task returning id
	
  def generateWorkflow(task: String) {
		
	}
}

object StatusStates extends Table [(Long, String, String)]("allowed_statuses") with Status {
	def id = column[Long]("uuid", O.PrimaryKey, O.AutoInc)
	def task = column[String]("task")
	def status = column[String]("status")
	
	def * = id ~ task ~ status
	def autoInc = task ~ status returning id
	
	def create(task: String)(implicit session: Session) = {
	  autoInc.insert(task, availableStatuses.head) //TODO: Change this is not good
	}
	
	/** There must be a better way to find your current status */
	/** This is currently bugged, updated a task seems to update all tasks statuses */
	def update(task: String)(implicit session: Session) = {
	  val currentState = StatusStates filter (_.task === task)
	  val self = for { s <- StatusStates if s.task === task } yield s.status
	  currentState map ( _.status ) update(nextState(self.list.head))
	}
}