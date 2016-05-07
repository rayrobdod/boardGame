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
package com.rayrobdod.boardGame

import java.net.URL
import java.awt.{Dimension, Color => AwtColor}
import scala.annotation.tailrec
import scala.collection.immutable.{Seq, Map}
import scala.util.Random
import javafx.scene.Node
import javafx.scene.layout.GridPane
import javafx.scene.image.{Image, ImageView, WritableImage}
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import com.rayrobdod.boardGame.view.RectangularTilesheet
import com.rayrobdod.boardGame.view.SpaceClassMatcherFactory

/**
 * 
 */
package object javafxView {
	
	def blankIcon(size:Dimension):Node = new Rectangle(size.width, size.height, Color.TRANSPARENT)
	def rgbToColor(rgb:AwtColor):Color = Color.rgb(rgb.getRed, rgb.getGreen, rgb.getBlue)
	def rgbToIcon(rgb:AwtColor, size:Dimension):Node = new Rectangle(size.width, size.height, rgbToColor(rgb))
	def stringIcon(text:String, rgb:AwtColor, size:Dimension):Node = new javafx.scene.text.Text(text)
	
	
	def compostLayers(layersWithLCMFrames:Seq[Seq[Image]]):Node = {
		
		val a:Seq[Node] = if (! layersWithLCMFrames.isEmpty) {
			// FIXTHIS: assumes all images are the same size
			val imageWidth = layersWithLCMFrames.head.head.getWidth.intValue
			val imageHeight = layersWithLCMFrames.head.head.getHeight.intValue
			
			// merge all the layers in each frame into one image per frame
			val frames:Seq[Node] = layersWithLCMFrames.foldLeft(
				Seq.fill(layersWithLCMFrames.head.size){
					new Canvas(imageWidth, imageHeight)
				}
			){(newImage:Seq[Canvas], layer:Seq[Image]) =>
				newImage.zip(layer).map({(newImage:Canvas, layer:Image) =>
					newImage.getGraphicsContext2D().drawImage(
						layer,
						0, 0
					)
					newImage
				}.tupled)
			}
			
			frames
		} else {
			Seq(new ImageView(new WritableImage(1,1)))
		}
		
		if (a.length == 1) {
			a.head
		} else {
			// TODO: Animation
			a.headOption.getOrElse(new ImageView(new WritableImage(1,1)))
		}
	}
	
	def sheeturl2images(sheetUrl:URL, tileDimension:Dimension):Seq[Image] = {
		val sheetImage:Image = new Image(sheetUrl.toString)
		val tilesX = sheetImage.getWidth.intValue / tileDimension.width
		val tilesY = sheetImage.getHeight.intValue / tileDimension.height
		
		(0 to (sheetImage.getWidth.intValue - tileDimension.width) by tileDimension.width).flatMap{x:Int =>
			(0 to (sheetImage.getHeight.intValue - tileDimension.height) by tileDimension.height).map{y:Int =>
				val retVal = new WritableImage(tileDimension.width, tileDimension.height)
				retVal.getPixelWriter().setPixels(
					0, 0,
					tileDimension.width, tileDimension.height,
					sheetImage.getPixelReader,
					x, y
				)
				retVal
			}
		}
	}
	
	
	
	
	def RectangularFieldComponent[A](
			field:RectangularField[A],
			tilesheet:RectangularTilesheet[A, javafx.scene.Node],
			rng:Random = Random
	):(GridPane, GridPane) = {
		
		val a:Map[(Int, Int), (Node, Node)] = field.map{x => ((x._1, tilesheet.getIconFor(field, x._1._1, x._1._2, rng) )) }
		val top = a.mapValues{_._1}
		val bot = a.mapValues{_._2}
		
		val topComp = new GridPane()
		top.foreach{case ((x,y), node) =>
			topComp.add(node, x, y)
			GridPane.setHgrow(node, javafx.scene.layout.Priority.ALWAYS)
			GridPane.setVgrow(node, javafx.scene.layout.Priority.ALWAYS)
			GridPane.setHalignment(node, javafx.geometry.HPos.CENTER)
			GridPane.setValignment(node, javafx.geometry.VPos.CENTER)
		}
		val botComp = new GridPane()
		bot.foreach{case ((x,y), node) =>
			botComp.add(node, x, y)
			GridPane.setHgrow(node, javafx.scene.layout.Priority.ALWAYS)
			GridPane.setVgrow(node, javafx.scene.layout.Priority.ALWAYS)
			GridPane.setHalignment(node, javafx.geometry.HPos.CENTER)
			GridPane.setValignment(node, javafx.geometry.VPos.CENTER)
		}
		
		((topComp, botComp))
	}
	
}
