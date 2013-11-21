package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.Random
import java.util.UUID


//TODO: Maybe status should be its own type
/** Currently Unused
trait Status {
        val availableStatuses = List("Open", "In Progress", "Pending", "Closed")
} */

/** Define the workflow for a task */
case class Workflows(stage: List[String])

object Workflows extends Table[(UUID, UUID, String, String)]("workflows") {
  def id = column[UUID]("id", O.PrimaryKey)
  def taskId = column[UUID]("task_id")
  def presentState = column[String]("state")
  def futureState = column[String]("next_state")

  def * = id ~ taskId ~ presentState ~ futureState
  def autoId = id ~ taskId ~ presentState ~ futureState returning id

  def nextState(id: UUID, state: String): String = DB.withSession {
    implicit session: Session => {
      val transistions = Workflows
        .filter(_.taskId === id)
        .map(w => (w.presentState, w.futureState))
        .list
  
      val transistionMapping = transistions
        .groupBy(_._1)                           //A -> List((A,B),(A,C))
        .map { case (k, v) => (k, v.map(_._2)) } //A -> List(B, C)
  
      val choices = transistionMapping(state)
  
      /** Temporary ***************************************
        * When you have logic like A -> (B,C), randomly pick which state to move into next
        * For testing, not sure how conflicts should be resolved
        * You would instead give user ability to choose next state from available states
        */
      println("=========")
      println("Possible Outcomes")
      println("==========")
      choices map (println)
      println("==========")
  
      val rand = new Random(System.currentTimeMillis())
      val idx = rand.nextInt(choices.length)
      println("Randomly Outcome => " + choices(idx))
      choices(idx)
      /** Temporary * ****************************************/
    }
  }

  /** In this case update is just re-creating the workflow */
  def create(task: String, stateTable: List[String]): Unit = DB.withSession {
    implicit session: Session => {
      val shiftedStateTable = stateTable.tail ::: List(stateTable.head) //O(n) can we do better?
      val stateTransistions = stateTable zip shiftedStateTable
      val taskId = Tasks.findById(task).id 
      delete(task)              //Delete previous task's workflow
      stateTransistions map {   //Create new task's workflow
        case (state, nextState) => autoId.insert(Config.pkGenerator.newKey, taskId, state, nextState)
      }
    }
  }

  def delete(task: String): Unit = DB.withSession {
    implicit session: Session =>
      Workflows filter (_.taskId === Tasks.findById(task).id) delete
  }

}

/** Define the packages current state */
//TOOD: Tie this in with a package rather than a task
object PackageStatuses extends Table[(UUID, UUID, String, String)]("allowed_statuses") {
  def id = column[UUID]("id", O.PrimaryKey)

  def taskId = column[UUID]("task_id")

  def task = column[String]("task")

  def status = column[String]("status")

  def * = id ~ taskId ~ task ~ status

  def autoId = id ~ taskId ~ task ~ status returning id

  def currentStatus(id: UUID): String = DB.withSession {
    // While going through code I don't think using Query is a good way to go
    // How would I nicely represent the below with Query?
    // So below is actually a query, ".list" and ".first" executes the query
    implicit session: Session => {
      val status = for {s <- PackageStatuses if s.taskId === id} yield s.status
      status.first
    }
  }

  def create(task: String, state: String): Unit = DB.withSession {
    implicit session: Session =>
      autoId.insert(Config.pkGenerator.newKey, Tasks.findById(task).id, task, state) //This is temporary
  }

  def delete(task: String): Unit = DB.withSession {
    implicit session: Session =>
      PackageStatuses where (_.id === Tasks.findById(task).id) delete
  }

  /** There should be a better way to find your current status */
  def update(task: String, state: String = ""): Unit = DB.withSession {
    implicit session: Session => {
      val taskId = Tasks.findById(task).id
      val currentState = PackageStatuses filter (_.taskId === taskId)
      val nextState = state match {
        case "" => Workflows.nextState(taskId, currentStatus(taskId))
        case _ => state
      }
      currentState map (_.status) update (nextState)
    }
  }
}
