package models

import scala.slick.driver.PostgresDriver.simple._

//TODO: Maybe status should be its own type
/** Currently Unused
trait Status { 
	val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
}*/

/** Define the workflow for a task */
case class Workflows(stage1: String, stage2: String, stage3: String)
object Workflows extends Table [(Long, String, String)]("workflows") {
	def id          = column[Long]("uuid", O.PrimaryKey, O.AutoInc)
	def task        = column[String]("task")
	def logic       = column[String]("logic") //TODO: Do not store as a comma separated string?
	def taskName    = foreignKey("fk_task", task, StatusStates)(_.task)
	
	def *       = id ~ task ~ logic
	def autoInc = task ~ logic returning id
	
	def defineLogic(id: Long, state : String*)(implicit session: Session) = {
	  val currentState = Workflows filter (_.id === id)
	  val logic = state mkString ","
	  currentState map ( _.logic ) update (logic)
	  println("STATE " + state(0))
	  println("logic :: " + logic)
	  //If current state not defined in logic set state to first state in logic
	  if (!logic.contains(StatusStates.currentStatus(id))){
	    StatusStates.update(id, state(0))
	  }
	}
	
	//TODO: Find a better way to express this
  def nextState(id: Long, state: String)(implicit session: Session) = { 
    val logic = (for { w <- Workflows if w.id === id } yield w.logic).list.head.split(",")
    val idx = (logic.indexOf(state) + 1) % logic.size
    logic(idx)
  }
	
	def create(task: String, state: String)(implicit session: Session) = {
    autoInc.insert(task, state)
  }
	
	def delete(id: Long)(implicit session: Session) = {
    Workflows where { _.id === id } delete
  }
	
}

/** Define the packages current state */ //TOOD: Tie this in with a package rather than a task
object StatusStates extends Table [(Long, String, String)]("allowed_statuses") {
	def id = column[Long]("uuid", O.PrimaryKey, O.AutoInc)
	def task = column[String]("task")
	def status = column[String]("status")
	
	def * = id ~ task ~ status
	def autoInc = task ~ status returning id
	
	def currentStatus(id: Long)(implicit session: Session) = {
	  val status = for { s <- StatusStates if s.id === id } yield s.status
	  status.list.head
	}
	
	def create(task: String, state: String)(implicit session: Session) = {
	  autoInc.insert(task, state) //This is temporary
	}
	
	def delete(id: Long)(implicit session: Session) = {
    StatusStates where { _.id === id } delete
  }
	
	/** There should be a better way to find your current status */
	def update(id: Long, state: String = "")(implicit session: Session) = {
	  val currentState = StatusStates filter (_.id === id)
	  val nextState = 
	    if(state == "") Workflows.nextState(id, currentStatus(id)) 
	    else state
	  currentState map ( _.status ) update (nextState)
	}
}