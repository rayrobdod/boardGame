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
import javafx.scene.control.{TextField, Button}
import javafx.beans.property.ObjectProperty
import javafx.application.Application
import javafx.event.{EventHandler, ActionEvent}
import javafx.scene.input.MouseEvent
import com.rayrobdod.jsonTilesheetViewer.tags._

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Path, Paths, Files}

import com.rayrobdod.boardGame.javafxView.{
		RectangularFieldComponent,
		RectangularTilesheet,
		IndexesTilesheet
}
import com.rayrobdod.boardGame.{
		RectangularField, RectangularSpace
}
import com.rayrobdod.json.parser.JsonParser


/**
 * @author Raymond Dodge
 * @todo I'd love to be able to add an ability to seed the RNG, but the tilesheets are apparently too nondeterministic.
 */
final class JSONTilesheetViewer2 extends Application {
	
	override def init():Unit = {
		val prop:String = "java.protocol.handler.pkgs";
		val pkg:String = "com.rayrobdod.tagprotocol";
		
		var value:String = System.getProperty(prop);
		value = if (value == null) {pkg} else {value + "|" + pkg};
		System.setProperty(prop, value);
		
		
		java.net.URLConnection.setContentHandlerFactory(
				ToggleContentHandlerFactory);
	}
	
	override def start(stage:Stage):Unit = {
		val args = this.getParameters().getUnnamed()
		
		val inputFields = new InputFields2(
			initialTilesheetUrl = if (args.size > 0) args.get(0) else "tag:rayrobdod.name,2013-08:tilesheet-nil",
			initialFieldUrl     = if (args.size > 1) args.get(1) else "tag:rayrobdod.name,2013-08:map-rotate",
			initialRand         = if (args.size > 2) args.get(2) else ""
		)
		val fieldComp = new StackPane()
		
		inputFields.addOkButtonActionListener(new EventHandler[ActionEvent]() {
			override def handle(e:ActionEvent) {
				JSONTilesheetViewer2.loadNewTilesheet(inputFields, fieldComp)
			}
		})
		
		
		
		stage.setTitle("JSON Tilesheet Viewer")
		stage.setScene(
			new Scene(
				new BorderPane(
					fieldComp,
					inputFields.panel,
					new Text(),
					new Text(),
					new Text()
				)
			)
		)
		
		JSONTilesheetViewer2.loadNewTilesheet(inputFields, fieldComp)
		stage.show()
	}
	
	
}

object JSONTilesheetViewer2 {
	def main(args:Array[String]):Unit = {
		Application.launch(classOf[JSONTilesheetViewer2])
	}
	
	private def allClassesInTilesheet(f:RectangularTilesheet[SpaceClass]):Seq[SpaceClass] = {
		import com.rayrobdod.boardGame.SpaceClassMatcher
		import com.rayrobdod.boardGame.javafxView.ParamaterizedRectangularVisualizationRule
		import com.rayrobdod.boardGame.javafxView.VisualizationRuleBasedRectangularTilesheet
		import com.rayrobdod.boardGame.javafxView.HashcodeColorTilesheet
		import StringSpaceClassMatcherFactory.EqualsMatcher
		
		val a = f match {
			case x:VisualizationRuleBasedRectangularTilesheet[SpaceClass] => {
				val a:Seq[ParamaterizedRectangularVisualizationRule[SpaceClass]] = x.visualizationRules.map{_.asInstanceOf[ParamaterizedRectangularVisualizationRule[SpaceClass]]}
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
			case x:HashcodeColorTilesheet => Seq("a", "b", "c", "d")
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
			
			fieldComp.getChildren().removeAll()
			fieldComp.getChildren().add(a._1)
			fieldComp.getChildren().add(a._2)
			
			
			currentRotationState.toSeq.map{_._1}.foreach{index =>
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
			
			fieldComp.getChildren().removeAll()
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
			
			fieldComp.getChildren().removeAll()
			fieldComp.getChildren().add(a._1)
			fieldComp.getChildren().add(a._2)
			
			nextRotationState.toSeq.map{_._1}.foreach{index =>
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
