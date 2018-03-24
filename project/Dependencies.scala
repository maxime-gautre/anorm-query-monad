import sbt._

object Dependencies {
  object scalatest {
    val version = "3.1.0"

    val play = "org.scalatestplus.play" %% "scalatestplus-play" % version
  }
}
