package models

import scala.slick.driver.PostgresDriver.simple._

object Cocktails extends Table[(Long, String, String)]("cocktails") {
  def id = column[Long]("ID")
  def name = column[String]("NAME")
  def xxx = column[String]("beauty")
  def * = id ~ name ~ xxx
}
