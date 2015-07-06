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
import java.awt.{BorderLayout, GridLayout, GridBagLayout, GridBagConstraints, Component}
import java.awt.event.{ActionListener, ActionEvent, MouseAdapter, MouseEvent}
import javax.swing.{JFrame, JPanel, JTextField, JLabel, JButton, JOptionPane}
import com.rayrobdod.swing.GridBagConstraintsFactory
import scala.util.Random
import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.swingView._


/**
 * @since 3.0.0
 */
final class InputFields(
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
		ToggleContentHandlerFactory.setCurrentToTilesheet();
		urlOrFileStringToUrl(tilesheetUrlBox.getText).getContent().asInstanceOf[RectangularTilesheet[SpaceClass]]
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
	def addOkButtonActionListener(x:ActionListener) {goButton.addActionListener(x)}
	
	
	
	
	
	
	
	
	
	val panel = new JPanel(new GridBagLayout)
	private val tilesheetUrlBox = new JTextField(initialTilesheetUrl)
	private val fieldUrlBox = new JTextField(initialFieldUrl)
	private val randBox = new JTextField(initialRand, 5)
	private val goButton = new JButton("->")
	
	private val label = GridBagConstraintsFactory(insets = new java.awt.Insets(0,5,0,5), fill = GridBagConstraints.BOTH)
	private val endOfLine = GridBagConstraintsFactory(gridwidth = GridBagConstraints.REMAINDER, weightx = 1, fill = GridBagConstraints.BOTH)
	
	panel.add(new JLabel("tilesheet: "), label)
	panel.add(tilesheetUrlBox, endOfLine)
	panel.add(new JLabel("map: "), label)
	panel.add(fieldUrlBox, endOfLine)
	panel.add(new JLabel("seed: "), label)
	panel.add(randBox, endOfLine)
	panel.add(goButton, endOfLine)
}


