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
  enablePlugins(JavaAppPackaging).
  dependsOn(`common`).
  settings(
    mainClass in Compile := Some("software.sigma.nucleobase.DNAProducer"),
    dockerBaseImage := "anapsix/alpine-java",
    //   dockerEntrypoint := Seq("bin/start.sh"), //script to launch after docker container started
    dockerUpdateLatest := true,
    packageName in Docker := packageName.value,
    version in Docker := version.value,
  )
//Publisher2
lazy val `DNAMutator` = project.enablePlugins(ProjectPlugin, JavaAppPackaging).
  dependsOn(`common`).
  settings(
    mainClass in Compile := Some("software.sigma.nucleobase.DNAMutator"),
    dockerBaseImage := "anapsix/alpine-java",
    dockerUpdateLatest := true,
    packageName in Docker := packageName.value,
    version in Docker := version.value,
  )
//consumer/validator
lazy val `DNAValidator` = project.enablePlugins(ProjectPlugin, JavaAppPackaging).
  dependsOn(`common`, `nucleoAlpha`, `alpha`).
  settings(
    mainClass in Compile := Some("software.sigma.nucleobase.DNAValidator"),
    libraryDependencies ++= Seq(actor, akkaStrem, akkaHTTP, akkaJSON, scalatest, scalactic, akkaHTTPTest, akkaTestKit),
    dockerBaseImage := "anapsix/alpine-java",
    dockerUpdateLatest := true,
    packageName in Docker := packageName.value,
    version in Docker := version.value
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