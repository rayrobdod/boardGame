/*
	Deduction Tactics
	Copyright (C) 2012-2015  Raymond Dodge

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
import java.nio.charset.StandardCharsets.UTF_8
import com.rayrobdod.json.parser.JsonParser
import com.rayrobdod.boardGame.view.SpaceClassMatcherFactory
import com.rayrobdod.boardGame.view.RectangularTilesheet
import com.rayrobdod.boardGame.view.VisualizationRuleBasedRectangularTilesheetBuilder

/**
 * Like {@link java.util.ServiceLoader}, but for Tilesheets.
 *
 * Is willing to load either resources using 'path/to/file' or scala objects 'package.name.object'
 * 
 * @author Raymond Dodge
 * @version 3.0.0
 */
final class RectangularTilesheetLoader[SpaceClass](
		val service:String,
		val matchers:SpaceClassMatcherFactory[SpaceClass],
		val loader:ClassLoader = Thread.currentThread().getContextClassLoader()
) extends Iterable[RectangularTilesheet[SpaceClass, javax.swing.Icon]] {
	
	def iterator:Iterator[RectangularTilesheet[SpaceClass, javax.swing.Icon]] = new MyIterator()
	
	private class MyIterator() extends Iterator[RectangularTilesheet[SpaceClass, javax.swing.Icon]]
	{
		private var current:Int = 0;
		private val readLines = try {
			readServices(service, loader);
		} catch {
			case e:java.io.IOException => throw new ServiceConfigurationError("", e)
			case e:java.net.URISyntaxException => throw new ServiceConfigurationError("", e)
		}
		
		def hasNext:Boolean = current < readLines.length;
		def remove:Nothing = throw new UnsupportedOperationException("Cannot remove from a Service");
		
		
		def next():RectangularTilesheet[SpaceClass, javax.swing.Icon] = {
			if (!hasNext) throw new java.util.NoSuchElementException();
			
			val line = readLines(current);
			try {
				val lineURL = {
					if (loader == null) {
						ClassLoader.getSystemResource(line);
					} else {	
						loader.getResource(line);
					}
				}
				
				current = current + 1;
				if (lineURL != null) {
					// then the line refers to a resource; read from the resource
					val b = new VisualizationRuleBasedRectangularTilesheetBuilder(lineURL, matchers, compostLayers, sheeturl2images)
					var r:java.io.Reader = new java.io.StringReader("{}")
					try {
						r = new java.io.InputStreamReader(lineURL.openStream(), UTF_8)
						new JsonParser(b).parse(r).apply
					} finally {
						r.close()
					}
				} else {
					// then the line refers to a class; find that class
					val clazz = {
						if (loader == null) {
							Class.forName(line + "$");
						} else {	
							loader.loadClass(line + "$");
						}
					}
					val module = clazz.getField("MODULE$")
					import javax.swing.Icon
					module.get(null) match {
						// stupid type erasure...
						case x:RectangularTilesheet[SpaceClass, Icon] => x
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
