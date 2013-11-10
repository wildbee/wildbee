package models

import scala.slick.driver.PostgresDriver.simple._

//Maybe status should be its own type
trait Status {
	val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
	def nextState (status: String) = { //This is a bad implementation CHANGE!
		val idx = (availableStatuses.indexOf(status) + 1) % availableStatuses.size //....
		availableStatuses(idx) //The horror......
	}
}

case class Workflows(stage1: String, stage2: String, stage3: String)
object Workflows extends Table [(Long, String)]("workflows") with Status {
	def id = column[Long]("uuid", O.PrimaryKey, O.AutoInc)
	def task = column[String]("task") //Should be using uuid
	def taskName    = foreignKey("fk_task", task, StatusStates)(_.task)
	
	def * = id ~ task
	def autoInc = task returning id
	
  def nextState() { println("Next State") }
	
	 def create(task: String)(implicit session: Session) = {
    autoInc.insert(task)
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
	def update(taskId: Long)(implicit session: Session) = {
	  val currentState = StatusStates filter (_.id === taskId)
	  val self = for { s <- StatusStates if s.id === taskId } yield s.status
	  Workflows.nextState
	  println("Task ID :: " + taskId)
	  println("Current Status :: " + self.list.head)
	  currentState map ( _.status ) update(nextState(self.list.head))
	}
}