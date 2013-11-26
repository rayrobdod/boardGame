name := "Board Game Generic"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

version := "2.1.0-SNAPSHOT"

scalaVersion := "2.9.3"

crossScalaVersions ++= Seq("2.10.2", "2.9.2", "2.9.1", "2.11.0-M4")

mainClass := Some("com.rayrobdod.jsonTilesheetViewer.JSONTilesheetViewer")

libraryDependencies += ("com.rayrobdod" %% "json" % "1.0.0")

// update for new tag handler
libraryDependencies += ("com.rayrobdod" %% "utilities" % "20130908")

libraryDependencies += ("net.sf.opencsv" % "opencsv" % "2.3")

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")

scalacOptions ++= Seq("-unchecked", "-deprecation" )

scalacOptions <++= scalaVersion.map{(sv:String) =>
	if (sv.take(3) == "2.1") {Seq("-feature", "-language:implicitConversions")} else {Nil}
}

// license nonsense
licenses += (("GPLv3 or later", new java.net.URL("http://www.gnu.org/licenses/") ))

// anon-fun-reduce
//autoCompilerPlugins := true
//
//addCompilerPlugin("com.rayrobdod" %% "anon-fun-reduce" % "1.0.0")
//
//libraryDependencies += ("com.rayrobdod" %% "anon-fun-reduce" % "1.0.0")

