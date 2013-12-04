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
    implicit session: Session => {
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

  /** Get all possible statuses for a workflow mapping for statuses UUID to statuses name */
  def transitionMap(workflow: UUID): Map[String, String] ={
    val logic = getLogic(workflow)
    val statuses = logic.keys map( id => (id.toString, Statuses.idToName(id)) )
    statuses.toMap
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

  /** Return mapping of status.uuid.toString -> status.uuid
   * Returns a mapping of id => name for all statuses that are allowed
   * in this workflow.
   */
  def allowedStatuses(task: AnyRef, pack: AnyRef): Map[String, String] = DB.withSession {
    implicit session: Session =>
      val workflow = Tasks.find(task).workflow
      val currentStatus = Packages.find(pack).status
      val nextStateLogic = getLogic(workflow)
      val nextStates = nextStateLogic(currentStatus)
      nextStates.map(state =>(state.toString, Statuses.idToName(state))).toMap
  }
}
