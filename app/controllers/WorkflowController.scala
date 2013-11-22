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
      "status" -> list(text)
    )(NewWorkflow.apply)(NewWorkflow.unapply))
  
  def newWorkflow = Action {
    Ok(views.html.workflows.newEntity(workForm, List("Open", "In Progress", "Pending", "Closed")))
  }
  def index = Action {
    Ok(views.html.workflows.index())
  }
 
  def create() = Action {
    implicit request =>
      workForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index("Error Creating Task :: " + errors)),
        workflow => {
          AllowedStatuses.create(workflow.name, workflow.status)
          Workflows.create(workflow.name)
          Redirect(routes.WorkflowController.index)
        }
      )
  }
}