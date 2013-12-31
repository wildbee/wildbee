package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.sql.Timestamp
import java.util.Date
import java.util.UUID
import helpers._
import models.traits.Queriable
import models.traits.Observable

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
  osVersion: String) extends NewEntity with Observable

case class NewObserver(name: String)

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
  updated: java.sql.Timestamp) extends Entity with Timekeeping

/**
 * The Packages table will be of type Table[Package] so that
 * we can map our projections to the Package case class.
 */
object Packages extends Table[Package]("packages") with Queriable[Package,NewPackage]
with EntityTable[Package, NewPackage] with TimekeepingTable[Package] with Observable{
  def task = column[UUID]("task_id")
  def creator = column[UUID]("creator_id")
  def assignee = column[UUID]("assignee_id")
  def ccList = column[String]("cc_list", O.Default("None"))
  def status = column[UUID]("status")
  def osVersion = column[String]("os_version")
  def belongsToTask = foreignKey("task_fk", task, Tasks)(_.id)
  def createdBy = foreignKey("creator_fk", creator, Users)(_.id)
  def assignedTo = foreignKey("assignee_fk", assignee, Users)(_.id)

  /**
   * The default projection is mapped to the Package case class.
   */
  def * = (id ~ name ~ task ~ creator ~ assignee ~ ccList ~
    status ~ osVersion ~ created ~ updated <> (Package, Package.unapply _))

  def mappedEntity = (name ~ task.toString ~ creator.toString ~ assignee.toString ~
    ccList ~ status.toString ~ osVersion <> (NewPackage, NewPackage.unapply _))

  /**
   * Packages do not have unique names and instead need to be queried
   * by both their task and their package name.
   * @param task
   * @param pack
   * @return The queried package
   */
  def findByTask(task: String, pack: String): Package = DB.withSession {
    implicit session: Session =>
      val t = Tasks.find(task)
      val p = Packages.find(pack)
      Query(this).where(_.name === p.name).where(_.task === t.id).first
  }

  /**
   * Implements the Queriable trait's mapToEntity method.
   * @param p
   * @param nid
   * @return
   */
  def mapToEntity(p: NewPackage, nid: UUID = newId): Package =
    Package(nid, p.name, uuid(p.task), uuid(p.creator), uuid(p.assignee),
    p.ccList, Tasks.getStartingStatus(uuid(p.task)), p.osVersion,
    currentTimestamp, currentTimestamp)

  /**
   * Implements the Queriable trait's mapToNew method.
   */
  def mapToNew(id: UUID): NewPackage = {
    val p = find(id)
    NewPackage(p.name, p.task.toString, p.creator.toString, p.assignee.toString,
      p.ccList, p.status.toString, p.osVersion)
  }

  override def afterUpdate(id: UUID, item: NewPackage) = {
    println("Overriding after Update on Packages" + countObservers)
    notifyObservers()
  }
}