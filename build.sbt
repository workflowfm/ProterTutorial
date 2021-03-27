ThisBuild / scalaVersion     := "2.12.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.workflowfm"
ThisBuild / organizationName := "WorkflowFM"

lazy val root = (project in file("."))
  .settings(
    name := "Proter Tutorial",
    libraryDependencies += "com.workflowfm" %% "proter" % "0.6.1"
  )


