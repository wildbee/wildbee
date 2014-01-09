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

  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "task" -> nonEmptyText,
      "creator" -> nonEmptyText,
      "assignee" -> nonEmptyText,
      "ccList" -> text,
      "status"-> text,
      "osVersion" -> nonEmptyText)(NewPackage.apply)(NewPackage.unapply))

  /**
   * Implements its own version of show because
   * we rely on a foreign primary key (task)
   * @param taskId
   * @param packageId
   * @return
   */
  def show(taskId: String, packageId: String) = Action { implicit request =>
    Ok(views.html.packages.show(Packages.findByTask(taskId, packageId)))
  }

  /**
   * Implements its own edit because of the task id foreign key.
   * @param taskId
   * @param packageId
   * @return
   */
  def edit(taskId: String, packageId: String) = Action { implicit request =>
    val pack = Packages.mapToNew(Packages.findByTask(taskId, packageId).id)
    val filledForm = form.fill(pack)
    val statuses = Transitions.allowedStatuses(pack.task,pack.name)
    Ok(views.html.packages.edit(filledForm, packageId, statuses))
  }

  /**
   * Implements its own update due to the task id being foreign key.
   * @param id
   * @return
   */
  def update(id: String) = Action { implicit request =>
    val oldPack = Packages.find(id)
    form.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.packages.edit(formWithErrors, oldPack.id.toString)),
      updatedPack => {
        Packages.update(Packages.mapToEntity(oldPack.id, updatedPack))
        Redirect(routes.PackagesController.show(oldPack.task.toString, oldPack.name))
          .flashing("success" -> "Package Updated!")
      })
  }

  /**
   * Packages implement their own copy method due to the
   * task foreign key constraint.
   * @param taskId
   * @param packageId
   * @return
   */
  def copy(taskId: String, packageId: String) = Action { implicit request =>
    val pack = Packages.mapToNew(Packages.findByTask(taskId, packageId).id)
    val filledForm = form.fill(pack)
    val statuses = Transitions.allowedStatuses(pack.task,pack.name)
    Ok(views.html.packages.newEntity(filledForm))
  }
}