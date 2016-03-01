name := """CodeKommune"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


libraryDependencies += "org.elasticsearch" % "elasticsearch" % "2.2.0"

libraryDependencies += "com.sksamuel.elastic4s" % "elastic4s-core_2.10" % "2.2.0"

libraryDependencies += "com.sksamuel.elastic4s" % "elastic4s-testkit_2.10" % "2.2.0"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "3.0.0-M15"


fork in run := false