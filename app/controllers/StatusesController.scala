package controllers

import models._

import play.api.data._
import play.api.data.Forms._

object StatusesController extends EntityController[Status, NewStatus] {
  val table = models.Statuses
  val modelName = "statuses"

  val form = Form(
    mapping(
      "statusName" -> nonEmptyText
  )(NewStatus.apply)(NewStatus.unapply))

}