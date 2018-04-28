resolvers ++= Seq(
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Public" at "https://oss.sonatype.org/content/repositories/public",
  "Zeiss VSTS Repository" at "https://zeissgroup.pkgs.visualstudio.com/_packaging/SIP/maven/v1/",
  "JFrog" at "http://repo.jfrog.org/artifactory/libs-releases/",
  "JBoss" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
  "MVNSearch" at "http://www.mvnsearch.org/maven2/"
)


lazy val server = (project in file("server")).settings(commonSettings).settings(
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,



  libraryDependencies ++= Seq(
    "org.elasticsearch" % "elasticsearch" % "6.2.4",
    "com.sksamuel.elastic4s" %% "elastic4s-core" % "6.2.6",
    "com.sksamuel.elastic4s" %% "elastic4s-testkit" % "6.2.6",
    "org.scalatest" %% "scalatest" % "3.0.5",
    "com.typesafe.akka" %% "akka-stream" % "2.5.12",
    "com.typesafe.play" %% "play-json" % "2.6.7"
  )

).enablePlugins(PlayScala).
  dependsOn(sharedJvm)



lazy val client = (project in file("client")).settings(commonSettings).settings(
  //scalaJSUseMainModuleInitializer := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.4"
  ),
  npmDependencies in Compile ++= Seq(
    "snabbdom" -> "0.5.3"
  ),
  jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv() // to make the DOM available
).enablePlugins(ScalaJSPlugin, ScalaJSWeb, ScalaJSBundlerPlugin).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).settings(commonSettings).settings(
  libraryDependencies ++= Seq(
    "com.typesafe.play" %%% "play-json" % "2.6.7"
  )
)
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.12.6",
  version := "1.0-" + new java.util.Date().getTime,

)

// loads the server project at sbt startup - if client project is wanted switch to "project client"
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}

