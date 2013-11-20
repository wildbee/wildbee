package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.sql.Timestamp
import java.util.Date
import java.util.UUID
import helpers._

/**
 * The Entity trait can be shared by entities in our
 * models since it will define all of the basic CRUD operations
 * in a generalized way.
 */
trait Queriable[T <: AnyRef { val id: UUID }] {
  self: Table[T] =>

  def id: Column[UUID]

  def name: Column[String]

  def * : scala.slick.lifted.ColumnBase[T]

  def returnID = * returning id

  def findAll: List[T] = DB.withSession {
    implicit session: Session =>
      Query(this).list
  }

  def findById(id: String): T = DB.withSession {
    implicit session: Session =>
      Query(this).where(_.id === uuid(id)).first
  }

  //  def findMappedById(id: String): Y = DB.withSession {
  //    implicit session: Session =>
  //      tableToQuery(this).filter(_.id === uuid(id)).map(item => mappedEntity).first
  //  }

  /**
   * To use this generalized insert trait, you need to pass
   * the correct case class T into it. That means that the model
   * needs to implement its own mapping from user inputs to
   * case class.
   */
  def insert(item: T) = {
    DB.withSession { implicit session: Session =>
      returnID.insert(item)
    }
  }

  /**
   * Same as insert above. Need to map your inputs to the correct
   * class of type T, then pass that into this method.
   */
  def update(id: UUID, item: T) = DB.withSession {
    implicit session: Session =>
      tableQueryToUpdateInvoker(
        tableToQuery(this).where(_.id === id)).update(item)
  }

  def delete(id: UUID) = DB.withSession {
    implicit session: Session =>
      queryToDeleteInvoker(
        tableToQuery(this).where(_.id === id)).delete
  }

  def getUserMap: Map[String, String] = DB.withSession {
    implicit session: Session =>
      Query(Users).list.map(u => (u._1.toString, u._2)).toMap
  }

  def getTaskMap: Map[String, String] = DB.withSession {
    implicit session: Session =>
      Query(Tasks).list.map(t => (t._1.toString, t._2)).toMap
  }

  def uuid(id: String): UUID = {
    Config.pkGenerator.fromString(id)
  }

  def currentTimestamp: Timestamp = {
    new Timestamp((new Date()).getTime())
  }

  def newId: UUID = {
    Config.pkGenerator.newKey
  }

}