package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.data._
import play.api.data.Forms._
import helpers.Config
import java.util.UUID

object WorkflowController extends EntityController[Workflow,NewWorkflow] {
  val table = models.Workflows
  val modelName = "workflows"

  val form = Form(
    mapping(
      "name"   -> nonEmptyText,
      "status" -> list(text))(NewWorkflow.apply)(NewWorkflow.unapply))

//  def newWorkflow = Action { implicit request =>
//    Ok(views.html.workflows.newEntity(form))
//  }

//  def index = Action { implicit request =>
//    Ok(views.html.workflows.index())
//  }

//  def show(name: String) = Action { implicit request =>
//    Ok(views.html.workflows.show(Workflows.find(name)))
//  }

//  def create() = Action { implicit request =>
//      workForm.bindFromRequest.fold(
//        errors => BadRequest(views.html.index("Error Creating Workflow :: " + errors)),
//        workflow => {
//          Workflows.insert(newInstance = workflow)
//          Redirect(routes.WorkflowController.show(workflow.name))
//        }
//    )
//  }

//  /**
//   * We should get the specialized validation logic out of the delete controller method and into
//   * a method that just does the validation. Then we can start creating generalized validators that
//   * work with all CRUDeable entity models.
//   */
//  def delete(name: String) = Action { implicit request =>
//     Workflows.delete(name) match {
//       case Some(violatedDeps) =>
//         Redirect(routes.WorkflowController.show(name))
//         .flashing("failure" ->
//         (s"Tasks: $violatedDeps depend on this workflow."
//         + "You must remove or modify these tasks if you would like to delete this workflow."))
//       case None =>
//         Redirect(routes.WorkflowController.index)
//     }
//  }

//  def edit(workflow: String) = Action { implicit request =>
//    val id = Workflows.find(workflow).id
//    val form = form.fill(Workflows.mapToNew(id))
//    Ok(views.html.workflows.edit(form, id.toString))
//  }

//  def update(workflow: String) = Action { implicit request =>
//    val old = Workflows.find(workflow)
//    form.bindFromRequest.fold(
//      formWithErrors => BadRequest(views.html.workflows.edit(formWithErrors, old.id.toString)),
//    updatedWorkflow => {
//      Workflows.update(old.id, updatedWorkflow)
//      Redirect(routes.WorkflowController.show(updatedWorkflow.name))
//    })
//  }

  def copy(wid: String) = Action { implicit request =>
    val pack = Workflows.mapToNew(Workflows.find(wid).id)
    val filledForm = form.fill(pack)
    Ok(views.html.workflows.newEntity(filledForm))
  }
}
