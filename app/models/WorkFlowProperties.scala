package models

import scala.slick.driver.PostgresDriver.simple._

trait Status {
	val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
	def nextState (status: String) = { //This is a bad implementation CHANGE!
		val idx = (availableStatuses.indexOf(status) + 1) % availableStatuses.size //UGLY
		availableStatuses(idx) //The horror......
	}
}

/** Use a trait instead
object Statuses extends Table[(Long, String)]("statuses") {
  val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
	
	def id = column[Long]("uuid", O.PrimaryKey, O.AutoInc)
	def status = column[String]("status", O.NotNull)
	
	def * = id ~ status
	def autoInc = status returning id
	
  def create()(implicit session: Session) = {
    availableStatuses map { autoInc.insert(_) }
  } 
  def generateWorkFlow()(implicit session: Session) = {
    Workflows.test()
  }
}
*/

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
	  autoInc.insert(task, availableStatuses.head) //TODO: Not Good
	}
	
	/** MORE UGLY SCALA CODE FOR FINDING YOUR OWN STATUS MUST BE A BETTER WAY 
	 *  CHANGE LATER !!!*/
	def update(task: String)(implicit session: Session) = {
	  val currentState = StatusStates filter (_.task === task)
	  val self = for { s <- StatusStates if s.task === task } yield s.status
	  currentState map ( _.status ) update( nextState(self.list.head) )
	}
}