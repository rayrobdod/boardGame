name := "Board Game Generic"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

apiURL := Some(url(s"http://doc.rayrobdod.name/boardgame/${version.value}/"))

version := "3.0.0-SNAPSHOT"

scalaVersion := "2.10.6"

crossScalaVersions := Seq("2.10.6", "2.11.8") ++
    (if (System.getProperty("scoverage.disable", "") != "true") {Nil} else {Seq("2.12.0-RC1")})

// heavy resource use, including Services
fork := true

// proguard doesn't see the META-INF without this
exportJars := true

mainClass in (Compile, run) := Some("com.rayrobdod.jsonTilesheetViewer.JsonTilesheetViewer")

resolvers += ("rayrobdod" at "http://ivy.rayrobdod.name/")

libraryDependencies += ("com.rayrobdod" %% "json" % "3.0-RC1")

libraryDependencies += ("com.rayrobdod" %% "utilities" % "20160112")

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")

libraryDependencies += ("com.opencsv" % "opencsv" % "3.4")

javacOptions in Compile ++= Seq("-Xlint:deprecation", "-Xlint:unchecked", "-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-target:jvm-1.7")

scalacOptions in doc in Compile ++= Seq(
		"-doc-title", name.value,
		"-doc-version", version.value,
		"-doc-root-content", ((scalaSource in Compile).value / "rootdoc.txt").toString,
		"-diagrams",
		"-sourcepath", baseDirectory.value.toString,
		"-doc-source-url", "https://github.com/rayrobdod/boardGame/tree/" + version.value + "€{FILE_PATH}.scala"
)

autoAPIMappings in doc in Compile := true

packageOptions in (Compile, packageBin) += {
	val manifest = new java.util.jar.Manifest()
	manifest.getEntries().put("scala/", {
		val attrs = new java.util.jar.Attributes()
		attrs.putValue("Implementation-Title", "Scala")
		attrs.putValue("Implementation-URL", "http://www.scala-lang.org/")
		attrs.putValue("Implementation-Version", scalaVersion.value)
		attrs
	})
	manifest.getEntries().put("com/opencsv/", {
		val attrs = new java.util.jar.Attributes()
		attrs.putValue("Implementation-Title", "opencsv")
		attrs.putValue("Implementation-URL", "http://opencsv.sourceforge.net/")
		attrs.putValue("Implementation-Version", "3.4")
		attrs
	})
	Package.JarManifest( manifest )
}


if (System.getProperty("scoverage.disable", "") == "true") {
	// provide no-op replacements for disabled tasks
	TaskKey[Unit]("coverage") := {}
} else {
	TaskKey[Unit]("asfdsdfasdf") := {}
}

if (System.getProperty("scoverage.disable", "") == "true") {
	// provide no-op replacements for disabled tasks
	TaskKey[Unit]("coveralls") := {}
} else {
	TaskKey[Unit]("asfdsdfasdf") := {}
}

if (System.getProperty("scoverage.disable", "") == "true") {
	// provide no-op replacements for disabled tasks
	TaskKey[Unit]("coverageReport") := {}
} else {
	TaskKey[Unit]("asfdsdfasdf") := {}
}



// license nonsense
licenses += (("GPLv3 or later", new URL("http://www.gnu.org/licenses/") ))

mappings in (Compile, packageSrc) <+= baseDirectory.map{(b) => (new File(b, "LICENSE.txt"), "LICENSE.txt" )}

mappings in (Compile, packageBin) <+= baseDirectory.map{(b) => (new File(b, "LICENSE.txt"), "LICENSE.txt" )}



proguardSettings

ProguardKeys.proguardVersion in Proguard := "5.2.1"

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

ProguardKeys.libraries in Proguard := {
	(ProguardKeys.libraries in Proguard).value.filter{x => x.toString.endsWith(".jar")}
}

// scalaTest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"

jfxSettings

JFX.addJfxrtToClasspath := true


testOptions in Test += Tests.Argument("-oS", "-u", s"${crossTarget.value}/test-results-junit")


scalastyleConfig := baseDirectory.value / "project" / "scalastyle-config.xml"
