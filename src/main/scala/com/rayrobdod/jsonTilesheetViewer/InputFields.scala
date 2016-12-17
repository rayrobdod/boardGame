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

import java.net.{URL}
import java.awt.{GridBagLayout, GridBagConstraints}
import java.awt.event.{ActionListener}
import java.nio.charset.StandardCharsets.UTF_8
import javax.swing.{JPanel, JTextField, JLabel, JButton, JComboBox, JFileChooser}
import com.rayrobdod.swing.GridBagConstraintsFactory
import scala.util.Random
import scala.collection.immutable.Seq
import com.rayrobdod.json.parser.JsonParser;
import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.swingView._


/**
 * Holds input fields that accept user input.
 * @since 3.0.0
 * @version next
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
	private def urlOrFileStringToUrl(s:String) = {
		try {
			new URL(s)
		} catch {
			case e:java.net.MalformedURLException =>
						new java.io.File(s).toURI.toURL
		}
	}
	
	
	def tilesheet:RectangularTilesheet[SpaceClass] = {
		tilesheetUrlBox.getSelectedItem.toString match {
			case TAG_SHEET_NIL => NilTilesheet
			case TAG_SHEET_INDEX => IndexesTilesheet
			case TAG_SHEET_RAND => new RandomColorTilesheet
			case TAG_SHEET_HASH => new HashcodeColorTilesheet
			case CheckerboardURIMatcher(sheet) => sheet
			case urlStr => {
				val url = urlOrFileStringToUrl(urlStr)
				val b = new VisualizationRuleBasedRectangularTilesheetBuilder[String](url, StringSpaceClassMatcherFactory)
				var r:java.io.Reader = new java.io.StringReader("{}")
				try {
					r = new java.io.InputStreamReader(url.openStream(), UTF_8);
					return new JsonParser().parse(b, r).fold(
						{c => c.apply},
						{p => throw new java.text.ParseException("Parsed value: " + p.toString, 0)},
						{(msg, idx) => throw new java.text.ParseException(msg, idx)}
					)
				} finally {
					r.close();
				}
			}
		}
	}
	def fieldIsRotationField:Boolean = {
		// not quite sure how to do this without hardcoding anymore
		fieldUrlBox.getSelectedItem.toString startsWith TAG_MAP_ROTATE
	}
	def field:RectangularField[SpaceClass] = {
		import java.io.InputStreamReader
		import com.opencsv.CSVReader
		
		val layoutReader = new InputStreamReader(urlOrFileStringToUrl(fieldUrlBox.getSelectedItem.toString).openStream(), UTF_8)
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
	def addOkButtonActionListener(x:ActionListener) {goButton.addActionListener(x)}
	
	
	
	
	
	
	private[this] val tilesheetFileChooser = new JFileChooser
	tilesheetFileChooser.setAcceptAllFileFilterUsed(true)
	tilesheetFileChooser.setMultiSelectionEnabled(false)
	tilesheetFileChooser.addActionListener(new java.awt.event.ActionListener {
		def actionPerformed(e:java.awt.event.ActionEvent) = {
			tilesheetUrlBox.setSelectedItem( tilesheetFileChooser.getSelectedFile.toURI.toString )
		}
	})
	private[this] val fieldFileChooser = new JFileChooser
	fieldFileChooser.setAcceptAllFileFilterUsed(true)
	fieldFileChooser.setMultiSelectionEnabled(false)
	fieldFileChooser.addActionListener(new java.awt.event.ActionListener {
		def actionPerformed(e:java.awt.event.ActionEvent) = {
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
	
	private val randBox = new JTextField(initialRand, 5)
	private val goButton = new JButton("->")
	
	private val label = GridBagConstraintsFactory(insets = new java.awt.Insets(0,5,0,5), fill = GridBagConstraints.BOTH)
	private val midUrlBox = GridBagConstraintsFactory(fill = GridBagConstraints.BOTH)
	private val endOfLine = GridBagConstraintsFactory(gridwidth = GridBagConstraints.REMAINDER, weightx = 1, fill = GridBagConstraints.BOTH)
	
	panel.add(new JLabel("tilesheet: "), label)
	panel.add(tilesheetUrlBox, midUrlBox)
	panel.add(tilesheetFileButton, endOfLine)
	panel.add(new JLabel("map: "), label)
	panel.add(fieldUrlBox, midUrlBox)
	panel.add(fieldFileButton, endOfLine)
	panel.add(new JLabel("seed: "), label)
	panel.add(randBox, endOfLine)
	panel.add(goButton, endOfLine)
}


