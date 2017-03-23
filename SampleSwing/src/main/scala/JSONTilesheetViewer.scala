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
			
			val a = renderable(
				currentRotationState,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.removeAll()
			fieldComp.add(a._2.component)
			fieldComp.add(a._1.component)
			
			
			currentRotationState.foreachIndex{index =>
				a._1.addOnClickHandler(index, new FieldRotationMouseListener(
						index, currentRotationRotation, currentRotationState, fieldComp, inputFields
				))
			}
			
		} else {
			val a = renderable(
				inputFields.field,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.removeAll()
			fieldComp.add(a._2.component)
			fieldComp.add(a._1.component)
		}
		
		frame.pack()
	}
	
	
	
	final class FieldRotationMouseListener(
			index:(Int,Int),
			currentRotationRotation:Seq[SpaceClass],
			currentRotationState:RectangularField[SpaceClass],
			fieldComp:JPanel,
			inputFields:InputFields
	) extends Function0[Unit] {
		override def apply():Unit = {
			
			val currentSpace:SpaceClass = currentRotationState.space(index._1, index._2).get.typeOfSpace
			val currentSpaceIndex:Int = currentRotationRotation.indexOf(currentSpace)
			val nextSpaceIndex:Int = (currentSpaceIndex + 1) % currentRotationRotation.size
			val nextSpace:SpaceClass = currentRotationRotation(nextSpaceIndex)
			
			val nextSpaceClasses:Map[(Int, Int), SpaceClass] =
					currentRotationState.mapIndex{x => ((x, currentRotationState.space(x._1, x._2).get.typeOfSpace))}.toMap +
							((index, nextSpace))
			
			val nextRotationState:RectangularField[SpaceClass] = RectangularField(nextSpaceClasses)
			
			val a = renderable(
				nextRotationState,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.removeAll()
			fieldComp.add(a._2.component)
			fieldComp.add(a._1.component)
			fieldComp.validate()
			
			
			nextRotationState.foreachIndex{index =>
				a._1.addOnClickHandler(index, new FieldRotationMouseListener(
						index, currentRotationRotation, nextRotationState, fieldComp, inputFields
				))
			}
		}
	}
}
