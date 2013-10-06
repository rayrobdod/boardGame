name := "Board Game Generic"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

version := "2.0.0-SNAPSHOT"

scalaVersion := "2.9.3"

crossScalaVersions ++= Seq("2.10.2", "2.9.2", "2.9.1", "2.11.0-M4")

mainClass := Some("com.rayrobdod.jsonTilesheetViewer.JSONTilesheetViewer")

libraryDependencies += ("com.rayrobdod" %% "json" % "1.0.0")

libraryDependencies += ("com.rayrobdod" %% "utilities" % "20130908")

// This import better be replaced by 2.0.1
libraryDependencies += ("com.rayrobdod" %% "csv" % "1.0.0")


