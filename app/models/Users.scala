package models

import scala.slick.driver.PostgresDriver.simple._

case class User(name: String, email: String)

object Users extends Table[(Long, String, String)]("users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def * =  id ~ name ~ email

  def autoInc = name ~ email returning id

  def insert(name: String,
             email: String)
            (implicit session: Session) = autoInc.insert(name, email)
}

