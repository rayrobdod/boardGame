name := "Board Game Generic"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

version := "2.1.0-SNAPSHOT"

scalaVersion := "2.9.3"

crossScalaVersions ++= Seq("2.10.2", "2.9.2", "2.9.1", "2.11.0-M4")

mainClass := Some("com.rayrobdod.jsonTilesheetViewer.JSONTilesheetViewer")

libraryDependencies += ("com.rayrobdod" %% "json" % "1.0.0")

libraryDependencies += ("com.rayrobdod" %% "utilities" % "20130908")

// This import better be replaced by 2.0.1
libraryDependencies += ("com.rayrobdod" %% "csv" % "1.0.0")

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")

scalacOptions ++= Seq("-unchecked", "-deprecation" )

scalacOptions <++= scalaVersion.map{(sv:String) =>
	if (sv.take(3) == "2.1") {Seq("-feature", "-language:implicitConversions")} else {Nil}
}


// anon-fun-reduce
//autoCompilerPlugins := true
//
//addCompilerPlugin("com.rayrobdod" %% "anon-fun-reduce" % "1.0.0")
//
//libraryDependencies += ("com.rayrobdod" %% "anon-fun-reduce" % "1.0.0")

