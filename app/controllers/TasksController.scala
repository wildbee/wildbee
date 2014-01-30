package controllers
import models._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Action

object TasksController extends EntityController[Task, NewTask] with CloneableEntity[Task,NewTask] {
  val table = models.Tasks
  val modelName = "tasks"

  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "owner" -> nonEmptyText,
      "workflow" -> nonEmptyText)(NewTask.apply)(NewTask.unapply))
}
