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
package com.rayrobdod.jsonTilesheetViewer

import scala.collection.immutable.Seq
import scala.util.Random

import java.net.{URL, URI}
import scalafx.stage.Window
import scalafx.scene.Scene
import scalafx.scene.layout.GridPane
import scalafx.scene.layout.StackPane
import scalafx.scene.text.Text
import scalafx.scene.control.TextField
import scalafx.scene.control.Button
import scalafx.beans.property.ObjectProperty
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import com.rayrobdod.jsonTilesheetViewer.tags._

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Path, Paths, Files}

import com.rayrobdod.boardGame.scalafxView.{
		RectangularFieldComponent,
		RectangularTilesheet,
		IndexesTilesheet
}
import com.rayrobdod.boardGame.{
		RectangularField, RectangularSpace
}
import com.rayrobdod.json.parser.JsonParser


/**
 * @author Raymond Dodge
 * @todo I'd love to be able to add an ability to seed the RNG, but the tilesheets are apparently too nondeterministic.
 */
object JSONTilesheetViewer2 extends JFXApp
{
	{
		val prop:String = "java.protocol.handler.pkgs";
		val pkg:String = "com.rayrobdod.tagprotocol";
		
		var value:String = System.getProperty(prop);
		value = if (value == null) {pkg} else {value + "|" + pkg};
		System.setProperty(prop, value);
		
		
		java.net.URLConnection.setContentHandlerFactory(
				ToggleContentHandlerFactory);
	}
	
	val tileUrlBox = new TextField()
	val mapUrlBox = new TextField()
	val randBox = new TextField()
	val goButton = new Button("Go")
	
	
	val tilesheet:RectangularTilesheet[SpaceClass] = IndexesTilesheet
	val field:RectangularField[SpaceClass] = RectangularField(Seq.fill(14, 12){""})
	val fieldCompParts = RectangularFieldComponent[SpaceClass](field, tilesheet)
	val fieldComp = new StackPane()
	fieldComp.getChildren().addAll(fieldCompParts._1, fieldCompParts._2)
	
	stage = new PrimaryStage {
		title = "JSON Tilesheet Viewer"
		scene = new Scene {
			root = new GridPane {
				addRow( 0, new Text("tilesheet: "), tileUrlBox )
				addRow( 1, new Text("map: "),       mapUrlBox )
				addRow( 2, new Text("seed: "),      randBox )
				addRow( 3, goButton )
				addRow( 4, fieldComp )
			}
		}
	}
}
