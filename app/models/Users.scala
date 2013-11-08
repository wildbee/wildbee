package models

import scala.slick.driver.PostgresDriver.simple._

object Users extends Table[(Long, String, String)]("users") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def email = column[String]("EMAIL")

  // TODO: We'd better move it to SystemAdministrator and UserAdminstor tables
  // def admin = column[Boolean]("ADMIN", O.Default[Boolean](false))

  def * =  id ~ name ~ email

  def autoInc = name ~ email returning id

  def insert(name: String,
             email: String
            )
           (implicit session: Session) = autoInc.insert(name, email)
}
