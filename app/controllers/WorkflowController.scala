package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.data._
import play.api.data.Forms._
import helpers.Config
import java.util.UUID

object WorkflowController extends Controller {

  val workForm = Form(
    mapping(
      "name"   -> nonEmptyText,
      "status" -> list(text))(NewWorkflow.apply)(NewWorkflow.unapply))

  def newWorkflow = Action { implicit request =>
    Ok(views.html.workflows.newEntity(workForm))
  }

  def index = Action { implicit request =>
    Ok(views.html.workflows.index())
  }

  def show(name: String) = Action { implicit request =>
    Ok(views.html.workflows.show(Workflows.find(name)))
  }

  /** When creating a workflow create its logic first */
  def create() = Action { implicit request =>
      workForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index("Error Creating Workflow :: " + errors)),
        workflow => {
          Workflows.insert(workflow)
          Transitions.create(Workflows.nameToId(workflow.name),  workflow.status)
          Redirect(routes.WorkflowController.show(workflow.name))
        }
    )
  }

  /** When deleting a workflow delete its logic first */
  def delete(name: String) = Action { implicit request =>
     Workflows.delete(name) /*match {
       case Some(violatedDeps) =>
         Redirect(routes.WorkflowController.show(name))
         .flashing("failure" ->
         (s"Tasks: $violatedDeps depend on this workflow."
         + "You must remove or modify these tasks if you would like to delete this workflow."))
       case None =>
         Redirect(routes.WorkflowController.index)
     }*/
     Redirect(routes.WorkflowController.index)
  }

  def edit(workflow: String) = Action { implicit request =>
    val id = Workflows.find(workflow).id
    val form = workForm.fill(Workflows.mapToNew(id))
    Ok(views.html.workflows.edit(form, id.toString))
  }

  def update(workflow: String) = Action { implicit request =>
    val old = Workflows.find(workflow)
    workForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.workflows.edit(formWithErrors, old.id.toString)),
    updatedWorkflow => {
      Workflows.update(Workflows.mapToEntity(updatedWorkflow, old.id))
      Redirect(routes.WorkflowController.show(updatedWorkflow.name))
    })

  }

  def copy(wid: String) = Action { implicit request =>
    val pack = Workflows.mapToNew(Workflows.find(wid).id)
    val filledForm = workForm.fill(pack)
    Ok(views.html.workflows.newEntity(filledForm))
  }
}
