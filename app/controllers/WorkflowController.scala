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

  def copy(wid: String) = Action { implicit request =>
    val pack = Workflows.mapToNew(Workflows.find(wid).id)
    val filledForm = form.fill(pack)
    Ok(views.html.workflows.newEntity(filledForm))
  }
}
