package models

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp
import java.util.UUID

/**
 * 
id
name
creator
assignee
cc-list
status
Date created
Date updated
OS version
 */

object Packages extends Table[(
  UUID, // id 
  String, // name
  UUID, // creator id
  UUID, // assignee id
  String, // cc-list: List of strings for cc emails?
  String, // Status: String for now, must switch to Status once ready.
  String, // OS version
  Timestamp, // date created
  Timestamp // date updated
  )]("PACKAGES") {
  
  def id = column[UUID]("ID", O.PrimaryKey)
  def name = column[String]("NAME")
  def creator = column[UUID]("CREATOR_ID")
  def assignee = column[UUID]("ASSIGNEE_ID")
  def ccList = column[String]("CC_LIST")
  def status = column[String]("STATUS")
  def osVersion = column[String]("OS_VERSION")
  def creationTime = column[Timestamp]("CREATION_TIME",O.NotNull)
  def lastUpdated = column[Timestamp]("LAST_UPDATED",O.NotNull)
  //def task = column[Task]("TASK_ID")
  // A package belongs to only one task: 			
  //def belongsToTask = foreignKey("TASK_FK", task, Task)(_.id)
  
  def * = id ~ name ~ creator ~ assignee ~ ccList ~ 
  			status ~ osVersion ~ creationTime ~ lastUpdated
  			
  

  
}