package controllers

import play.api._
import play.api.mvc._
import models._
import java.util.UUID
import play.api.data._
import play.api.data.Forms._

object PackagesController extends EntityController[Package, NewPackage] {

  val modelName = "packages"
  val table = models.Packages
  val controller = routes.PackagesController

  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "task" -> nonEmptyText,
      "creator" -> nonEmptyText,
      "assignee" -> nonEmptyText,
      "ccList" -> text,
      "status"-> text,
      "osVersion" -> nonEmptyText)(NewPackage.apply)(NewPackage.unapply))

//  override def newEntity = Action { implicit request =>
//    Ok(views.html.packages.newEntity(form))
//  }

  /**
   * Packages have their own show controller method because
   * they rely on a foreign primary key (the task they belong to)
   * @param tid
   * @param pid
   * @return
   */
  def show(tid: String, pid: String) = Action { implicit request =>
    Ok(views.html.packages.show(Packages.findByTask(tid, pid)))
  }

  /**
   * Packages have their own edit controller method because
   * of the task id foreign key.
   * @param tid
   * @param pid
   * @return
   */
  def edit(tid: String, pid: String) = Action { implicit request =>
    val pack = Packages.mapToNew(Packages.findByTask(tid, pid).id)
    val filledForm = form.fill(pack)
    val statuses = Transitions.allowedStatuses(pack.task,pack.name)
    Ok(views.html.packages.edit(filledForm, pid, statuses))
  }

  /**
   * Packages implement their own copy method due to the
   * task foreign key constraint.
   * @param tid
   * @param pid
   * @return
   */
  def copy(tid: String, pid: String) = Action { implicit request =>
    val pack = Packages.mapToNew(Packages.findByTask(tid, pid).id)
    val filledForm = form.fill(pack)
    val statuses = Transitions.allowedStatuses(pack.task,pack.name)
    Ok(views.html.packages.newEntity(filledForm))
  }
}