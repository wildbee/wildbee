package controllers

import play.api._
import play.api.mvc._

import models._

import play.api.data._
import play.api.data.Forms._

object WorkflowController extends Controller {

  val workForm = Form(
    mapping(
      "name"   -> nonEmptyText,
      "status" -> list(text))(NewWorkflow.apply)(NewWorkflow.unapply))

  def newWorkflow = Action {
    Ok(views.html.workflows.newEntity(
        workForm, List("Open", "In Progress", "Pending", "Closed"))) //TODO: Make it easier to create workflow, Ex. Checkboxes, pictures, etc
  }

  def index = Action {
    Ok(views.html.workflows.index())
  }

  def show(name: String) = Action {
    Ok(views.html.workflows.show(Workflows.findByName(name)))
  }

  /** When creating a workflow creat its logic first */
  def create() = Action {
    implicit request =>
      workForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index("Error Creating Workflow :: " + errors)),
        workflow => {
          AllowedStatuses.create(workflow.name, workflow.status)
          Workflows.create(workflow.name)
          Redirect(routes.WorkflowController.show(workflow.name))
        }
    )
  }

  /** When deleting a workflow delete its logic first */
  def delete(name: String) = Action {
    implicit request => {
      AllowedStatuses.delete(name)
      Workflows.delete(name)
    }
    Redirect(routes.WorkflowController.index)
  }
}
