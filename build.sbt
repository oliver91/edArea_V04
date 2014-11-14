name := """edArea_V04"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)
