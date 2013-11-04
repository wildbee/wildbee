Wildbee web application
=====================================
This project is a completely rewrite of [ETT][ETT] using Scala and the
[Play framework][playframework].

This project's goal is to track package upgrades that will eventually end up
in a release. Modules will be created to enhance the behaviors of the packages.

### How to compile and run:
Those instructions are destined for Linux/Mac users. Please consider switching
to those operating systems if you are using Windows. Your life will change.
```bash
cd ~
wget http://downloads.typesafe.com/play/2.2.1/play-2.2.1.zip
unzip play-2.2.1.zip
```

In your `~/.bashrc`, add this line:
```bash
PATH=$HOME/play-2.2.1:$PATH
```

Finally, touch your `~/.bashrc` file in a terminal to reflect those changes:
```bash
. ~/.bashrc
```

Then clone this repository, `cd` to it, and deploy the app!
```bashrc
git clone https://github.com/wildbee/wildbee.git
cd wildbee
play run
```

Now if you point your browser to `http://localhost:9000`, you should see our web
application. Wasn't that easy?

#### Database Setup
These instructions are valid for Fedora 19.

To install and setup Postgres
```
sudo yum install postgresql postgresql-server
su -
su - postgres
/usr/bin/initdb
sudo systemctl start postgresql.service
```

To be able to create a database from your own user:
- Add your username to postgres
```
su -
su - postgres
psql
```
Inside of the psql terminal, type:
```
create user <user> with password '1234';
alter user <user> createdb;
```

- In a new terminal with you as <user>, type in a terminal:
```
createdb <database_name>
```

- To be inside the database, type in the terminal:
```
psql <database_name>
```

For configuration of the database servers, click [here][scaladatabase].

### Versions Used
- Play framework: 2.2.1
- Slick: 1.0.1

[ETT]: https://github.com/liweinan/ett
[playframework]: http://www.playframework.com/
[scaladatabase]: http://www.playframework.com/documentation/2.2.1/ScalaDatabase

### Template controller setup with database access
```scala
package controllers

import play.api._
import play.api.mvc._
import views._

import models._
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.Database.threadLocalSession
import scala.slick.driver.PostgresDriver.simple._

object Application extends Controller {
  lazy val database = Database.forDataSource(DB.getDataSource())

  def index = Action {
    database withSession {
      Cocktails.insert(1, "haha", "hoho")
    }
    Ok("commited!")
  }
}
```

### Template model
```scala
package models

import scala.slick.driver.PostgresDriver.simple._

object Cocktails extends Table[(Long, String, String)]("cocktails") {
  def id = column[Long]("ID")
  def name = column[String]("NAME")
  def xxx = column[String]("beauty")
  def * = id ~ name ~ xxx
}
```
