package models

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp
import java.util.UUID

case class Package(
  name: String,
  creator: String,
  assignee: String,
  ccList: String,
  status: String,
  osVersion: String)

object Packages extends Table[(UUID, // id 
String, // name
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
  def creator = column[UUID]("creator_id")
  def assignee = column[UUID]("assignee_id")
  def ccList = column[String]("cc_list")
  def status = column[String]("status")
  def osVersion = column[String]("os_version")
  def creationTime = column[Timestamp]("creation_time", O.NotNull)
  def lastUpdated = column[Timestamp]("last_updated", O.NotNull)
  //def task = column[Task]("TASK_ID")
  // A package belongs to only one task: 			
  //def belongsToTask = foreignKey("TASK_FK", task, Task)(_.id)

  /**
   * def insertNew(id: UUID, p: Package) = Packages.insert(
   * id, p.name, UUID.fromString(p.creator),
   * UUID.fromString(p.assignee), p.ccList, p.status, p.osVersion,
   * None, None)*
   */

  def * = id ~ name ~ creator ~ assignee ~ ccList ~
    status ~ osVersion ~ creationTime ~ lastUpdated

}