name := "Board Game Generic"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

version := "3.0.0-SNAPSHOT"

scalaVersion := "2.9.3"

crossScalaVersions ++= Seq("2.10.3", "2.9.2", "2.9.1", "2.11.2")

exportJars := true

mainClass := Some("com.rayrobdod.jsonTilesheetViewer.JSONTilesheetViewer")

libraryDependencies += ("com.rayrobdod" %% "json" % "1.0.0")

// update for new tag handler
libraryDependencies += ("com.rayrobdod" %% "utilities" % "20140518")

libraryDependencies += ("net.sf.opencsv" % "opencsv" % "2.3")

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")

scalacOptions ++= Seq("-unchecked", "-deprecation" )

scalacOptions <++= scalaVersion.map{(sv:String) =>
	if (sv.take(3) == "2.1") {Seq("-feature", "-language:implicitConversions")} else {Nil}
}


// license nonsense
licenses += (("GPLv3 or later", new java.net.URL("http://www.gnu.org/licenses/") ))

mappings in (Compile, packageSrc) <+= baseDirectory.map{(b) => (new File(b, "LICENSE.txt"), "LICENSE.txt" )}

mappings in (Compile, packageBin) <+= baseDirectory.map{(b) => (new File(b, "LICENSE.txt"), "LICENSE.txt" )}


// excludeFilter in unmanagedSources := "jsonTilesheetViewer"

proguardSettings

ProguardKeys.options in Proguard <+= (baseDirectory in Compile).map{"-include '"+_+"/viewer.proguard'"}

ProguardKeys.inputFilter in Proguard := { file =>
	if (file.name.startsWith("board-game-generic")) {
		None
	} else if (file.name == "classes") {
		None
	} else if (file.name.startsWith("rt")) {
		Some("**.class;java.**;javax.**")
	} else {
		Some("**.class")
	}
}

// scalaTest
scalaVersion in Test := "2.9.3"

libraryDependencies += "org.scalatest" % "scalatest_2.9.3" % "1.9.2" % "test"

// testOptions in Test += Tests.Argument("-oS")


// anon-fun-reduce
//autoCompilerPlugins := true
//
//addCompilerPlugin("com.rayrobdod" %% "anon-fun-reduce" % "1.0.0")
//
//libraryDependencies += ("com.rayrobdod" %% "anon-fun-reduce" % "1.0.0")

