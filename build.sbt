scalaVersion in ThisBuild := "2.12.4"

name := """anorm-query-manad"""

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, BuildInfoPlugin, SbtWeb)
  .settings(Settings.commonPlay: _*)
  .settings(
    libraryDependencies ++= Seq()
  )
