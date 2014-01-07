package controllers
import models._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Action

object TasksController extends EntityController[Task, NewTask] {
  val table = models.Tasks
  val modelName = "tasks"
  val controller = routes.TasksController

  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "owner" -> nonEmptyText,
      "workflow" -> nonEmptyText)(NewTask.apply)(NewTask.unapply))

//  def index() = Action { implicit request =>
//    Ok(views.html.tasks.index())
//  }

//  def show(task: AnyRef) = Action { implicit request =>
//    Ok(views.html.tasks.show(Tasks.find(task)))
//  }

//  def newTask = Action { implicit request =>
//    Ok(views.html.tasks.newEntity(taskForm))
//  }

//  def create = Action { implicit request =>
//    form.bindFromRequest.fold(
//      formWithErrors => BadRequest(views.html.tasks.newEntity(formWithErrors)).flashing("success" -> "Package Created!"),
//      newTask => {
//        Tasks.insert(newInstance = newTask)
//        Redirect(routes.TasksController.show(newTask.name)).flashing("success" -> "Task Created!")
//      })
//  }
//
//  def delete(name: String) = Action { implicit request =>
//    Tasks.delete(name) match {
//      case Some(violatedDeps) =>
//        Redirect(routes.TasksController.show(name))
//        .flashing("failure" ->
//          (s"Packages: ${violatedDeps} depend on this task."
//          + "You must remove these packages if you would like to delete this Task."))
//      case None => Redirect(routes.TasksController.index)
//    }
//  }
}
