import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._
import sbt._
import sbt.Keys._
//import com.typesafe.sbt.digest.SbtDigest.autoImport._
//import com.typesafe.sbt.gzip.SbtGzip.autoImport._
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import com.typesafe.sbt.web.SbtWeb.autoImport._
import play.sbt.PlayImport.{PlayKeys, ws}
import play.sbt.routes.RoutesKeys
import sbt.internal.io.Source
import sbtbuildinfo.BuildInfoPlugin.autoImport._
import sbtrelease.ReleasePlugin.autoImport._

object Settings {
  val timestamp = new java.text.SimpleDateFormat("yyyyMMdd-HHmm").format(new java.util.Date())

  lazy val common = Seq(
    scalacOptions := Seq(
      "-encoding",
      "UTF-8",
      "-target:jvm-1.8",
      "-Ywarn-adapted-args",
      "-Ywarn-inaccessible",
      "-Ywarn-nullary-override",
      "-Ywarn-infer-any",
      "-Ywarn-dead-code",
      "-Ywarn-unused",
      "-Ywarn-unused-import",
      "-Ywarn-value-discard",
      "-Ywarn-macros:after",
      "-Ywarn-numeric-widen",
      "-Ypartial-unification",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-g:vars",
      "-Xlint:_",
      "-opt:l:inline",
      "-opt-inline-from"
    ),
    // name dist with timestamp
    packageName in Universal := s"${name.value}-${version.value}-$timestamp",
  ) ++ scalaFmtSettings

  lazy val commonPlay = common ++ Seq(
    buildInfoPackage := "sbt",
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      BuildInfoKey.constant("buildTime" -> timestamp)
    ),
    RoutesKeys.routesImport := Seq(),
    // dont include local.conf in dist
    mappings in Universal := {
      val origMappings = (mappings in Universal).value
      origMappings.filterNot { case (_, file) => file.endsWith("local.conf") }
    },
    libraryDependencies ++= Seq(
      ws,
      Dependencies.scalatest.play % Test,
    ),
    releaseProcess := releaseSteps
  )

  lazy val releaseSteps = {
    import ReleaseTransformations._
    Seq[ReleaseStep](
      runClean,
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  }

  lazy val scalaFmtSettings = Seq(
    scalafmtVersion := "1.3.0",
    scalafmtOnCompile := true
  )

  def getSourceFile(source: Source): java.io.File = {
    val cl = classOf[Source]
    val baseField = cl.getDeclaredField("base")
    baseField.setAccessible(true)
    baseField.get(source).asInstanceOf[java.io.File]
  }
}
