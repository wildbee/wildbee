package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.Random
import java.util.UUID

case class AllowedStatus(id: UUID, workflow: String, presentState: String, futureState: String)
object AllowedStatuses extends Table[AllowedStatus]("allowed_statuses") {
  def id           = column[UUID]  ("id", O.PrimaryKey)
  def workflow     = column[String]("workflow")
  def presentState = column[String]("state")
  def futureState  = column[String]("next_state")
  
  def * = id ~ workflow ~ presentState ~ futureState <> (AllowedStatus, AllowedStatus.unapply _)
  def autoId = id ~ workflow ~ presentState ~ futureState returning id

  def getLogic(workflow: String): Map[String, List[String]] = DB.withSession {
    implicit session: Session => {
      val transistions = AllowedStatuses
        .filter(_.workflow === workflow)
        .map(w => (w.presentState, w.futureState))
        .list
  
      val transistionMapping = transistions
        .groupBy(_._1)                           //A -> List((A,B),(A,C))
        .map { case (k, v) => (k, v.map(_._2)) } //A -> List(B, C)
  
      transistionMapping
      
      /** Temporary ***************************************
        * When you have logic like A -> (B,C), randomly pick which state to move into next
        * For testing, not sure how conflicts should be resolved
        * You would instead give user ability to choose next state from available states
      val choices = transistionMapping(state)
      println("=========")
      println("Possible Outcomes")
      println("==========")
      choices map (println)
      println("==========")
  
      val rand = new Random(System.currentTimeMillis())
      val idx = rand.nextInt(choices.length)
      println("Randomly Outcome => " + choices(idx))
      choices(idx)
      * Temporary * ****************************************/
       
    }
  }

  /** In this case update is just re-creating the workflow */
  def create(workflow: String, stateTable: List[String]): Unit = DB.withSession {
    implicit session: Session => {
      println("Creating logic for :: " + workflow)
      val shiftedStateTable = stateTable.tail ::: List(stateTable.head)
      val stateTransistions = stateTable zip shiftedStateTable
      delete(workflow)              //Delete previous task's workflow
      stateTransistions map {       //Create new task's workflow
        case (state, nextState) => autoId.insert(Config.pkGenerator.newKey, workflow, state, nextState)
      }
    }
  }

  def delete(workflow: String): Unit = DB.withSession {
    implicit session: Session =>
      AllowedStatuses filter (_.workflow === workflow) delete
  }

}

/** Define the packages current state */
case class Status(id: UUID, taskId: UUID, task: String, status: String)
object Statuses extends Table[Status]("status") {
  def id     = column[UUID]("id", O.PrimaryKey)
  def taskId = column[UUID]("task_id")
  def task   = column[String]("task")
  def status = column[String]("status")
  
  def * = id ~ taskId ~ task ~ status <> (Status, Status.unapply _)
  def autoId = id ~ taskId ~ task ~ status returning id

  def currentStatus(workflow: String): String = DB.withSession {
    // While going through code I don't think using Query is a good way to go
    // How would I nicely represent the below with Query?
    // So below is actually a query, ".list" and ".first" executes the query
    implicit session: Session => {
      val status = for {s <- Statuses if s.taskId === id} yield s.status
      status.first
    }
  }

  def create(task: String, state: String): Unit = DB.withSession {
    implicit session: Session =>
      autoId.insert(Config.pkGenerator.newKey, Tasks.findByName(task).id, task, state) //This is temporary
  }

  def delete(task: String): Unit = DB.withSession {
    implicit session: Session =>
      Statuses where (_.id === Tasks.findByName(task).id) delete
  }

  /** There should be a better way to find your current status 
  def update(workflow: String, state: String = ""): Unit = DB.withSession {
    implicit session: Session => {
      val currentState = Statuses filter (_.taskId === taskId)
      val nextState = state match {
        case "" => AllowedStatuses.nextState(workflow, currentStatus(workflow))
        case _ => state
      }
      currentState map (_.status) update (nextState)
    }
  }*/
}
