import Dependencies._
import sbt.Keys.libraryDependencies

//the parent project, aggregating the others
val `nucleobase` = project.in(file("."))
  .enablePlugins(RootProjectPlugin)
  .aggregate(`common`,
    `DNAProducer`,
    `DNAMutator`,
    `DNAValidator`,
    `alpha`,
    `nucleoAlpha`)

// modules
//common module for JMS connection, producers and consumers
lazy val `common` = project.enablePlugins(ProjectPlugin).
  settings(
    libraryDependencies ++= Seq(logback, scalatest, sprayJSON, activemq, jms, actor, akkaStrem)
  )
//Publisher1
lazy val `DNAProducer` = project.enablePlugins(ProjectPlugin).
  dependsOn(`common`).
  settings(
    mainClass in Compile := Some("software.sigma.nucleobase.DNAProducer"),
    dockerBaseImage := "image name from docker hub",
    dockerEntrypoint := Seq("bin/start.sh"), //script to launch after docker container started
  )
//Publisher2
lazy val `DNAMutator` = project.enablePlugins(ProjectPlugin).
  dependsOn(`common`).
  settings(
    mainClass in Compile := Some("software.sigma.nucleobase.DNAMutator")
  )
//consumer/validator
lazy val `DNAValidator` = project.enablePlugins(ProjectPlugin).
  dependsOn(`common`, `nucleoAlpha`).
  settings(
    mainClass in Compile := Some("software.sigma.nucleobase.DNAValidator"),
    libraryDependencies ++= Seq(actor, akkaStrem, akkaHTTP, akkaJSON)
  )
//basic alpha algorithm
lazy val `alpha` = project.enablePlugins(ProjectPlugin).
  settings(
    libraryDependencies ++= Seq(logback, scalatest, sprayJSON, activemq, jms)
  )
//alpha algorithm for nucleo base pair
lazy val `nucleoAlpha` = project.enablePlugins(ProjectPlugin).
  settings(
    libraryDependencies ++= Seq(logback, scalatest, sprayJSON, activemq, jms)
  )