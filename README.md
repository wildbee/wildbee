Wildbee web application
=====================================
This project is a completely rewrite of [ETT] using Scala and the
[playframework].

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
TODO

### Versions Used
Play framework
: 2.2.1
Slick
: 1.0.1

[ETT]: https://github.com/liweinan/ett (ETT)
[playframework]: http://www.playframework.com/ (Play framework)
