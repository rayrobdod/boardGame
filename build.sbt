name := "Board Game Generic"

version := "1.0"

scalaVersion := "2.9.3"

crossScalaVersions ++= Seq("2.10.0", "2.9.1", "2.11.0-M4")

target := new File("C:/Users/Raymond/AppData/Local/Temp/build/BoardGameGeneric/")

libraryDependencies += ("com.rayrobdod" %% "json" % "1.0.0-SNAPSHOT")

libraryDependencies += ("com.rayrobdod" %% "utilities" % "1.0.0-SNAPSHOT")


