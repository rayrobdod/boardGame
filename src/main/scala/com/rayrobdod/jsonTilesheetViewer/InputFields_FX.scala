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

import java.net.{URL, URI}
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import javafx.scene.control.{TextField, Button}
import javafx.event.{EventHandler, ActionEvent}
import scala.util.Random
import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.javafxView._


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
	
	
	def tilesheet:RectangularTilesheet[SpaceClass] = {
		ToggleContentHandlerFactory.setCurrentToTilesheetFx();
		val a = urlOrFileStringToUrl(tilesheetUrlBox.getText).getContent()
		
		import com.rayrobdod.boardGame.swingView
		import com.rayrobdod.boardGame.javafxView
		import javafx.scene.Node
		a match {
			case swingView.NilTilesheet => javafxView.NilTilesheet
			case swingView.IndexesTilesheet => javafxView.IndexesTilesheet
			case swingView.HashcodeColorTilesheet(x) => javafxView.HashcodeColorTilesheet(new javafxView.Dimension(x.width, x.height))
			case b:view.RectangularTilesheet[SpaceClass, Node] => b
		}
	}
	def fieldIsRotationField:Boolean = {
		// not quite sure how to do this without hardcoding anymore
		fieldUrlBox.getText startsWith "tag:rayrobdod.name,2013-08:map-rotate"
	}
	def field:RectangularField[SpaceClass] = {
		ToggleContentHandlerFactory.setCurrentToField();
		urlOrFileStringToUrl(fieldUrlBox.getText).getContent().asInstanceOf[RectangularField[SpaceClass]]
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


