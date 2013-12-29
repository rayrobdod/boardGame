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
package com.rayrobdod.jsonTilesheetViewer;

import scala.collection.immutable.Seq
import java.net.ContentHandler;
import java.net.URLConnection;
import java.io.IOException;
import java.io.StringWriter;
import java.io.InputStreamReader;
import com.rayrobdod.boardGame.{
		SpaceClassConstructor, RectangularField
}
import com.rayrobdod.boardGame.swingView.JSONRectangularTilesheet;

/**
 * A contentHandler that will turn anything into a String
 */
class JsonMapHandler extends ContentHandler {
	
	/**
	 * Reads data from a URLConnection's input stream and puts
	 * that data into a string.
	 * @throws IOException
	 */
	override def getContent(urlc:URLConnection):RectangularField = {
		import java.io.File
		import java.nio.charset.StandardCharsets.UTF_8
		import java.nio.file.{Path, Paths, Files}
		import com.rayrobdod.javaScriptObjectNotation.parser.JSONParser
		import com.rayrobdod.javaScriptObjectNotation.parser.listeners.ToScalaCollection
		import au.com.bytecode.opencsv.CSVReader;

		val mapURI = urlc.getURL.toURI;
		
		val metadataPath = Paths.get(mapURI)
		val metadataReader = Files.newBufferedReader(metadataPath, UTF_8);
		val metadataMap:Map[String,String] = {
			val listener = ToScalaCollection()
			JSONParser.parse(listener, metadataReader)
			listener.resultMap.mapValues{_.toString}
		}

		val letterToSpaceClassConsPath = metadataPath.getParent.resolve(metadataMap("classMap"))
		val letterToSpaceClassConsReader = Files.newBufferedReader(letterToSpaceClassConsPath, UTF_8)
		val letterToSpaceClassConsMap:Map[String,SpaceClassConstructor] = {
			val listener = ToScalaCollection()
			JSONParser.parse(listener, letterToSpaceClassConsReader)
			val letterToClassNameMap = listener.resultMap.mapValues{_.toString}
			
			letterToClassNameMap.mapValues{(objectName:String) => 
				val clazz = Class.forName(objectName + "$")
				val field = clazz.getField("MODULE$")
				
				field.get(null).asInstanceOf[SpaceClassConstructor]
			}
		}
		
		val layoutPath = metadataPath.getParent.resolve(metadataMap("layout"))
		val layoutReader = Files.newBufferedReader(layoutPath, UTF_8)
		val layoutTable:Seq[Seq[SpaceClassConstructor]] = {
			import scala.collection.JavaConversions.collectionAsScalaIterable;
			
			val reader = new CSVReader(layoutReader);
			val letterTable3 = reader.readAll();
			val letterTable = Seq.empty ++ letterTable3.map{Seq.empty ++ _}
			
			letterTable.map{_.map{letterToSpaceClassConsMap}}
		}
		
		RectangularField.applySCC( layoutTable )
	}
	
	/**
	 * if and of the classes are a superclass of JSONRectangularTilesheet,
	 * returns same as getContent(urlc)
	 * Otherwise, returns null.
	 * @throws IOException
	 */
	 override def getContent(urlc:URLConnection, classes:Array[Class[_]]) = {
		
		classes.find{
			_.isAssignableFrom(classOf[RectangularField])
		}.map{(a) =>
			getContent(urlc)
		}.orNull
		
	}
	
	protected def canEquals(other:Any):Boolean = {
		return (other.isInstanceOf[JsonMapHandler]);
	}
	
	override def equals(other:Any):Boolean = {
		return (this.canEquals(other) && other.asInstanceOf[JsonMapHandler].canEquals(this));
	}
}
