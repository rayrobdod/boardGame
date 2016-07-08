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
package com.rayrobdod.jsonTilesheetViewer

import java.awt.{Dimension, Color}
import java.net.{URL, URI}
import java.nio.charset.StandardCharsets.UTF_8
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import javafx.scene.control.{TextField, Button}
import javafx.event.{EventHandler, ActionEvent}
import scala.util.Random
import scala.collection.immutable.Seq
import com.rayrobdod.json.parser.JsonParser
import com.rayrobdod.json.union.StringOrInt
import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.view._
import com.rayrobdod.boardGame.view.Javafx._


/**
 * @since 3.0.0
 */
final class InputFields2(
		initialTilesheetUrl:String,
		initialFieldUrl:String,
		initialRand:String
) {
	private def urlOrFileStringToUrl(s:String) = {
		try {
			new URL(s)
		} catch {
			case e:java.net.MalformedURLException =>
						new java.io.File(s).toURI.toURL
		}
	}
	
	
	def tilesheet:RectangularTilesheet[SpaceClass, javafx.scene.Node] = tilesheetUrlBox.getText match {
		case TAG_SHEET_NIL => new NilTilesheet(Javafx.blankIcon(new Dimension(16,16)))
		case TAG_SHEET_INDEX => new IndexesTilesheet(Javafx.rgbToIcon(Color.cyan, new Dimension(32, 32)), Javafx.rgbToIcon(Color.magenta, new Dimension(32, 32)), {s:String => Javafx.stringIcon(s, Color.black, new Dimension(32, 32))})
		case TAG_SHEET_RAND => new RandomColorTilesheet(Javafx.rgbToIcon, Javafx.stringIcon, new Dimension(32, 32))
		case TAG_SHEET_HASH => new HashcodeColorTilesheet(Javafx.blankIcon(new Dimension(24, 24)), {c => Javafx.rgbToIcon(c, new Dimension(24, 24))})
		case x => {
			val url = urlOrFileStringToUrl(x)
			val b = new VisualizationRuleBasedRectangularTilesheetBuilder(url, StringSpaceClassMatcherFactory, Javafx.compostLayers, Javafx.sheeturl2images).mapKey(StringOrInt.unwrapToString)
			var r:java.io.Reader = new java.io.StringReader("{}");
			try {
				r = new java.io.InputStreamReader(url.openStream(), UTF_8);
				return new JsonParser().parse(b, r).fold({x => x}, {x => throw new java.text.ParseException("Parsed to primitive", 0)}, {(s,i) => throw new java.text.ParseException("", 0)}).apply()
			} finally {
				r.close();
			}
		}
	}
	def fieldIsRotationField:Boolean = {
		fieldUrlBox.getText startsWith TAG_MAP_ROTATE
	}
	def field:RectangularField[SpaceClass] = {
		import java.io.InputStreamReader
		import com.opencsv.CSVReader
		
		val layoutReader = new InputStreamReader(urlOrFileStringToUrl(fieldUrlBox.getText).openStream(), UTF_8)
		val layoutTable:Seq[Seq[String]] = {
			import scala.collection.JavaConversions.collectionAsScalaIterable;
			
			val reader = new CSVReader(layoutReader);
			val letterTable3 = reader.readAll();
			val letterTable = Seq.empty ++ letterTable3.map{Seq.empty ++ _}
			
			letterTable
		}
		
		RectangularField( layoutTable )
	}
	def rng:Random = randBox.getText match {
		case "" => Random
		case "a" => new Random(new java.util.Random(){override def next(bits:Int):Int = 1})
		case "b" => new Random(new java.util.Random(){override def next(bits:Int):Int = 0})
		case s => try {
			new Random(s.toLong)
		} catch {
			case e:NumberFormatException => {
				throw new IllegalStateException(
						"Seed must be '', 'a', 'b' or an integer",
						e
				)
			}
		}
	}
	def addOkButtonActionListener(x:EventHandler[ActionEvent]) {goButton.setOnAction(x)}
	
	
	
	
	
	
	
	
	
	val panel = new GridPane()
	private val tilesheetUrlBox = new TextField(initialTilesheetUrl)
	private val fieldUrlBox = new TextField(initialFieldUrl)
	private val randBox = new TextField(initialRand)
	private val goButton = new Button("->")
	
	goButton.setDefaultButton(true)
	goButton.setMaxWidth(Double.PositiveInfinity)
	goButton.setMaxHeight(Double.PositiveInfinity)
	
	panel.getColumnConstraints().addAll(
		new javafx.scene.layout.ColumnConstraints(),
		{
			val a = new javafx.scene.layout.ColumnConstraints()
			a.setHgrow(javafx.scene.layout.Priority.ALWAYS)
			a
		}
	);
	
	panel.add(new Text("tilesheet: "), 0, 0, 1, 1)
	panel.add(tilesheetUrlBox, 1, 0, 1, 1 )
	panel.add(new Text("map: "), 0, 1, 1, 1)
	panel.add(fieldUrlBox, 1, 1, 1, 1 )
	panel.add(new Text("seed: "), 0, 2, 1, 1)
	panel.add(randBox, 1, 2, 1, 1 )
	panel.add(goButton, 0, 3, 2, 1 )
}


