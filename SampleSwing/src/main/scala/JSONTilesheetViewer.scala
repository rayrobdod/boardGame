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

import scala.collection.immutable.Seq

import java.awt.{BorderLayout}
import java.awt.event.{ActionListener, ActionEvent}
import javax.swing.{JFrame, JPanel, JScrollPane}

import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.view._
import com.rayrobdod.boardGame.view.Swing._


/**
 * The main class for the FX Sample Viewer
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
			
			val dimProps = inputFields.dimension
			val tilesheet = inputFields.tilesheet(dimProps)
			
			val currentRotationRotation:Seq[SpaceClass] = {
				allClassesInTilesheet(tilesheet) :+ ""
			}
			val currentRotationState:Tiling[SpaceClass, dimProps.templateProps.Index, _] = {
				dimProps.initialRotationField(currentRotationRotation.head)
			}
			
			val a = renderable(
				currentRotationState,
				tilesheet,
				inputFields.rng
			)(
				dimProps.templateProps.iconLocation
			)
			
			fieldComp.removeAll()
			fieldComp.add(a._2.component)
			fieldComp.add(a._1.component)
			
			
			currentRotationState.foreachIndex{index =>
				a._1.addOnClickHandler(index, FieldRotationMouseListener(
						dimProps)(index, currentRotationRotation, currentRotationState, fieldComp, inputFields
				))
			}
			
		} else {
			val dimensionProperties = inputFields.dimension
			
			val a = renderable[String, dimensionProperties.templateProps.Index, dimensionProperties.Dimension](
				inputFields.field(dimensionProperties),
				inputFields.tilesheet(dimensionProperties),
				inputFields.rng
			)(
				dimensionProperties.templateProps.iconLocation
			)
			
			fieldComp.removeAll()
			fieldComp.add(a._2.component)
			fieldComp.add(a._1.component)
		}
		
		frame.pack()
	}
	
	
	def FieldRotationMouseListener(
			dimProps:NameToTilesheetDemensionType[_, javax.swing.Icon]
	)(
			index:dimProps.templateProps.Index,
			currentRotationRotation:Seq[SpaceClass],
			currentRotationState:Tiling[SpaceClass, dimProps.templateProps.Index, _],
			fieldComp:JPanel,
			inputFields:InputFields
	):Function0[Unit] = new Function0[Unit]{
		override def apply():Unit = {
			
			val currentSpace:SpaceClass = currentRotationState.spaceClass(index).get
			val currentSpaceIndex:Int = currentRotationRotation.indexOf(currentSpace)
			val nextSpaceIndex:Int = (currentSpaceIndex + 1) % currentRotationRotation.size
			val nextSpace:SpaceClass = currentRotationRotation(nextSpaceIndex)
			
			val nextSpaceClasses:Map[dimProps.templateProps.Index, SpaceClass] =
					currentRotationState.mapIndex{x => ((x, currentRotationState.spaceClass(x).get))}.toMap +
							((index, nextSpace))
			
			val nextRotationState:Tiling[SpaceClass, dimProps.templateProps.Index, _] = dimProps.arbitraryField(nextSpaceClasses)
			
			val a = renderable(
				nextRotationState,
				inputFields.tilesheet(dimProps),
				inputFields.rng
			)(
				dimProps.templateProps.iconLocation
			)
			
			fieldComp.removeAll()
			fieldComp.add(a._2.component)
			fieldComp.add(a._1.component)
			fieldComp.validate()
			
			
			nextRotationState.foreachIndex{index =>
				a._1.addOnClickHandler(index, FieldRotationMouseListener(
						dimProps)(index, currentRotationRotation, nextRotationState, fieldComp, inputFields
				))
			}
		}
	}
}
