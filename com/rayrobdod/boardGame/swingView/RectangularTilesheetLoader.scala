package com.rayrobdod.boardGame.swingView

import scala.collection.immutable.Iterable
import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.collection.JavaConversions.enumerationAsScalaIterator
//import com.rayrobdod.boardGame.view.{Tilesheet, JSONTilesheet}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths, FileSystems,
		FileSystemNotFoundException}
import java.net.URL
import com.rayrobdod.util.services.ResourcesServiceLoader
import java.util.ServiceConfigurationError;
import com.rayrobdod.util.services.Services.readServices;

/**
 * Like {@link java.util.ServiceLoader}, but for Tilesheets.
 * 
 * @author Raymond Dodge
 * @version 15 Apr 2012
 * @version 31 May 2012 - adding support for resources in jar files
 * @version 08 Jun 2012 - Making sure a jar file system can't be created twice
 * @version 2012 Oct 28 - copying from com.rayrobdod.boardGame.view to com.rayrobdod.boardGame.swingview
 *                        and modifying to use appropriate swingView classes.
 * @version 2012 Dec 02 - nuking; replacing implementaiton with implementation backed by [[com.rayrobdod.util.services.ResourcesServiceLoader]]
 * @version 2013 Jan 19 - nuking; giving an interface based on ResourceServerLoader, but which allows classes to be loaded as well
 */
final class RectangularTilesheetLoader(val service:String)
			extends Iterable[RectangularTilesheet]
{
	// IDEA: recognise files ("com/*.json") verses classes ("com.*") and load based on that difference
	
	
	def iterator:Iterator[RectangularTilesheet] = new MyIterator()
	
	private class MyIterator() extends Iterator[RectangularTilesheet]
	{
		private var current:Int = 0;
		private val readLines = try {
			readServices(service);
		} catch {
			case e:java.io.IOException => throw new ServiceConfigurationError("", e)
			case e:java.net.URISyntaxException => throw new ServiceConfigurationError("", e)
		}
		
		def hasNext = current < readLines.length;
		def remove = throw new UnsupportedOperationException("Cannot remove from a Service");
		
		
		def next():RectangularTilesheet = {
			if (!hasNext) throw new java.util.NoSuchElementException();
			
			val line = readLines(current);
			try {
				val lineURL = ClassLoader.getSystemResource(line);
				
				current = current + 1;
				if (lineURL != null) {
					JSONRectangularTilesheet(lineURL)
				} else {
					val clazz = Class.forName(line + "$")
					val module = clazz.getField("MODULE$")
					module.get(null) match {
						case x:RectangularTilesheet => x
						case _ => throw new ServiceConfigurationError(line + " is not a RectangularTilesheet")
					}
				}
			} catch {
				case e:java.net.URISyntaxException => throw new ServiceConfigurationError("Invalid Path", e);
				case e:ClassNotFoundException => throw new ServiceConfigurationError("", e)
			}
			
		}
	}
}
