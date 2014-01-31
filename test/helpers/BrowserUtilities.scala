package helpers

import play.api.test.TestBrowser

trait BrowserUtilities {
  def addUser(browser: TestBrowser, port: Int, user: String, email: String) {
    browser.goTo("http://localhost:" + port + "/user/new")
    browser.$("#name").text(user)
    browser.$("#email").text(email)
  }
}
