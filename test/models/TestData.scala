package models

import helpers.Config
import play.api.test.FakeApplication

trait TestData {

  def fakeAppGen = FakeApplication(additionalConfiguration = Map(
  "db.default.url" -> "jdbc:postgresql://localhost/wildbeehivetest"))

  def clearDB() { //Order Matters
    Workflows.deleteAll
    Statuses.deleteAll
    Tasks.deleteAll
    Users.deleteAll
  }

  def randomID = Config.pkGenerator.newKey
  lazy val userID = Config.pkGenerator.newKey
  lazy val workflowID = Config.pkGenerator.newKey
  lazy val statusID = Config.pkGenerator.newKey
  lazy val taskID = Config.pkGenerator.newKey

  //TODO: Create a generator for models
  val task1 = NewTask("Task1", userID.toString(), workflowID.toString)
  val task2 = NewTask("Task2", userID.toString(), workflowID.toString)
  val user1 = User(userID,"User1", "email@example.com")
  val status1 = Status(userID, "Status1")
  val workflow1 = Workflow(workflowID, "Workflow1", statusID)

}