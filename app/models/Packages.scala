package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.util.UUID
import models.traits._
import observers.commands.ObserverCommands
import ObserverCommands._
import scala.Some



/** This class is for creating new packages from string inputs: */
case class NewPackage(
  name: String,
  task: String,
  creator: String,
  assignee: String,
  ccList: String = "None",
  status: String = "None",
  osVersion: String) extends NewEntity

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

object Packages extends Table[Package]("packages")
  with EntityTable[Package, NewPackage]
  with TimekeepingTable[Package]
  with Observable {

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
      (Tasks.find(task), Packages.find(pack)) match
      {
        case (Some(t), Some(p)) => Query(this).where(_.name === p.name).where(_.task === t.id).first
        case _ => throw new IllegalArgumentException
      }
  }

  /**
   * Implements the Queriable trait's mapToEntity method.
   * @param p
   * @param id
   * @return
   */
  def mapToEntity(id: UUID = newId, p: NewPackage): Package =
    Package(id, p.name, uuid(p.task), uuid(p.creator), uuid(p.assignee),
    p.ccList, Tasks.getStartingStatus(uuid(p.task)), p.osVersion,
    currentTimestamp, currentTimestamp)

  /**
   * Implements the Queriable trait's mapToNew method.
   */
  def mapToNew(id: UUID): NewPackage = {
    find(id) match {
      case Some(p) =>
        NewPackage(p.name, p.task.toString, p.creator.toString, p.assignee.toString,
          p.ccList, p.status.toString, p.osVersion)
      case None => throw new IllegalArgumentException
    }

  }

  override def afterUpdate(item: Package) = {
    notifyObservers(item.id, Edit)
  }

  //TODO: Extend to acommodate multiple plugins
  override def afterDelete(id: UUID){
    notifyObservers(id, Delete)
    val plugins = Plugins.findAll

    plugins foreach { plugin => plugin.pack match {
      case Some(packId) => if(packId == id){
        val updatedPlugin = NewPlugin(plugin.name, None)
        Plugins.update(Plugins.mapToEntity(plugin.id, updatedPlugin))
      }
      case None =>
    }}
  }
}