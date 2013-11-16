package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.sql.Timestamp
import java.util.Date
import java.util.UUID
import helpers._

trait queriable {

  def currentTimestamp: Timestamp = {
    new Timestamp((new Date()).getTime())
  }
}

case class NewPackage(
  name: String,
  task: String,
  creator: String,
  assignee: String,
  ccList: String = "None",
  status: String,
  osVersion: String)

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

object Packages extends Table[Package]("packages") with queriable {
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

  def * = (id ~ name ~ task ~ creator ~ assignee ~ ccList ~
    status ~ osVersion ~ creationTime ~ lastUpdated <> (Package, Package.unapply _))

  def autoId = id ~ name ~ task ~ creator ~ assignee ~ ccList ~
    status ~ osVersion ~ creationTime ~ lastUpdated returning id

  def findAll = {
    DB.withSession { implicit session: Session =>
      val all = Query(this).list
    }
  }

  def findById(id: String) = {
    val uuid = Config.pkGenerator.fromString(id)
    DB.withSession { implicit session: Session =>
      val row = Query(this).where(_.id === uuid).first
    }
  }

  def insert(p: NewPackage)(implicit session: Session) = autoId.insert(
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