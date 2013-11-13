package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.libs.json._
import java.util.Random

//TODO: Maybe status should be its own type
/** Currently Unused
trait Status { 
	val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
}*/

/** Define the workflow for a task */
case class Workflows(stage: List[String])
object Workflows extends Table [(Long, Long, String, String)]("workflows") {
	def id            = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def taskId        = column[Long]("task_id")
	def presentState  = column[String]("state")
	def futureState   = column[String]("next_state")
	
	def *       = id ~ taskId ~ presentState ~ futureState
	def autoInc = taskId ~ presentState ~ futureState returning id
	
	
  def nextState(id: Long, state: String)(implicit session: Session) = { 
    val transistions = Workflows
      .filter( _.taskId === id )
      .map (w => (w.presentState, w.futureState))
      .list
    
    val transistionMapping = transistions
      .groupBy(_._1)                              //A -> List((A,B),(A,C))
      .map { case (k,v) => (k,v.map(_._2)) }      //A -> List(B, C)

    val choices = transistionMapping(state)
    /** Temporary ***************************************
     *  When you have logic like A -> (B,C), randomly pick which state to move into next
     *  For testing, not sure how conflicts should be resolved
     *  You would instead give user ability to choose next state from available states
     */
    println("=========")
    println("Possible Outcomes")
    println("==========")
    choices map println
    println("==========")
    val rand = new Random(System.currentTimeMillis())
    val idx = rand.nextInt(choices.length)
    println("Randomly Outcome => " + choices(idx))
    choices(idx)
    /** Temporary * ****************************************/
  }
	
	/** In this case update is just re-creating the workflow */
	def create(task: String, stateTable: List[String])(implicit session: Session) = {   
	  val shiftedStateTable = stateTable.tail ::: List(stateTable.head) //O(n) can we do better?
    val stateTransistions = stateTable zip shiftedStateTable
	  val taskId = Tasks
      .filter(_.task === task ) //Find the corresponding task from Task table
      .map (_.id)               //Find get the task id
      .list.head                //Convert Column[Int] into an Int
    
    delete(taskId)              //Delete previous task workflow if any
	  stateTransistions map { case(state, nextState) => autoInc.insert(taskId, state, nextState) }
  }
	
	def delete(taskId: Long)(implicit session: Session) = {
    Workflows filter { _.taskId === taskId } delete
  }
	
}

/** Define the packages current state */ //TOOD: Tie this in with a package rather than a task
object PackageStatuses extends Table [(Long, String, String)]("allowed_statuses") {
	def id     = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def task   = column[String]("task")
	def status = column[String]("status")
	
	def *       = id ~ task ~ status
	def autoInc = task ~ status returning id
	
	def currentStatus(id: Long)(implicit session: Session) = {
	  val status = for { s <- PackageStatuses if s.id === id } yield s.status
	  status.list.head
	}
	
	def create(task: String, state: String)(implicit session: Session) = {
	  autoInc.insert(task, state) //This is temporary
	}
	
	def delete(id: Long)(implicit session: Session) = {
    PackageStatuses where { _.id === id } delete
  }
	
	/** There should be a better way to find your current status */
	def update(id: Long, state: String = "")(implicit session: Session) = {
	  val currentState = PackageStatuses filter (_.id === id)
	  val nextState = state match {
	    case ""   => Workflows.nextState(id, currentStatus(id))
	    case _    => state
	  }
	  currentState map ( _.status ) update (nextState)
	}
}