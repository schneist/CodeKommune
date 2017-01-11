import sbt.Keys._
import sbt.Project.projectToRef

name := "CodeKommune"

lazy val scalaV = "2.11.8"
lazy val clients = Seq(client)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(
    scalaVersion := scalaV,
    publishArtifact := false,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.5.8",
      "org.scalatest" %% "scalatest" % "2.2.6"
    )
  ).
  jsConfigure(_ enablePlugins ScalaJSPlay)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js


lazy val server = (project in file("server")).settings(
  version := "1.1-SNAPSHOT",
  resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases" ,
  resolvers += Resolver.jcenterRepo,
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  mainClass := Some("application.SimpleApplicationLoader"),
  libraryDependencies ++= Seq(
    jdbc,
    cache,
    ws,
    javaWs,
    specs2 % Test
  ),

  libraryDependencies ++= Seq(
    "org.elasticsearch" % "elasticsearch" % "5.0.0",
    "com.sksamuel.elastic4s" %% "elastic4s-core" % "5.0.0",
    "com.sksamuel.elastic4s" %% "elastic4s-testkit" % "5.0.0",
    "org.scalatest" %% "scalatest" % "2.2.6",
    "com.typesafe.akka" %% "akka-stream" % "2.4.16"
  )

).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)



lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  publishArtifact := false,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value

