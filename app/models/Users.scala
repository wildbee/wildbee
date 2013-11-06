package models

import scala.slick.driver.PostgresDriver.simple._

object Users extends Table[(Long, String, String, Boolean)]("users") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def email = column[String]("EMAIL")
  def admin = column[Boolean]("ADMIN", O.Default[Boolean](false))
  def * =  id ~ name ~ email ~ admin

  def autoInc = name ~ email ~ admin returning id

  def insert(name: String,
             email: String,
             admin: Boolean)
           (implicit session: Session) = autoInc.insert(name, email, admin)
           
  }
