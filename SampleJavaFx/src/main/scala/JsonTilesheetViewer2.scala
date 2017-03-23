/*
	Deduction Tactics
	Copyright (C) 2012-2016  Raymond Dodge

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

import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.layout.{BorderPane, StackPane}
import javafx.scene.text.Text
import javafx.scene.control.ScrollPane
import javafx.application.Application
import javafx.event.{EventHandler, ActionEvent}

import com.rayrobdod.boardGame.RectangularField
import com.rayrobdod.boardGame.view._
import com.rayrobdod.boardGame.view.Javafx._


/**
 * @author Raymond Dodge
 * @todo I'd love to be able to add an ability to seed the RNG, but the tilesheets are apparently too nondeterministic.
 */
final class JsonTilesheetViewer2 extends Application {
	
	override def start(stage:Stage):Unit = {
		val args = this.getParameters().getUnnamed()
		
		val inputFields = new InputFields2(
			initialTilesheetUrl = if (args.size > 0) args.get(0) else TAG_SHEET_NIL,
			initialFieldUrl     = if (args.size > 1) args.get(1) else TAG_MAP_ROTATE,
			initialRand         = if (args.size > 2) args.get(2) else "",
			stage = stage
		)
		val fieldComp = new StackPane()
		
		inputFields.addOkButtonActionListener(new EventHandler[ActionEvent]() {
			override def handle(e:ActionEvent) {
				JsonTilesheetViewer2.loadNewTilesheet(inputFields, fieldComp)
			}
		})
		
		
		
		stage.setTitle("JSON Tilesheet Viewer")
		stage.setScene(
			new Scene({
				val a = new BorderPane()
				a.setCenter({
					val b = new ScrollPane()
					b.setContent(fieldComp)
					b
				})
				a.setTop(inputFields.panel)
				a.setLeft(new Text())
				a.setBottom(new Text())
				a.setRight(new Text())
				a
			})
		)
		
		JsonTilesheetViewer2.loadNewTilesheet(inputFields, fieldComp)
		stage.show()
	}
	
	
}

object JsonTilesheetViewer2 {
	def main(args:Array[String]):Unit = {
		Application.launch(classOf[JsonTilesheetViewer2], args:_*)
	}
	
	def loadNewTilesheet(inputFields:InputFields2, fieldComp:StackPane):Unit = {
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
			
			// `.retainAll` and `.removeAll` do the opposite of the name when they
			// have no arguments; `.retainAll` removes all elements from the list
			fieldComp.getChildren().retainAll()
			fieldComp.getChildren().add(a._1.component)
			fieldComp.getChildren().add(a._2.component)
			
			
			currentRotationState.foreachIndex{index =>
				a._1.addOnClickHandler(index, new FieldRotationMouseListener(
						inputFields, fieldComp,
						index, currentRotationRotation, currentRotationState
				))
			}
			
		} else {
			val a = renderable(
				inputFields.field,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.getChildren().retainAll()
			fieldComp.getChildren().add(a._1.component)
			fieldComp.getChildren().add(a._2.component)
		}
	}
	
	final class FieldRotationMouseListener(
			inputFields:InputFields2,
			fieldComp:StackPane,
			index:(Int,Int),
			currentRotationRotation:Seq[SpaceClass],
			currentRotationState:RectangularField[SpaceClass]
	) extends Function0[Unit] {
		override def apply():Unit = {
			
			val currentSpace:SpaceClass = currentRotationState.space(index).get.typeOfSpace
			val currentSpaceIndex:Int = currentRotationRotation.indexOf(currentSpace)
			val nextSpaceIndex:Int = (currentSpaceIndex + 1) % currentRotationRotation.size
			val nextSpace:SpaceClass = currentRotationRotation(nextSpaceIndex)
			
			val nextSpaceClasses:Map[(Int, Int), SpaceClass] =
					currentRotationState.mapIndex{x => ((x, currentRotationState.space(x).get.typeOfSpace))}.toMap +
							((index, nextSpace))
			
			val nextRotationState:RectangularField[SpaceClass] = RectangularField(nextSpaceClasses)
			
			val a = renderable(
				nextRotationState,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.getChildren().retainAll()
			fieldComp.getChildren().add(a._1.component)
			fieldComp.getChildren().add(a._2.component)
			
			nextRotationState.foreachIndex{index =>
				a._1.addOnClickHandler(index, new FieldRotationMouseListener(
						inputFields, fieldComp,
						index, currentRotationRotation, nextRotationState
				))
			}
		}
	}
}
