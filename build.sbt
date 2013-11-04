name := "wildbee"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4"
)

play.Project.playScalaSettings
