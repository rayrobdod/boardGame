name := "Board Game Generic"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

version := "3.0.0-SNAPSHOT"

scalaVersion := "2.10.5"

crossScalaVersions := Seq("2.10.5", "2.11.6")

exportJars := true

mainClass := Some("com.rayrobdod.jsonTilesheetViewer.JSONTilesheetViewer")

libraryDependencies += ("com.rayrobdod" %% "json" % "2.0-RC3")

libraryDependencies += ("com.rayrobdod" %% "utilities" % "20140518")

libraryDependencies += ("net.sf.opencsv" % "opencsv" % "2.3")

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")

scalacOptions ++= Seq("-unchecked", "-deprecation")


packageOptions in (Compile, packageBin) += {
	val manifest = new java.util.jar.Manifest()
	manifest.getEntries().put("scala/", {
		val attrs = new java.util.jar.Attributes()
		attrs.putValue("Implementation-Title", "Scala")
		attrs.putValue("Implementation-URL", "http://www.scala-lang.org/")
		attrs.putValue("Implementation-Version", scalaVersion.value)
		attrs
	})
	manifest.getEntries().put("au/com/bytecode/opencsv/", {
		val attrs = new java.util.jar.Attributes()
		attrs.putValue("Implementation-Title", "opencsv")
		attrs.putValue("Implementation-URL", "http://opencsv.sourceforge.net/")
		attrs.putValue("Implementation-Version", "2.3")
		attrs
	})
	Package.JarManifest( manifest )
}


// license nonsense
licenses += (("GPLv3 or later", new URL("http://www.gnu.org/licenses/") ))

mappings in (Compile, packageSrc) <+= baseDirectory.map{(b) => (new File(b, "LICENSE.txt"), "LICENSE.txt" )}

mappings in (Compile, packageBin) <+= baseDirectory.map{(b) => (new File(b, "LICENSE.txt"), "LICENSE.txt" )}



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
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

testOptions in Test += Tests.Argument("-oS")


scalastyleConfig := baseDirectory.value / "project" / "scalastyle-config.xml"

