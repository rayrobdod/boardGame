/*
	Deduction Tactics
	Copyright (C) 2012-2013  Raymond Dodge

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.rayrobdod.boardGame.swingView

import scala.collection.immutable.Iterable
import java.util.ServiceConfigurationError;
import com.rayrobdod.util.services.Services.readServices;

/**
 * Like {@link java.util.ServiceLoader}, but for Tilesheets.
 *
 * Is willing to load either files using 'path/to/file' or scala objects 'package.name.object'
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
