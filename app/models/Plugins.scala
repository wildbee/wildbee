package models

import play.api.db.slick.Config.driver.simple._
import java.util.UUID
import models.traits.CRUDOperations
import helpers._

case class NewPlugin(name: String, pack: Option[String]) extends NewEntity
case class Plugin(id: UUID, name: String, path: String, pack: Option[UUID]) extends Entity

object Plugins extends Table[Plugin]("plugins")
  with CRUDOperations[Plugin,NewPlugin]
  with EntityTable[Plugin, NewPlugin] {

  def path = column[String]("path")
  def pack = column[Option[UUID]]("pack")

  def * = id ~ name ~ path ~ pack <> (Plugin, Plugin.unapply _)
  def autoId = id ~ name ~ path ~ pack returning id

  def mapToEntity(id: UUID = newId, o: NewPlugin ): Plugin = {
    Plugin(id, o.name.split('.').last, o.name, findUUID(o.pack))
  }

  def mapToNew(id: UUID): NewPlugin = {
    find(id) match {
      case Some(plugin) => NewPlugin(plugin.path, Some(plugin.pack.toString()))
    }
  }

  override def afterInsert(id: UUID, newInstance: NewPlugin) = {
    find(id) match {
      case Some(plugin) => activate(plugin)
      case None =>
    }
  }

  override def afterUpdate(plugin: Plugin) = {
    deactivate(plugin)
    activate(plugin)
  }

  override def beforeDelete(id: UUID) {
    find(id) match {
      case Some(plugin) => deactivate(plugin)
      case None =>
    }
  }

  def findPlugins: Map[String, String] = {
    ObserverHelper.mapIdToName
  }

  /** Initilize New Plugins
   *  If new plugins have been added, then put them into the database.
   *  Ensure not to re-add plugins.
   */
  def initializeNewPlugins() = {
    val getRealName = (n: String) => n.split('.').last
    val plugins = ObserverHelper.mapIdToName map { case (path, name) => NewPlugin(path, None) }
    val pluginNames =  plugins map (p => p.name.split('.').last)
    plugins map { plugin => find(getRealName(plugin.name)) match {
      case None => insert(newInstance = plugin)
      case Some(x) =>
      }
    }
  }

  /** Deactivate a plugin, a list of plugins
   *  All plugins will be activated if not arguement is given
   */
  def deactivate(plugin: Plugin): Unit = deactivate(List(plugin))
  def deactivate(plugins: List[Plugin] = findAll): Unit = {
    plugins foreach ( p => Packages.removeObserver(p.path) )
  }

  /** Active a plugin, a list of plugins
   *  All plugins will be activated if not arguement is given
   */
  def activate(plugin: Plugin): Unit = activate(List(plugin))
  def activate(plugins: List[Plugin] = findAll): Unit = {
    plugins foreach ( p => (p.path,p.pack) match {
      case (_, None) =>
      case (path, Some(pack)) => Packages.addObserver(path, pack)
    })
  }
}