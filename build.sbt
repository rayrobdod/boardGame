lazy val root = (project in file("."))
	.aggregate(model, view, viewSwing, viewJavaFx, sampleSwing, sampleJavaFx)

lazy val model = (project in file("Model"))
	.settings(
		  commonSettings
		, commonTestSettings
		, Seq(
			name := "tile-model"
		)
	)

lazy val view = (project in file("View"))
	.dependsOn(model)
	.settings(
		  commonSettings
		, commonTestSettings
		, Seq(
			  name := "tile-view-shared"
			, fork := true
			, libraryDependencies ++= Seq(
				  "com.rayrobdod" %% "json" % "4.0-RC1"
				, "com.lihaoyi" %% "fastparse" % "1.0.0"
			)
		)
	)

lazy val viewSwing = (project in file("ViewSwing"))
	.dependsOn(view)
	.settings(
		  commonSettings
		, commonTestSettings
		, Seq(
			  name := "tile-view-swing"
			, fork := true
			, libraryDependencies ++= Seq(
			)
		)
	)

lazy val viewJavaFx = (project in file("ViewJavaFx"))
	.dependsOn(view)
	.settings(
		  commonSettings
		, commonTestSettings
		, Seq(
			  name := "tile-view-javafx"
			, fork := true
		)
	)

lazy val sampleSwing = (project in file("SampleSwing"))
	.dependsOn(viewSwing)
	.settings(
		  commonSettings
		, commonTestSettings
		, Seq(
			  name := "tiles-sample-viewer-swing"
			  // heavy resource use
			, fork := true
			, mainClass in (Compile, run) := Some("com.rayrobdod.jsonTilesheetViewer.JsonTilesheetViewer")
			, libraryDependencies ++= Seq(
				  "com.opencsv" % "opencsv" % "3.4"
			)
		)
	)

lazy val sampleJavaFx = (project in file("SampleJavaFx"))
	.dependsOn(viewJavaFx)
	.dependsOn(sampleSwing)
	.settings(
		commonSettings
		, commonTestSettings
		, Seq(
			  name := "tiles-sample-viewer-fx"
			  // main is javafx; javafx requires forking to run
			, fork := true
			, mainClass in (Compile, run) := Some("com.rayrobdod.jsonTilesheetViewer.JsonTilesheetViewer2")
			, libraryDependencies ++= Seq(
			)
		)
	)

lazy val commonSettings = Seq(
	  version := "4.0-SNAPSHOT"
	, organization := "com.rayrobdod"
	, organizationHomepage := Some(new URL("https://rayrobdod.name/"))
	, apiURL := Some(url(s"https://doc.rayrobdod.name/boardgame/${version.value}/"))
	, scalaVersion := "2.10.7"
	, crossScalaVersions := Seq("2.10.7", "2.11.12" , "2.12.6")
	
	, compileOrder := CompileOrder.JavaThenScala
	, javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")
	, javacOptions in Compile ++= Seq("-Xlint:deprecation", "-Xlint:unchecked", "-source", "1.8", "-target", "1.8")
	, scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
	, scalacOptions ++= (scalaBinaryVersion.value match {
		case "2.10" | "2.11" => Seq("-target:jvm-1.8")
		case _ => Seq("-target:jvm-1.8")
	})
	, scalacOptions ++= (scalaBinaryVersion.value match {
		case "2.10" => Seq.empty
		case _ => Seq("-Ywarn-unused-import", "-Ywarn-unused", "-Xlint:_", "-Xlint:-adapted-args")
	})
	, scalacOptions in doc in Compile ++= Seq(
		"-doc-title", name.value,
		"-doc-version", version.value,
		"-doc-root-content", ((scalaSource in Compile).value / "rootdoc.txt").toString,
		"-diagrams",
		"-implicits",
		"-groups",
		"-sourcepath", baseDirectory.value.toString,
		"-doc-source-url", "https://github.com/rayrobdod/boardGame/tree/" + version.value + "€{FILE_PATH}.scala"
	)
	, autoAPIMappings in doc in Compile := true
	
	, packageOptions in (Compile, packageBin) += {
		val manifest = new java.util.jar.Manifest()
		manifest.getEntries().put("scala/", {
			val attrs = new java.util.jar.Attributes()
			attrs.putValue("Implementation-Title", "Scala")
			attrs.putValue("Implementation-URL", "http://www.scala-lang.org/")
			attrs.putValue("Implementation-Version", scalaVersion.value)
			attrs
		})
		Package.JarManifest( manifest )
	}
	, scalastyleConfig := baseDirectory.value / ".." / "project" / "scalastyle-config.xml"
	
	, licenses += (("GPLv3 or later", new URL("http://www.gnu.org/licenses/") ))
	, mappings in (Compile, packageSrc) += (baseDirectory.value / ".." / "LICENSE.txt", "LICENSE.txt" )
	, mappings in (Compile, packageBin) += (baseDirectory.value / ".." / "LICENSE.txt", "LICENSE.txt" )
)

lazy val commonTestSettings = Seq(
	  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
	, libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
	, testOptions in Test += Tests.Argument("-oS", "-u", s"${crossTarget.value}/test-results-junit")
)

scalaVersion := "2.10.7"
crossScalaVersions := Seq("2.10.7", "2.11.12" , "2.12.6")
