import sbt._

object Dependencies {
  // scalastyle:off
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

  //testing
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  val scalactic = "org.scalactic" %% "scalactic" % "3.0.5"
  val akkaHTTPTest = "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % Test
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % "2.5.9" % Test

  //jsons
  val akkaJSON = "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0-RC1"
  val sprayJSON = "io.spray" %% "spray-json" % "1.3.3"

  //message bus
  val activemq = "org.apache.activemq" % "activemq-client" % "5.14.5" exclude("org.apache.geronimo.specs", "geronimo-jms_1.1_spec")
  val jms = "javax" % "javaee-api" % "7.0"

  //akka
  val actor = "com.typesafe.akka" %% "akka-actor" % "2.5.9"
  val akkaStrem = "com.typesafe.akka" %% "akka-stream" % "2.5.9"
  val akkaHTTP = "com.typesafe.akka" %% "akka-http" % "10.0.11"

  val config = "com.typesafe" % "config" % "1.3.2"
}
