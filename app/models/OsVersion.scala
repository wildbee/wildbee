package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import java.util.UUID
import helpers._

case class NewOsVersion(name: String)

case class OsVersion(id: UUID, name: String)

object OsVersions extends Table[OsVersion]("os_version") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def uniqueName = index("idx_name", name, unique = true)

  def * = id ~ name <> (OsVersion, OsVersion.unapply _)
  def autoId = id ~ name returning id

  def insert(name: String) = DB.withSession {
    implicit session: Session =>
      autoId.insert(Config.pkGenerator.newKey, name)
  }

  def findAll: List[OsVersion] = DB.withSession {
    implicit session: Session =>
      Query(this).list
  }

  def findByName(name: String): OsVersion = DB.withSession {
    implicit session: Session =>
      Query(this).where(_.name === name).first
  }
}
