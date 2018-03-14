import com.typesafe.sbt.packager.universal.UniversalDeployPlugin
import sbt.Keys._
import sbt.{AutoPlugin, _}

object RootProjectPlugin extends AutoPlugin {

  override def requires: sbt.Plugins = UniversalDeployPlugin && DefaultResolversPlugin

  override lazy val projectSettings = Seq(
    organization in ThisBuild := "software.sigma",
    name := "Nucleobase",
    version := "0.1",
    scalaVersion := "2.12.4",
    //topLevelDirectory := None, // do not create top level directory in artifact
    crossPaths in ThisBuild := false, // Do not append Scala versions to the generated artifacts
    // use cached version for non changing libraries
    updateOptions in ThisBuild := updateOptions.value.withCachedResolution(cachedResoluton = true),
    fork in ThisBuild := true, // If true, forks a new JVM when running
    // do not publish root build. will fail on CI
    publish := false
  )

}
