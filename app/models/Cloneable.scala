package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.session.Session
import java.util.UUID
import scala.language.reflectiveCalls

/**
 * This trait is to allow models to create clones of themselves in the database.
 * Requires a Queriable object and all of its restrictions.
 * @tparam T
 */
trait Cloneable[T <: AnyRef with Queriable[T,Y] { val id: UUID; val name: String },
                Y <: AnyRef {val name: String}] {
  self: Table[T] =>

  def id: Column[UUID]
  def name: Column[String]
  def find: T
  def newId: UUID
  def mapToNew: Y

//  def clone(oid: UUID): UUID = DB.withSession {
//    implicit session: Session =>
//
//  }

 }