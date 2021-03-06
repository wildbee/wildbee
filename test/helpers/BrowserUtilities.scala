package helpers

import play.api.test.TestBrowser

trait BrowserUtilities extends RandomUtilities {

  /** Add a user via the /user/new page */
  def addUser(browser: TestBrowser, port: Int, user: String = randString,
              email: String = randEmail ) = {
    browser.goTo("http://localhost:" + port + "/user/new")
    browser.$("#name").text(user)
    browser.$("#email").text(email)
    browser.$("#newUser").click()
    email
  }

  /** Delete a user from the show user page */
  def removeUser(browser: TestBrowser, port: Int, email: String) {
    browser.goTo(s"http://localhost:$port/user/$email")
    browser.$("#deleteUser").click()
  }

  def removeTask(browser: TestBrowser, port: Int, task: String){
    browser.goTo(s"http://localhost:$port/task/${task}")
    browser.$("#deleteTask").click()
  }

  def addStatus(browser: TestBrowser, port: Int, name: String ) = {
    browser.goTo(s"http://localhost:$port/workflow/new")
    browser.$("#statusName").text(name)
    browser.$("#newStatus").click()
  }
}
