package models

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp
import java.util.Date
import java.util.UUID
import helpers._

case class Package(
  name: String,
  task: String,
  creator: String,
  assignee: String,
  ccList: String = "None",
  status: String,
  osVersion: String)

object Packages extends Table[(UUID, // id 
String, // name
UUID, // task id
UUID, // creator id
UUID, // assignee id
String, // cc-list: List of strings for cc emails?
String, // Status: String for now, must switch to Status once ready.
String, // OS version
Timestamp, // date created
Timestamp // date updated
)]("package") {

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

  def * = id ~ name ~ task ~ creator ~ assignee ~ ccList ~
    status ~ osVersion ~ creationTime ~ lastUpdated

  def autoId = id ~ name ~ task ~ creator ~ assignee ~ ccList ~
    status ~ osVersion ~ creationTime ~ lastUpdated returning id

  def insert(p: Package)(implicit session: Session) = autoId.insert(
    Config.pkGenerator.newKey,
    p.name,
    Config.pkGenerator.fromString(p.task),
    Config.pkGenerator.fromString(p.creator),
    Config.pkGenerator.fromString(p.assignee),
    p.ccList,
    p.status,
    p.osVersion,
    new Timestamp((new Date()).getTime()),
    new Timestamp((new Date()).getTime()))

}