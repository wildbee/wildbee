package models

import play.api.db.slick.Config.driver.simple._
import java.util.UUID
import models.traits.CRUDOperations
import helpers._

case class NewPlugin(name: String, pack: String) extends NewEntity
case class Plugin(id: UUID, name: String, path: String, pack: UUID) extends Entity

//CRUDOperations is dependent on Entity Table?
object Plugins extends Table[Plugin]("plugins")
  with CRUDOperations[Plugin,NewPlugin]
  with EntityTable[Plugin, NewPlugin] {

  def path = column[String]("path")
  def pack = column[UUID]("pack")

  def * = id ~ name ~ path ~ pack <> (Plugin, Plugin.unapply _)
  def autoId = id ~ name ~ path ~ pack returning id

  //mapToEntity required used by insert
  //Here the name is really a path....
  def mapToEntity(id: UUID = newId, o: NewPlugin ): Plugin = {
    Plugin(id, o.name.split('.').last, o.name, Packages.findUUID(o.pack))
  }

  //mapToNew Required
  def mapToNew(id: UUID): NewPlugin = {
    NewPlugin("TEST" , "TESTPACK")
  }

  def findPlugins: Map[String, String] = {
    ObserverHelper.mapIdToName
  }
}