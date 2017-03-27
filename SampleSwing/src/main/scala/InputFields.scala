/*
	Deduction Tactics
	Copyright (C) 2012-2017  Raymond Dodge

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

import java.awt.{GridBagLayout, GridBagConstraints}
import java.awt.event.ActionListener
import javax.swing.{JPanel, JTextField, JLabel, JButton, JComboBox, JFileChooser, JRadioButton}
import scala.util.Random
import com.rayrobdod.swing.GridBagConstraintsFactory
import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.view._


/**
 * The Swing Sample's input fields and the conversion from those field's contents to a displayable thing
 * 
 * @constructor
 * @param initialTilesheetUrl the initial value for the tilesheet input field
 * @param initialFieldUrl the initial value for the field input field
 * @param initialRand the initial value for the rng input field
 */
final class InputFields(
		initialTilesheetUrl:String,
		initialFieldUrl:String,
		initialRand:String
) {
	
	def tilesheet[IconPart, Icon](
		  props:NameToTilesheetDemensionType[IconPart, Icon]
	):Tilesheet[SpaceClass, props.templateProps.Index, props.Dimension, Icon] = {
		nameToTilesheet(tilesheetUrlBox.getSelectedItem.toString, props)
	}
	def fieldIsRotationField:Boolean = {
		fieldUrlBox.getSelectedItem.toString startsWith TAG_MAP_ROTATE
	}
	def field(
		  props:NameToTilesheetDemensionType[_, _]
	):Tiling[SpaceClass, props.templateProps.Index, props.SpaceType] = {
		nameToField(fieldUrlBox.getSelectedItem.toString, props)
	}
	def rng:Random = {
		nameToRandom( randBox.getText )
	}
	def dimension:NameToTilesheetDemensionType[java.awt.Image, javax.swing.Icon] = {
		if (horizHexButton.isSelected) {
			new HorizHexNameToTilesheetDemensionType(Swing)
		} else if (elongTriButton.isSelected) {
			new ElongTriNameToTilesheetDemensionType(Swing)
		} else { // assume rectangularButton is selected
			new RectangularNameToTilesheetDemensionType(Swing)
		}
	}
	
	def addOkButtonActionListener(x:ActionListener) {
		goButton.addActionListener(x)
	}
	
	
	
	
	
	
	private[this] val tilesheetFileChooser = new JFileChooser
	tilesheetFileChooser.setAcceptAllFileFilterUsed(true)
	tilesheetFileChooser.setMultiSelectionEnabled(false)
	tilesheetFileChooser.addActionListener(new java.awt.event.ActionListener {
		def actionPerformed(e:java.awt.event.ActionEvent):Unit = {
			tilesheetUrlBox.setSelectedItem( tilesheetFileChooser.getSelectedFile.toURI.toString )
		}
	})
	private[this] val fieldFileChooser = new JFileChooser
	fieldFileChooser.setAcceptAllFileFilterUsed(true)
	fieldFileChooser.setMultiSelectionEnabled(false)
	fieldFileChooser.addActionListener(new java.awt.event.ActionListener {
		def actionPerformed(e:java.awt.event.ActionEvent):Unit = {
			fieldUrlBox.setSelectedItem( fieldFileChooser.getSelectedFile.toURI.toString )
		}
	})
	
	
	
	
	val panel = new JPanel(new GridBagLayout)
	private val tilesheetUrlBox = {
		val a = new JComboBox[String]()
		a.addItem(TAG_SHEET_NIL)
		a.addItem(TAG_SHEET_INDEX)
		a.addItem(TAG_SHEET_RAND)
		a.addItem(TAG_SHEET_HASH)
		a.addItem(TAG_SHEET_CHECKER)
		a.addItem(TAG_SHEET_CHECKER + "?size=32&light=16711680&dark=255")
		a.setEditable(true)
		a.setSelectedItem(initialTilesheetUrl)
		a
	}
	private val fieldUrlBox = {
		val a = new JComboBox[String]()
		a.addItem(TAG_MAP_ROTATE)
		a.setEditable(true)
		a.setSelectedItem(initialFieldUrl)
		a
	}
	private val tilesheetFileButton = {
		val a = new JButton("…")
		a.addActionListener(new java.awt.event.ActionListener {
			def actionPerformed(e:java.awt.event.ActionEvent) = {
				tilesheetFileChooser.showOpenDialog(a)
			}
		})
		a
	}
	private val fieldFileButton = {
		val a = new JButton("…")
		a.addActionListener(new java.awt.event.ActionListener {
			def actionPerformed(e:java.awt.event.ActionEvent) = {
				fieldFileChooser.showOpenDialog(a)
			}
		})
		a
	}
	
	private val orientationButtonGroup = new javax.swing.ButtonGroup
	private val rectangularButton = new JRadioButton("Rectangular")
	private val horizHexButton = new JRadioButton("HorizHex")
	private val elongTriButton = new JRadioButton("ElonTri")
	orientationButtonGroup.add(rectangularButton)
	orientationButtonGroup.add(horizHexButton)
	orientationButtonGroup.add(elongTriButton)
	orientationButtonGroup.setSelected(rectangularButton.getModel, true)
	
	
	private val randBox = new JTextField(initialRand, 5)
	private val goButton = new JButton("->")
	
	private val label = GridBagConstraintsFactory(insets = new java.awt.Insets(0,5,0,5), fill = GridBagConstraints.BOTH)
	private val midUrlBox = GridBagConstraintsFactory(fill = GridBagConstraints.BOTH, weightx = 10)
	private val endOfLine = GridBagConstraintsFactory(gridwidth = GridBagConstraints.REMAINDER, weightx = 1, fill = GridBagConstraints.BOTH)
	
	panel.add(new JLabel("tilesheet: "), label)
	panel.add(tilesheetUrlBox, midUrlBox)
	panel.add(tilesheetFileButton, endOfLine)
	panel.add(new JLabel("map: "), label)
	panel.add(fieldUrlBox, midUrlBox)
	panel.add(fieldFileButton, endOfLine)
	panel.add(new JLabel("seed: "), label)
	panel.add(randBox, endOfLine)
	panel.add({val a = new JPanel
		a.add(rectangularButton)
		a.add(horizHexButton)
		a.add(elongTriButton)
		a
	}, endOfLine)
	panel.add(goButton, endOfLine)
}
