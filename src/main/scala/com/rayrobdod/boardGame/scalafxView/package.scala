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
import scala.annotation.tailrec
import scala.collection.immutable.Seq
import javafx.scene.Node
import javafx.scene.image.{Image, ImageView, WritableImage}
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import com.rayrobdod.boardGame.view.SpaceClassMatcherFactory

/**
 * 
 */
package object javafxView {
	type RectangularTilesheet[A] = view.RectangularTilesheet[A, javafx.scene.Node]
	
	def VisualizationRuleBasedRectangularTilesheet[A](name:String, visualizationRules:Seq[view.RectangularVisualizationRule[A, Image]]) = {
		view.VisualizationRuleBasedRectangularTilesheet(name, visualizationRules, this.compostLayers _)
	}
	def VisualizationRuleBasedRectangularTilesheetBuilder[A](base:URL, classMap:SpaceClassMatcherFactory[A]) = {
		new view.VisualizationRuleBasedRectangularTilesheetBuilder(base, classMap, compostLayers, sheeturl2images)
	}
	
	def blankIcon(w:Int, h:Int):Rectangle = new Rectangle(w, h, Color.TRANSPARENT)
	def rgbToColor(rgb:Int) = Color.rgb((rgb >> 16) % 256, (rgb >> 8) % 256, (rgb >> 0) % 256)
	def rgbToIcon(rgb:Int, w:Int, h:Int):Rectangle = new Rectangle(w, h, rgbToColor(rgb))
	
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
	
	private def sheeturl2images(sheetUrl:URL, tileWidth:Int, tileHeight:Int):Seq[Image] = {
		val sheetImage:Image = new Image(sheetUrl.toString)
		val tilesX = sheetImage.getWidth.intValue / tileWidth
		val tilesY = sheetImage.getHeight.intValue / tileHeight
		
		(0 to (sheetImage.getWidth.intValue - tileWidth) by tileWidth).flatMap{x:Int =>
			(0 to (sheetImage.getHeight.intValue - tileHeight) by tileHeight).map{y:Int =>
				val retVal = new WritableImage(tileWidth, tileHeight)
				retVal.getPixelWriter().setPixels(
					0, 0,
					tileWidth, tileHeight,
					sheetImage.getPixelReader,
					x, y
				)
				retVal
			}
		}
	}
	
}

package javafxView {
	final case class Dimension(val width:Int, val height:Int)
	
}
