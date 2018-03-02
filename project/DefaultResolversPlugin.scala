import sbt.Keys._
import sbt._

object DefaultResolversPlugin extends AutoPlugin {

  val scalaz = "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

  val typeSafeResolver = "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/"

  val sprayResolver = "spray repo" at "http://repo.spray.io"

  val bintraySbtResolver = Resolver.url("bintray-sbt-plugins", url("https://dl.bintray.com/sbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

  val nativePackager = Resolver.url("sbt-plugin-releases on bintray", new URL("https://dl.bintray.com/sbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

  val mavenCentral = DefaultMavenRepository

  override lazy val projectSettings = Seq(
    // Add all needed resolvers
    resolvers ++= Seq(
      scalaz,
      typeSafeResolver,
      sprayResolver,
      bintraySbtResolver,
      nativePackager,
      mavenCentral)
  )
}
