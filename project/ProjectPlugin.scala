import sbt._
import sbt.Keys._

trait LocalDependencies {
  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.16"
  val log4j = "org.slf4j" % "slf4j-log4j12" % "1.7.16"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  val scalactic = "org.scalactic" %% "scalactic" % "3.0.5"
}

object ProjectPlugin extends AutoPlugin with LocalDependencies {

  override lazy val projectSettings = Seq(
    exportJars := true, // generate jar file
    libraryDependencies ++= Seq(scalatest, scalactic)
  )
}
