name := "Board Game Generic"

version := "1.0"

scalaVersion := "2.9.3"

crossScalaVersions ++= Seq("2.10.0", "2.9.1", "2.11.0-M4")

target := new File("C:/Users/Raymond/AppData/Local/Temp/build/BoardGameGeneric/")

unmanagedSourceDirectories in Compile <+= (sourceDirectory in Compile).apply{(x:File) => 
	new File(x, "/../secondary/scala")
}

libraryDependencies += ("com.rayrobdod" %% "json" % "1.0.0")


