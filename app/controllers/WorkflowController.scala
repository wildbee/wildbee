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

  def newWorkflow = Action { implicit request =>
    Ok(views.html.workflows.newEntity(workForm))
  }

  def index = Action { implicit request =>
    Ok(views.html.workflows.index())
  }

  def show(name: String) = Action { implicit request =>
    Ok(views.html.workflows.show(Workflows.find(name)))
  }

  /** When creating a workflow creat its logic first */
  def create() = Action { implicit request =>
      workForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index("Error Creating Workflow :: " + errors)),
        workflow => {
          Transitions.create(workflow.name, workflow.status)
          Workflows.insert(workflow)
          Redirect(routes.WorkflowController.show(workflow.name))
        }
    )
  }

  /** When deleting a workflow delete its logic first */
  def delete(name: String) = Action { implicit request => {
      Transitions.delete(name)
      Workflows.delete(name)
    }
    Redirect(routes.WorkflowController.index)
  }
}
