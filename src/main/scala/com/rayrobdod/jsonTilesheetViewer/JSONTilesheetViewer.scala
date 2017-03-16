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

import scala.collection.immutable.Seq

import java.awt.{BorderLayout}
import java.awt.event.{ActionListener, ActionEvent, MouseAdapter, MouseEvent}
import javax.swing.{JFrame, JPanel, JTextField, JLabel, JButton, JOptionPane, JScrollPane}

import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.view._
import com.rayrobdod.boardGame.view.Swing._


/**
 * A simple program using boardGame to display the results of applying a
 * rectangular tilesheet to a rectangular map
 * 
 * @version next
 */
object JsonTilesheetViewer {
	def main(args:Array[String]):Unit = {
		
		val frame = new JFrame("JSON Tilesheet Viewer")
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
		
		val inputFields = new InputFields(
				initialTilesheetUrl = if (args.size > 0) args(0) else TAG_SHEET_NIL,
				initialFieldUrl     = if (args.size > 1) args(1) else TAG_MAP_ROTATE,
				initialRand         = if (args.size > 2) args(2) else ""
		)
		val fieldComp = new JPanel(new com.rayrobdod.swing.layouts.LayeredLayout)
		
		inputFields.addOkButtonActionListener(new ActionListener() {
			override def actionPerformed(e:ActionEvent) {
				loadNewTilesheet(frame, fieldComp, inputFields)
			}
		})
		
		frame.getContentPane.add(inputFields.panel, BorderLayout.NORTH)
		frame.getContentPane.add(new JScrollPane(fieldComp))
		
		loadNewTilesheet(frame, fieldComp, inputFields)
		frame.setVisible(true)
	}
	
	def loadNewTilesheet(
			frame:JFrame,
			fieldComp:JPanel,
			inputFields:InputFields
	):Unit = {
		
		if (inputFields.fieldIsRotationField) {
			
			val currentRotationRotation:Seq[SpaceClass] = {
				allClassesInTilesheet(inputFields.tilesheet) :+ ""
			}
			val currentRotationState:RectangularField[SpaceClass] = {
				RectangularField(Seq.fill(14, 12){currentRotationRotation.head})
			}
			
			val a = RectangularFieldComponent(
				currentRotationState,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.removeAll()
			fieldComp.add(a._2)
			fieldComp.add(a._1)
			
			
			currentRotationState.toSeq.map{_._1}.foreach{index =>
				a._1.addMouseListener(index, new FieldRotationMouseListener(
						index, currentRotationRotation, currentRotationState, fieldComp, inputFields
				))
			}
			
		} else {
			val a = RectangularFieldComponent(
				inputFields.field,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.removeAll()
			fieldComp.add(a._2)
			fieldComp.add(a._1)
		}
		
		frame.pack()
	}
	
	
	
	def allClassesInTilesheet(f:RectangularTilesheet[SpaceClass, _]):Seq[SpaceClass] = {
		import com.rayrobdod.boardGame.SpaceClassMatcher
		import com.rayrobdod.boardGame.view.ParamaterizedRectangularVisualizationRule
		import com.rayrobdod.boardGame.view.VisualizationRuleBasedRectangularTilesheet
		import com.rayrobdod.boardGame.view.HashcodeColorTilesheet
		import StringSpaceClassMatcherFactory.EqualsMatcher
		
		val a = f match {
			case x:VisualizationRuleBasedRectangularTilesheet[SpaceClass, _, _] => {
				val a:Seq[ParamaterizedRectangularVisualizationRule[SpaceClass, _]] = x.visualizationRules.map{_.asInstanceOf[ParamaterizedRectangularVisualizationRule[SpaceClass, _]]}
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
			// designed to be one of each color // green, blue, red, white
			//case x:HashcodeColorTilesheet[SpaceClass] => Seq("AWv", "Ahf", "\u43c8\u0473\u044b", "")
			case x:HashcodeColorTilesheet[_] => Seq("a", "b", "c", "d")
			case _ => Seq("")
		}
		
		a
	}
	
	
	final class FieldRotationMouseListener(
			index:(Int,Int),
			currentRotationRotation:Seq[SpaceClass],
			currentRotationState:RectangularField[SpaceClass],
			fieldComp:JPanel,
			inputFields:InputFields
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
			
			val a = RectangularFieldComponent(
				nextRotationState,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.removeAll()
			fieldComp.add(a._2)
			fieldComp.add(a._1)
			fieldComp.validate()
			
			
			nextRotationState.toSeq.map{_._1}.foreach{index =>
				a._1.addMouseListener(index, new FieldRotationMouseListener(
						index, currentRotationRotation, nextRotationState, fieldComp, inputFields
				))
			}
		}
	}
}
