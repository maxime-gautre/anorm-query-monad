scalaVersion in ThisBuild := "2.12.10"

name := """anorm-query-monad"""

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, BuildInfoPlugin, SbtWeb)
  .settings(Settings.commonPlay: _*)
  .settings(
    libraryDependencies ++= Seq(
      jdbc,
      "com.typesafe.play" %% "anorm" % "2.5.3",
      "org.postgresql" % "postgresql" % "42.2.2"
    )
  )
