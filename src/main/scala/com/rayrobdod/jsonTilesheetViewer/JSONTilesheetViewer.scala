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

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Path, Paths, Files}

import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.swingView._


/**
 * @version 3.0.0
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
	
	val inputFields = new InputFields(
			initialTilesheetUrl = if (args.size > 0) args(0) else "tag:rayrobdod.name,2013-08:tilesheet-nil",
			initialFieldUrl     = if (args.size > 1) args(1) else "tag:rayrobdod.name,2013-08:map-rotate",
			initialRand         = if (args.size > 2) args(2) else ""
	)
	
	inputFields.addOkButtonActionListener(new ActionListener() {
		override def actionPerformed(e:ActionEvent) {
			loadNewTilesheet()
		}
	})
	
	val fieldComp = new LayeredComponent 
	
	frame.getContentPane.add(inputFields.panel, BorderLayout.NORTH)
	frame.getContentPane.add(fieldComp)
	
	
	
	loadNewTilesheet()
	frame.setVisible(true)
	
	def loadNewTilesheet():Unit = {
		
		if (inputFields.fieldIsRotationField) {
			
			val currentRotationRotation:Seq[SpaceClass] = {
				allClassesInTilesheet(inputFields.tilesheet) :+ ""
			}
			val currentRotationState:RectangularField[SpaceClass] = {
				RectangularField(Seq.fill(14, 12){currentRotationRotation.head})
			}
			
			val a = RectangularFieldLayer(
				currentRotationState,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.removeAllLayers()
			fieldComp.addLayer(a._1)
			fieldComp.addLayer(a._2)
			
			
			currentRotationState.toSeq.map{_._1}.foreach{index =>
				a._1.addMouseListener(index, new FieldRotationMouseListener(
						index, currentRotationRotation, currentRotationState
				))
			}
			
		} else {
			val a = RectangularFieldLayer(
				inputFields.field,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.removeAllLayers()
			fieldComp.addLayer(a._1)
			fieldComp.addLayer(a._2)
		}
		
		frame.pack()
	}
	
	
	
	
	
	
	
	
	
	
	
	def allClassesInTilesheet(f:RectangularTilesheet[SpaceClass]):Seq[SpaceClass] = {
		import com.rayrobdod.boardGame.SpaceClassMatcher
		import com.rayrobdod.boardGame.swingView.JSONRectangularTilesheet
		import com.rayrobdod.boardGame.swingView.JSONRectangularVisualizationRule
		import StringSpaceClassMatcher.EqualsMatcher
		
		val a = f match {
			case x:JSONRectangularTilesheet[SpaceClass] => {
				val a:Seq[JSONRectangularVisualizationRule[SpaceClass]] = x.visualizationRules
				val b:Seq[Map[_, SpaceClassMatcher[SpaceClass]]] = a.map{_.surroundingTiles}
				val c:Seq[Seq[SpaceClassMatcher[SpaceClass]]] = b.map{(a) => (Seq.empty ++ a.toSeq).map{_._2}}
				val d:Seq[SpaceClassMatcher[SpaceClass]] = c.flatten
				
				val e:Seq[Option[SpaceClass]] = d.map{_ match {
					case EqualsMatcher(ref) => Option(ref)
					case _ => None
				}}
				val f:Seq[SpaceClass] = e.flatten.distinct
				
				f
				}
			case _ => Seq("")
		}
		
		a
	}
	
	
	final class FieldRotationMouseListener(
			index:(Int,Int),
			currentRotationRotation:Seq[SpaceClass],
			currentRotationState:RectangularField[SpaceClass]
	) extends MouseAdapter {
		override def mouseClicked(e:MouseEvent):Unit = {
			
			val currentSpace:SpaceClass = currentRotationState(index).typeOfSpace
			val currentSpaceIndex:Int = currentRotationRotation.indexOf(currentSpace)
			val nextSpaceIndex:Int = (currentSpaceIndex + 1) % currentRotationRotation.size
			val nextSpace:SpaceClass = currentRotationRotation(nextSpaceIndex)
			
			val nextSpaceClasses:Map[(Int, Int), SpaceClass] =
					currentRotationState.map{x => ((x._1, x._2.typeOfSpace))} +
							((index, nextSpace))
			
			val nextRotationState:RectangularField[SpaceClass] = RectangularField(nextSpaceClasses)
			
			val a = RectangularFieldLayer(
				nextRotationState,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.removeAllLayers()
			fieldComp.addLayer(a._1)
			fieldComp.addLayer(a._2)
			
			
			nextRotationState.toSeq.map{_._1}.foreach{index =>
				a._1.addMouseListener(index, new FieldRotationMouseListener(
						index, currentRotationRotation, nextRotationState
				))
			}
		}
	}
}
