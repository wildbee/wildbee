package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.sql.Timestamp
import java.util.Date
import java.util.UUID
import helpers._

/*
 * This class is for creating new packages from string inputs:
 */
case class NewPackage(
  name: String,
  task: String,
  creator: String,
  assignee: String,
  ccList: String = "None",
  status: String = "None",
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
  status: UUID,
  osVersion: String,
  created: java.sql.Timestamp,
  updated: java.sql.Timestamp)

/**
 * The Packages table will be of type Table[Package] so that
 * we can map our projections to the Package case class.
 */
object Packages extends Table[Package]("packages") with Queriable[Package,NewPackage] {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def task = column[UUID]("task_id")
  def creator = column[UUID]("creator_id")
  def assignee = column[UUID]("assignee_id")
  def ccList = column[String]("cc_list", O.Default("None"))
  def status = column[UUID]("status")
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

  def mappedEntity = (name ~ task.toString ~ creator.toString ~ assignee.toString ~
    ccList ~ status.toString ~ osVersion <> (NewPackage, NewPackage.unapply _))

  def findByTask(task: String, pack: String): Package = DB.withSession {
    implicit session: Session =>
      val t = Tasks.find(task)
      val p = Packages.find(pack)
      Query(this).where(_.name === p.name).where(_.task === t.id).first
  }
  /**
   * Call this if you want to explicitly set your own id.
   */
  def insertWithId(id: UUID, p: NewPackage): UUID =
    Packages.insert(Package(
      id,
      p.name,
      uuid(p.task),
      uuid(p.creator),
      uuid(p.assignee),
      p.ccList,
      Tasks.getStartingStatus(uuid(p.task)),
      p.osVersion,
      currentTimestamp,
      currentTimestamp))

  /**
   * Call this to auto-create id.
   */
  def insert(p: NewPackage): UUID = {
    insertWithId(newId, p)
  }

  /**
   * This method returns a NewPackage type created from a queried
   * Package. Useful during updates to fill a form with the current
   * values.
   */
  def mapToNew(id: UUID): NewPackage = {
    val p = find(id)
    NewPackage(
      p.name,
      p.task.toString,
      p.creator.toString,
      p.assignee.toString,
      p.ccList,
      p.status.toString,
      p.osVersion)
  }

  /**
   * This method maps from NewPackage to Package in order to
   * create an update.
   */
  def updatePackage(id: UUID, p: NewPackage, o: Package) =
    update(id, Package(
      id,
      p.name,
      uuid(p.task),
      uuid(p.creator),
      uuid(p.assignee),
      p.ccList,
      uuid(p.status),
      p.osVersion,
      o.created,
      currentTimestamp))
}