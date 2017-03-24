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

import javafx.stage.Stage
import javafx.stage.FileChooser.ExtensionFilter
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import javafx.scene.control.{TextField, Button}
import javafx.event.{EventHandler, ActionEvent}
import scala.util.Random
import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.view._


/**
 * @since 3.0.0
 */
final class InputFields2(
		initialTilesheetUrl:String,
		initialFieldUrl:String,
		initialRand:String,
		stage:Stage
) {
	
	def tilesheet(
		  props:NameToTilesheetDemensionType[Image, Node]
	):Tilesheet[SpaceClass, props.templateProps.Index, props.Dimension, Node] = {
		nameToTilesheet(tilesheetUrlBox.getValue, props)
	}
	def fieldIsRotationField:Boolean = {
		fieldUrlBox.getValue startsWith TAG_MAP_ROTATE
	}
	def field(
		  props:NameToTilesheetDemensionType[_, _]
	):Tiling[SpaceClass, props.templateProps.Index, props.SpaceType[SpaceClass]] = {
		nameToField(fieldUrlBox.getValue, props)
	}
	def rng:Random = {
		nameToRandom(randBox.getText)
	}
	def dimension:NameToTilesheetDemensionType[Image, Node] = {
		if (horizHexButton.isSelected) {
			new HorizHexNameToTilesheetDemensionType(Javafx)
		} else { // assume rectangularButton is selected
			new RectangularNameToTilesheetDemensionType(Javafx)
		}
	}
	
	def addOkButtonActionListener(x:EventHandler[ActionEvent]) {
		goButton.setOnAction(x)
	}
	
	
	
	
	
	private[this] val tilesheetFileChooser = new javafx.stage.FileChooser
	tilesheetFileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
	private[this] val fieldFileChooser = new javafx.stage.FileChooser
	fieldFileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));

	
	
	
	
	val panel = new GridPane()
	private val tilesheetUrlBox = {
		val a = new javafx.scene.control.ComboBox[String]()
		a.getItems().add(TAG_SHEET_NIL)
		a.getItems().add(TAG_SHEET_INDEX)
		a.getItems().add(TAG_SHEET_RAND)
		a.getItems().add(TAG_SHEET_HASH)
		a.getItems().add(TAG_SHEET_CHECKER)
		a.getItems().add(TAG_SHEET_CHECKER + "?size=32&light=16711680&dark=255")
		a.setEditable(true)
		a.setValue(initialTilesheetUrl)
		a
	}
	private val fieldUrlBox = {
		val a = new javafx.scene.control.ComboBox[String]()
		a.getItems().add(TAG_MAP_ROTATE)
		a.setEditable(true)
		a.setValue(initialFieldUrl)
		a
	}
	private val tilesheetFileButton = {
		val a = new javafx.scene.control.Button("…")
		a.setOnAction(new javafx.event.EventHandler[javafx.event.ActionEvent]{
			def handle(e:javafx.event.ActionEvent) = {
				val res = tilesheetFileChooser.showOpenDialog(stage)
				if (res != null) {
					tilesheetUrlBox.setValue( res.toURI.toString )
				}
			}
		})
		a
	}
	private val fieldFileButton = {
		val a = new javafx.scene.control.Button("…")
		a.setOnAction(new javafx.event.EventHandler[javafx.event.ActionEvent]{
			def handle(e:javafx.event.ActionEvent) = {
				val res = fieldFileChooser.showOpenDialog(stage)
				if (res != null) {
					fieldUrlBox.setValue( res.toURI.toString )
				}
			}
		})
		a
	}
	
	private val orientationButtonGroup = new javafx.scene.control.ToggleGroup();
	private val rectangularButton = new javafx.scene.control.RadioButton("Rectangular");
	private val horizHexButton = new javafx.scene.control.RadioButton("HorizHex");
	rectangularButton.setToggleGroup(orientationButtonGroup);
	rectangularButton.setSelected(true);
	horizHexButton.setToggleGroup(orientationButtonGroup);
	
	
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
		},
		new javafx.scene.layout.ColumnConstraints()
	);
	
	panel.add(new Text("tilesheet: "), 0, 0, 1, 1)
	panel.add(tilesheetUrlBox, 1, 0, 1, 1 )
	panel.add(tilesheetFileButton, 2, 0, 1, 1)
	panel.add(new Text("map: "), 0, 1, 1, 1)
	panel.add(fieldUrlBox, 1, 1, 1, 1 )
	panel.add(fieldFileButton, 2, 1, 1, 1 )
	panel.add(new Text("seed: "), 0, 2, 1, 1)
	panel.add(randBox, 1, 2, 2, 1 )
	panel.add(new javafx.scene.layout.FlowPane(rectangularButton, horizHexButton), 0, 3, 3, 1 )
	panel.add(goButton, 0, 4, 3, 1 )
}


