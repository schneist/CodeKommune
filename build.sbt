name := """CodeKommune"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  javaWs,
  specs2 % Test
)


libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "4.0.0-RC1",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0-RC1",
  "com.mohiva" %% "play-silhouette-persistence" % "4.0.0-RC1",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0-RC1",
  filters
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


libraryDependencies += "org.elasticsearch" % "elasticsearch" % "2.3.2"

libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-core" % "2.3.0"

libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-testkit" % "2.3.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6"



fork in run := false