ThisBuild / scalaVersion     := "3.1.0"
ThisBuild / version          := "0.2.0-SNAPSHOT"
ThisBuild / organization     := "com.workflowfm"
ThisBuild / organizationName := "WorkflowFM"

lazy val root = (project in file("."))
  .settings(
    name := "Proter Tutorial",
    libraryDependencies += "com.workflowfm" %% "proter" % "0.7.4+134-f51bcd92-SNAPSHOT",

    scalacOptions ++= {
      Seq(
        "-encoding",
        "UTF-8",
        "-feature",
        "-deprecation",
        "-unchecked",
      )
    },

  )


