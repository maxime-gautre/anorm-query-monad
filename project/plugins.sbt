// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.11")

// To keep an homogeneous code style
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.14")

// Add build infos into code
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")

// plugin to manage release cycle
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.6")

// assets pipeline
//addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")
//addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")
