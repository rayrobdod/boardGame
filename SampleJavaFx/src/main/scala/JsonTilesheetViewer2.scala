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
import scala.util.Random

import java.net.{URL, URI}
import javafx.stage.Window
import javafx.stage.Stage
import javafx.scene.{Scene, Node}
import javafx.scene.layout.{GridPane, BorderPane, StackPane}
import javafx.scene.text.Text
import javafx.scene.control.{TextField, Button, ScrollPane}
import javafx.beans.property.ObjectProperty
import javafx.application.Application
import javafx.event.{EventHandler, ActionEvent}
import javafx.scene.input.MouseEvent

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Path, Paths, Files}

import com.rayrobdod.boardGame.view._
import com.rayrobdod.boardGame.view.Javafx._
import com.rayrobdod.boardGame.{
		RectangularField, RectangularSpace
}
import com.rayrobdod.json.parser.JsonParser


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
	
	private def allClassesInTilesheet(f:RectangularTilesheet[SpaceClass, _]):Seq[SpaceClass] = {
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
	
	def loadNewTilesheet(inputFields:InputFields2, fieldComp:StackPane):Unit = {
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
			
			// `.retainAll` and `.removeAll` do the opposite of the name when they
			// have no arguments; `.retainAll` removes all elements from the list
			fieldComp.getChildren().retainAll()
			fieldComp.getChildren().add(a._1)
			fieldComp.getChildren().add(a._2)
			
			
			currentRotationState.indexies.foreach{index =>
				import scala.collection.JavaConversions.collectionAsScalaIterable;
				a._2.getChildren().filter{x =>
					GridPane.getColumnIndex(x) == index._1 &&
					GridPane.getRowIndex(x) == index._2
				}.foreach{x => 
					x.setOnMouseClicked(new FieldRotationMouseListener(
						inputFields, fieldComp,
						index, currentRotationRotation, currentRotationState
					))
				}
			}
			
		} else {
			val a = RectangularFieldComponent(
				inputFields.field,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.getChildren().retainAll()
			fieldComp.getChildren().add(a._1)
			fieldComp.getChildren().add(a._2)
		}
	}
	
	final class FieldRotationMouseListener(
			inputFields:InputFields2,
			fieldComp:StackPane,
			index:(Int,Int),
			currentRotationRotation:Seq[SpaceClass],
			currentRotationState:RectangularField[SpaceClass]
	) extends EventHandler[MouseEvent] {
		override def handle(e:MouseEvent):Unit = {
			
			val currentSpace:SpaceClass = currentRotationState.getSpaceAt(index._1, index._2).get.typeOfSpace
			val currentSpaceIndex:Int = currentRotationRotation.indexOf(currentSpace)
			val nextSpaceIndex:Int = (currentSpaceIndex + 1) % currentRotationRotation.size
			val nextSpace:SpaceClass = currentRotationRotation(nextSpaceIndex)
			
			val nextSpaceClasses:Map[(Int, Int), SpaceClass] =
					currentRotationState.mapIndex{x => ((x, currentRotationState.getSpaceAt(x._1, x._2).get.typeOfSpace))}.toMap +
							((index, nextSpace))
			
			val nextRotationState:RectangularField[SpaceClass] = RectangularField(nextSpaceClasses)
			
			val a = RectangularFieldComponent(
				nextRotationState,
				inputFields.tilesheet,
				inputFields.rng
			)
			
			fieldComp.getChildren().retainAll()
			fieldComp.getChildren().add(a._1)
			fieldComp.getChildren().add(a._2)
			
			nextRotationState.indexies.foreach{index =>
				import scala.collection.JavaConversions.collectionAsScalaIterable;
				a._2.getChildren().filter{x =>
					GridPane.getColumnIndex(x) == index._1 &&
					GridPane.getRowIndex(x) == index._2
				}.foreach{x => 
					x.setOnMouseClicked(new FieldRotationMouseListener(
						inputFields, fieldComp,
						index, currentRotationRotation, nextRotationState
					))
				}
			}
		}
	}
}
