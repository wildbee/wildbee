import play.api._
import models.Plugins

//Documentation
//http://www.playframework.com/documentation/2.0.4/ScalaGlobal
//http://www.playframework.com/documentation/2.0/api/scala/play/api/GlobalSettings.html
object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Plugins.activate()
  }
}