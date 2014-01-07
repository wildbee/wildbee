package models

import play.api.db.slick.Config.driver.simple._
import java.util.UUID
import models.traits.CRUDOperations
import helpers._

case class NewPlugin(name: String, pack: String) extends NewEntity
case class Plugin(id: UUID, name: String, path: String, pack: UUID) extends Entity

object Plugins extends Table[Plugin]("plugins")
  with CRUDOperations[Plugin,NewPlugin]
  with EntityTable[Plugin, NewPlugin] {

  def path = column[String]("path")
  def pack = column[UUID]("pack")

  def * = id ~ name ~ path ~ pack <> (Plugin, Plugin.unapply _)
  def autoId = id ~ name ~ path ~ pack returning id

  def mapToEntity(id: UUID = newId, o: NewPlugin ): Plugin = {
    Plugin(id, o.name.split('.').last, o.name, Packages.findUUID(o.pack))
  }

  def mapToNew(id: UUID): NewPlugin = {
    NewPlugin("TEST" , "TESTPACK")
  }

  override def afterInsert(id: UUID, newInstance: NewPlugin) = {
    activate(find(id))
  }

  def findPlugins: Map[String, String] = {
    ObserverHelper.mapIdToName
  }

  def activate(plugin: Plugin): Unit = activate(List(plugin))
  def activate(plugins: List[Plugin] = findAll): Unit = {
    plugins foreach ( p => Packages.addObserver(p.path, p.pack) )
  }
}