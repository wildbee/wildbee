package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import helpers._
import java.util.Random
import java.util.UUID
import scala.language.postfixOps

case class Transition(id: UUID, workflow: UUID, presentState: UUID, futureState: UUID)
object Transitions extends Table[Transition]("transitions") {
  def id = column[UUID]("id", O.PrimaryKey)
  def workflow = column[UUID]("workflow")
  def presentState = column[UUID]("state")
  def futureState = column[UUID]("next_state")

  def * = id ~ workflow ~ presentState ~ futureState <> (Transition, Transition.unapply _)
  def autoId = id ~ workflow ~ presentState ~ futureState returning id

  def getLogic(workflow: UUID): Map[UUID, List[UUID]] = DB.withSession {
    implicit session: Session =>
      {
        val transistions = Transitions
          .filter(_.workflow === workflow)
          .map(w => (w.presentState, w.futureState))
          .list

        val transistionMapping = transistions
          .groupBy(_._1) //A -> List((A,B),(A,C))
          .map { case (k, v) => (k, v.map(_._2)) } //A -> List(B, C)

        transistionMapping
      }
  }

  /** In this case update is just re-creating the workflow */
  def create(workflow: UUID, stateTableStrings: List[String]): Unit = DB.withSession {
    implicit session: Session =>
      {
        val stateTable =  stateTableStrings map (Workflows.uuid(_))
        val shiftedStateTable = ( stateTable.tail ::: List(stateTable.head) )
        val stateTransistions = stateTable zip shiftedStateTable
        delete(workflow) //Delete previous task's workflow
        stateTransistions map { //Create new task's workflow
          case (state, nextState) => autoId.insert(Config.pkGenerator.newKey, workflow, state, nextState)
        }
      }
  }

  def delete(workflow: UUID): Unit = DB.withSession {
    implicit session: Session =>
      Transitions filter (_.workflow === workflow) delete
  }

  /**
   * Returns a mapping of id => name for all statuses that are allowed
   * in this workflow.
   */
  def allowedStatusesMap(workflow: UUID, currentStatus: UUID): Map[String, String] = DB.withSession {
    implicit session: Session =>
      val nextStateLogic = getLogic(workflow)
      val nextStates = nextStateLogic(currentStatus)
      nextStates.map(state =>(state.toString, Statuses.idToName(state))).toMap
  }
}

/**
 * Currently not in use
 * case class Status(id: UUID, taskId: UUID, task: String, status: String)
 * object Statuses extends Table[Status]("status") {
 * def id     = column[UUID]("id", O.PrimaryKey)
 * def taskId = column[UUID]("task_id")
 * def task   = column[String]("task")
 * def status = column[String]("status")
 *
 * def * = id ~ taskId ~ task ~ status <> (Status, Status.unapply _)
 * def autoId = id ~ taskId ~ task ~ status returning id
 *
 * def currentStatus(workflow: String): String = DB.withSession {
 * // While going through code I don't think using Query is a good way to go
 * // How would I nicely represent the below with Query?
 * // So below is actually a query, ".list" and ".first" executes the query
 * implicit session: Session => {
 * val status = for {s <- Statuses if s.taskId === id} yield s.status
 * status.first
 * }
 * }
 *
 * def create(task: String, state: String): Unit = DB.withSession {
 * implicit session: Session =>
 * autoId.insert(Config.pkGenerator.newKey, Tasks.findByName(task).id, task, state) //This is temporary
 * }
 *
 * def delete(task: String): Unit = DB.withSession {
 * implicit session: Session =>
 * Statuses where (_.id === Tasks.findByName(task).id) delete
 * }
 *
 * def update(workflow: String, state: String = ""): Unit = DB.withSession {
 * implicit session: Session => {
 * val currentState = Statuses filter (_.taskId === taskId)
 * val nextState = state match {
 * case "" => AllowedStatuses.nextState(workflow, currentStatus(workflow))
 * case _ => state
 * }
 * currentState map (_.status) update (nextState)
 * }
 * }
 * }
 */
