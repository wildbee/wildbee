package models

import play.api.db.slick.Config.driver.simple._
import java.util.UUID
import models.traits.CRUDOperations
import helpers._

case class NewPlugin(name: String) extends NewEntity
case class Plugin(id: UUID, name: String, path: String) extends Entity

//CRUDOperations is dependent on Entity Table?
object Plugins extends Table[Plugin]("plugins")
  with CRUDOperations[Plugin,NewPlugin]
  with EntityTable[Plugin, NewPlugin] {

  def path = column[String]("path")
  def * = id ~ name ~ path  <> (Plugin, Plugin.unapply _)
  def autoId = id ~ name ~ path returning id

  //mapToEntity required used by insert
  //Here name is
  def mapToEntity(id: UUID = newId, o: NewPlugin ): Plugin = {
    Plugin(id, o.name.split('.').last, o.name)
  }

  //mapToNew Required
  def mapToNew(id: UUID): NewPlugin = {
    NewPlugin("TEST")
  }

  def findPlugins: Map[String, String] = {
    ObserverHelper.mapIdToName
  }
}