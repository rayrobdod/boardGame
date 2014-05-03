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
import java.awt.{BorderLayout, GridLayout, GridBagLayout, GridBagConstraints, Component}
import java.awt.event.{ActionListener, ActionEvent, MouseAdapter, MouseEvent}
import javax.swing.{JFrame, JPanel, JTextField, JLabel, JButton, JOptionPane}
import com.rayrobdod.swing.GridBagConstraintsFactory

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Path, Paths, Files}

import com.rayrobdod.boardGame.swingView.{
		RectangularFieldComponent,
		JSONRectangularTilesheet,
		RectangularTilesheet
}
import com.rayrobdod.boardGame.{
		SpaceClassConstructor, RectangularField, RectangularSpace
}
import com.rayrobdod.javaScriptObjectNotation.parser.JSONParser
import com.rayrobdod.javaScriptObjectNotation.parser.listeners.ToScalaCollection


/**
 * @author Raymond Dodge
 * @version 2.1.0
 * @todo I'd love to be able to add an ability to seed the RNG, but the tilesheets are apparently too nondeterministic.
 */
object JSONTilesheetViewer extends App
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
	
	
	val frame = new JFrame("JSON Tilesheet Viewer")
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
	
	val tileUrl:String = if (args.size > 0) args(0) else "tag:rayrobdod.name,2013-08:tilesheet-nil"
	val mapUrl:String  = if (args.size > 1) args(1) else "tag:rayrobdod.name,2013-08:map-rotate"
	val rand:String    = if (args.size > 2) args(2) else ""
	
	val tileUrlBox = new JTextField(tileUrl)
	val mapUrlBox = new JTextField(mapUrl)
	val randBox = new JTextField(rand,5)
	val goButton = new JButton("Go")
	goButton.addActionListener(new ActionListener() {
		override def actionPerformed(e:ActionEvent) {
			loadNewTilesheet()
		}
	})
	
	val label = GridBagConstraintsFactory(insets = new java.awt.Insets(0,5,0,5))
	val endOfLine = GridBagConstraintsFactory(gridwidth = GridBagConstraints.REMAINDER, weightx = 1, fill = GridBagConstraints.BOTH)
	
	val navPanel = new JPanel(new GridBagLayout)
	navPanel.add(new JLabel("tilesheet: "), label)
	navPanel.add(tileUrlBox, endOfLine)
	navPanel.add(new JLabel("map: "), label)
	navPanel.add(mapUrlBox, endOfLine)
	navPanel.add(new JLabel("seed: "), label)
	navPanel.add(randBox, endOfLine)
	navPanel.add(goButton, endOfLine)
	
	var tilesheet:RectangularTilesheet = null
	var field:RectangularField = null
	var fieldComp:RectangularFieldComponent = null
	
	loadNewTilesheet()
	frame.setVisible(true)
	
	def loadNewTilesheet() = {
		val tilesheetURL = try {
			new URL(tileUrlBox.getText)
		} catch {
			case e:java.net.MalformedURLException =>
						new File(tileUrlBox.getText).toURI.toURL
		}
		val mapURL = try {
			new URL(mapUrlBox.getText)
		} catch {
			case e:java.net.MalformedURLException =>
						new File(mapUrlBox.getText).toURI.toURL
		}
		
		ToggleContentHandlerFactory.setCurrentToTilesheet();
		tilesheet = tilesheetURL.getContent().asInstanceOf[RectangularTilesheet]
		
		ToggleContentHandlerFactory.setCurrentToField();
		tags.RotateMapTagResource.rotation = rotation(tilesheet, tilesheetURL.toURI)
		field = mapURL.getContent().asInstanceOf[RectangularField]
		
		fieldComp = new RectangularFieldComponent(tilesheet, field, getRandomIndicatedByTextBox());
		
		field match {
			case x:RotateSpaceRectangularField => {
				x.spaces.flatten.zipWithIndex.foreach({(space:RectangularSpace, index:Int) =>
					fieldComp.addMouseListenerToSpace(space, new RotateListener(index))
				}.tupled)
			}
			case _ => {}
		}
		
		frame.getContentPane.removeAll()
		
		val fieldCompPane = new JPanel()
		fieldCompPane.add(fieldComp)
		
		frame.getContentPane.add(navPanel, BorderLayout.NORTH)
		frame.getContentPane.add(fieldComp)
		frame.pack()
	}
	
	class RotateListener(index:Int) extends MouseAdapter
	{
		override def mouseClicked(e:MouseEvent) =
		{
			field match {
				case x:RotateSpaceRectangularField => {
				
					field = x.rotate(index)
					
					frame.getContentPane.remove(fieldComp)
					
					fieldComp = new RectangularFieldComponent(tilesheet, field, getRandomIndicatedByTextBox())
					field.spaces.flatten.zipWithIndex.foreach({(space:RectangularSpace, index:Int) =>
						fieldComp.addMouseListenerToSpace(space, new RotateListener(index))
					}.tupled)
					frame.getContentPane.add(fieldComp)
					frame.getContentPane.validate()
				}
			}
		}
	}
	
	
	
	
	
	
	
	def rotation(tilesheet:RectangularTilesheet, tilesheetURI:URI):Seq[SpaceClassConstructor] = {
		Seq.empty ++ (tilesheet match {
			case x:JSONRectangularTilesheet => {
				val jsonMap = {
					val reader = Files.newBufferedReader(Paths.get(tilesheetURI), UTF_8)
					
					val listener = ToScalaCollection()
					JSONParser.parse(listener, reader)
					reader.close()
					
					listener.resultMap
				}
				
				
				val classesKey = if (jsonMap.contains("TilesheetViewer::classes"))
					{"TilesheetViewer::classes"} else {"classMap"}
				
				val classesURL = new URL(tilesheetURI.toURL, jsonMap(classesKey).toString)
				val classesReader = Files.newBufferedReader(Paths.get(classesURL.toURI), UTF_8)
				
				val classNames = {
					val listener = ToScalaCollection()
					JSONParser.parse(listener, classesReader)
					classesReader.close()
					val result = listener.resultSeq
					
					result.map{_ match {
						case x:Tuple2[_,_] => x._2.toString
						case x:Any => x.toString
					}}
				}
				
				classNames.map{(objectName:String) =>
					val clazz = Class.forName(objectName + "$")
					val field = clazz.getField("MODULE$")
					
					field.get(null).asInstanceOf[SpaceClassConstructor]
				}
			}
		/*	case FieldChessTilesheet => {
				import com.rayrobdod.deductionTactics._
				Seq(PassibleSpaceClass, ImpassibleSpaceClass,
						AttackableOnlySpaceClass, NoStandOnSpaceClass)
			}
		*/	case _ => Seq(AnySpaceClass)
		})
	}
	
	def getRandomIndicatedByTextBox():Random = {
		randBox.getText match {
			case "" => Random
			case "a" => new Random(new java.util.Random(){override def next(bits:Int) = 1})
			case "b" => new Random(new java.util.Random(){override def next(bits:Int) = 0})
			case s => try {
				new Random(s.toLong)
			} catch {
				case e:NumberFormatException => {
					JOptionPane.showMessageDialog(frame,
					"Seed must be '', 'a', 'b' or an integer",
					"Invalid seed",
					JOptionPane.WARNING_MESSAGE
					)
					Random
				}
			}
		}
	}
}
