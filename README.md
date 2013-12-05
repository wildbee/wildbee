Wildbee web application
=====================================
[![Build Status](https://travis-ci.org/wildbee/wildbee.png)](https://travis-ci.org/wildbee/wildbee)

This project is a complete rewrite of [ETT][ETT] using Scala and the
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

In your `~/.bashrc`, add this line so that we have access to the `play` command
from the terminal:
```bash
PATH=$HOME/play-2.2.1:$PATH
```

Finally, touch your `~/.bashrc` file in a terminal to reflect those changes:
```bash
. ~/.bashrc
```

Then clone this repository, `cd` to it, and deploy the app!
```bash
git clone https://github.com/wildbee/wildbee.git
cd wildbee
play run

# use `play ~run` for continuous compilation after a file change
```

Now if you point your browser to `http://localhost:9000`, you should see our web
application. Wasn't that easy?

# How to run tests
```bash
play test

# use `play ~test` for continous compilation and test runs after a file change
```

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

- Add the _wildbee_ user to postgres
```
su -
su - postgres
psql
```
Inside of the psql terminal, type:
```
create user wildbee with password '1234';
alter user wildbee createdb;
```

- Create the databases that our web application will use:
```
createdb wildbeehive -U wildbee
createdb wildbeehivetest -U wildbee
```
_wildbeehive_ is the name of the database we will be using.
_wildbeehivetest_ is the name of the database we will be using when running
tests so as not to affect the main database..

- To be inside the database, type in the terminal:
```
psql <database_name> -U wildbee
```

For configuration of the database servers, click [here][scaladatabase].

### Versions Used
- Play framework: 2.2.1
- Slick: 1.0.1

[ETT]: https://github.com/liweinan/ett
[playframework]: http://www.playframework.com/
[scaladatabase]: http://www.playframework.com/documentation/2.2.1/ScalaDatabase

