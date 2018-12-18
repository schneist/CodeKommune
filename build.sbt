import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

resolvers ++= Seq(
  "Apollo Bintray" at "https://dl.bintray.com/apollographql/maven/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Public" at "https://oss.sonatype.org/content/repositories/public",
  "JFrog" at "http://repo.jfrog.org/artifactory/libs-releases/",
  "JBoss" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
  "MVNSearch" at "http://www.mvnsearch.org/maven2/"
)


lazy val server = (project in file("server")).settings(commonSettings).settings(
  scalaJSProjects := Seq(frontend),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "org.elasticsearch" % "elasticsearch" % "6.2.4",
    "com.sksamuel.elastic4s" %% "elastic4s-core" % "6.2.9",
    "com.sksamuel.elastic4s" %% "elastic4s-testkit" % "6.2.9",
    "org.scalatest" %% "scalatest" % "3.0.5",
    "org.sangria-graphql" %% "sangria" % "1.4.1",
    "org.sangria-graphql" %% "sangria-relay" % "1.4.1",
    "org.sangria-graphql" %% "sangria-play-json" % "1.0.4",
    "com.vmunier" %% "scalajs-scripts" % "1.1.2",
  )

).enablePlugins(PlayScala, WebScalaJSBundlerPlugin)
  .dependsOn(commonJVM)

lazy val frontend = (project in file("frontend")).settings(commonSettings).settings(
  scalaJSUseMainModuleInitializer := true,

  // yes, we want to package JS dependencies
  skip in packageJSDependencies := false,

  name := "frontend",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.6"
  ),
  scalacOptions += "-P:scalajs:sjsDefinedByDefault",
  libraryDependencies ++= Seq(
    "me.shadaj" %%% "slinky-core" % "0.5.1",
    "me.shadaj" %%% "slinky-web" % "0.5.1",
    "me.shadaj" %%% "slinky-hot" % "0.5.1",
    "me.shadaj" %%% "slinky-scalajsreact-interop" % "0.5.1",
    "com.apollographql" %%% "apollo-scalajs-react" % "0.4.3",
  ),

  resolvers ++= Seq(
    "Apollo Bintray" at "https://dl.bintray.com/apollographql/maven/",
    "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Public" at "https://oss.sonatype.org/content/repositories/public",
    "JFrog" at "http://repo.jfrog.org/artifactory/libs-releases/",
    "JBoss" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
    "MVNSearch" at "http://www.mvnsearch.org/maven2/"
  ),
  npmDependencies in Compile ++= Seq(
    "react" -> "16.6.3",
    "react-dom" -> "16.6.3",
    "react-apollo" -> "2.1.0",
    "apollo-boost" -> "0.1.3",
    "graphql-tag" -> "2.8.0",
    "graphql" -> "0.13.2"
  ),

  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full),

).enablePlugins(ScalaJSPlugin,ScalaJSBundlerPlugin, ScalaJSWeb)
  .dependsOn(commonJS)


lazy val common =  crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("common"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %%% "play-json" % "2.6.9",
      "org.typelevel" %%% "cats-core" % "1.4.0",
      "org.typelevel" %%% "cats-free" % "1.4.0",
      "org.typelevel" %%% "cats-laws" % "1.4.0",
    ),


  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "com.apollographql" %%% "apollo-scalajs-react" % "0.4.0",
    ),

    resolvers ++= Seq(
      "Apollo Bintray" at "https://dl.bintray.com/apollographql/maven/",
      "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Public" at "https://oss.sonatype.org/content/repositories/public",
      "JFrog" at "http://repo.jfrog.org/artifactory/libs-releases/",
      "JBoss" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
      "MVNSearch" at "http://www.mvnsearch.org/maven2/"
    )

  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.sangria-graphql" %% "sangria" % "1.4.2",
      "org.sangria-graphql" %% "sangria-relay" % "1.4.2",
      "org.sangria-graphql" %% "sangria-play-json" % "1.0.5",
    ),
  ).enablePlugins(GitVersioning)


lazy val commonJS :Project = common.js

lazy val commonJVM :Project = common.jvm




lazy val commonSettings = Seq(
  scalaVersion := "2.12.7",

)

// loads the server project at sbt startup - if client project is wanted switch to "project client"
//onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}

