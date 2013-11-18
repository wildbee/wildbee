package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.sql.Timestamp
import java.util.Date
import java.util.UUID
import helpers._

/**
 * Traits that can be commonly used by all
 * database table objects.
 * Could go in a different file, and we could
 * add generalized queries to this once we figure
 * out how.
 */
trait Queriable {

  def currentTimestamp: Timestamp = {
    new Timestamp((new Date()).getTime())
  }

}

/**
 * This class is for creating new packages:
 * It only has some of the columns, and all
 * as strings.
 */
case class NewPackage(
  name: String,
  task: String,
  creator: String,
  assignee: String,
  ccList: String = "None",
  status: String,
  osVersion: String)

/**
 * This is the main case class that we will map projections to.
 */
case class Package(
  id: UUID,
  name: String,
  task: UUID,
  creator: UUID,
  assignee: UUID,
  ccList: String = "None",
  status: String,
  osVersion: String,
  created: java.sql.Timestamp,
  updated: java.sql.Timestamp)

/**
 * The Packages table will be of type Table[Package] so that
 * we can map our projections to the Package case class.
 */
object Packages extends Table[Package]("packages") with Queriable {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def task = column[UUID]("task_id")
  def creator = column[UUID]("creator_id")
  def assignee = column[UUID]("assignee_id")
  def ccList = column[String]("cc_list", O.Default("None"))
  def status = column[String]("status")
  def osVersion = column[String]("os_version")
  def creationTime = column[Timestamp]("creation_time", O.NotNull)
  def lastUpdated = column[Timestamp]("last_updated", O.NotNull)
  def belongsToTask = foreignKey("task_fk", task, Tasks)(_.id)
  def createdBy = foreignKey("creator_fk", creator, Users)(_.id)
  def assignedTo = foreignKey("assignee_fk", assignee, Users)(_.id)

  /**
   * The default projection is mapped to the Package case class.
   */
  def * = (id ~ name ~ task ~ creator ~ assignee ~ ccList ~
    status ~ osVersion ~ creationTime ~ lastUpdated <> (Package, Package.unapply _))

  def autoId = id ~ name ~ task ~ creator ~ assignee ~ ccList ~
    status ~ osVersion ~ creationTime ~ lastUpdated returning id

  def findAll: List[Package] = DB.withSession {
    implicit session: Session =>
      val all = Query(this).list
      return all
  }

  def findById(id: String): Package = DB.withSession {
    implicit session: Session =>
      val uuid = Config.pkGenerator.fromString(id)
      val row = Query(this).where(_.id === uuid).first
      return row
  }

  def insert(p: NewPackage) = DB.withSession {
    implicit session: Session =>
    autoId.insert(
    Config.pkGenerator.newKey,
    p.name,
    Config.pkGenerator.fromString(p.task),
    Config.pkGenerator.fromString(p.creator),
    Config.pkGenerator.fromString(p.assignee),
    p.ccList,
    p.status,
    p.osVersion,
    currentTimestamp,
    currentTimestamp)
  }

  /**
   * These two following helpers should be generalized and made
   * traits since they will be used by many models.
   */
  def getUserMap: Map[String, String] = DB.withSession {
    implicit session: Session =>
      Query(Users).list.map(u => (u._1.toString, u._2)).toMap
  }

  def getTaskMap: Map[String, String] = DB.withSession {
    implicit session: Session =>
      Query(Tasks).list.map(t => (t._1.toString, t._2)).toMap
  }

}