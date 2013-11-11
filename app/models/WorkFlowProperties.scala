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
object Workflows extends Table [(Long, String, String)]("workflows") {
	def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def task        = column[String]("task")
	def logic       = column[String]("logic") //TODO: Do not store as a comma separated string?
	def taskName    = foreignKey("fk_task", task, StatusStates)(_.task)
	
	def *       = id ~ task ~ logic
	def autoInc = task ~ logic returning id
	
	def defineLogic(id: Long, state : List[String])(implicit session: Session) = {
	  val currentState = Workflows filter (_.id === id)
	  //Implementation can probably be improved
	  val statePairs = for ( i <- 0 until state.size) yield(state(i), state((i + 1)% state.size)) 
	  val jsonLogic = Json.toJson(statePairs.groupBy(_._1).map { case (k,v) => (k,v.map(_._2))})
	  val logic = state mkString ","
	  currentState map ( _.logic ) update (Json.stringify(jsonLogic))
	}
	
	//TODO: Find a better way to express this
  def nextState(id: Long, state: String)(implicit session: Session) = { 
    val jsonLogic = (for { w <- Workflows if w.id === id } yield w.logic).list.head
    val logic = Json.fromJson[Map[String, List[String]]](Json.parse(jsonLogic)).get
    val choices = logic(state)
    /** Temporary ***************************************
     *  When you have logic like A -> (B,C), randomly pick which state to move into next
     *  For testing, not sure how conflicts should be resolved
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
	
	def create(task: String, state: String)(implicit session: Session) = {
    autoInc.insert(task, state)
  }
	
	def delete(id: Long)(implicit session: Session) = {
    Workflows where { _.id === id } delete
  }
	
}

/** Define the packages current state */ //TOOD: Tie this in with a package rather than a task
object StatusStates extends Table [(Long, String, String)]("allowed_statuses") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
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
	  val nextState = state match {
	    case ""   => Workflows.nextState(id, currentStatus(id))
	    case _    => state
	  }
	  currentState map ( _.status ) update (nextState)
	}
}