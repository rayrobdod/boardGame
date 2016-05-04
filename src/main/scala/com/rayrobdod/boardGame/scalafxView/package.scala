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

import scala.annotation.tailrec
import scala.collection.immutable.Seq
import javafx.scene.Node
import javafx.scene.image.{Image, ImageView, WritableImage}
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 * 
 */
package object javafxView {
	type IndexConverter = Function1[(Int, Int), (Int, Int)]
	type SpaceClassMatcherFactory[-SpaceClass] = view.SpaceClassMatcherFactory[SpaceClass]
	type RectangularTilesheet[A] = view.RectangularTilesheet[A, javafx.scene.Node]
	type VisualizationRuleBasedRectangularTilesheet[A] = view.VisualizationRuleBasedRectangularTilesheet[A, Image, Node]
	type ParamaterizedRectangularVisualizationRule[A] = view.ParamaterizedRectangularVisualizationRule[A, Image]
	type RectangularVisualziationRuleBuilder[A] = view.RectangularVisualziationRuleBuilder[A, Image]
	
	def VisualizationRuleBasedRectangularTilesheet[A](name:String, visualizationRules:Seq[view.RectangularVisualizationRule[A, Image]]) = {
		view.VisualizationRuleBasedRectangularTilesheet(name, visualizationRules, this.compostLayers _)
	}
	
	val NilTilesheet = new view.NilTilesheet[Node](
		new Rectangle(16, 16, Color.TRANSPARENT)
	)
	def HashcodeColorTilesheet(dim:Dimension) = new view.HashcodeColorTilesheet(
		dim.width,
		dim.height,
		new Rectangle(16, 16, Color.TRANSPARENT),
		{(c:Int, w:Int, h:Int) => new Rectangle(w, h, rgbToColor(c))}
	)
			
	
	def lcm(x:Int, y:Int):Int = view.lcm(x,y)
	def gcd(x:Int, y:Int):Int = view.gcd(x,y)
	def rgbToColor(rgb:Int) = Color.rgb((rgb >> 16) % 256, (rgb >> 8) % 256, (rgb >> 0) % 256)
	
	
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

}

package javafxView {
	final case class Dimension(val width:Int, val height:Int)
	
	/** A SpaceClassMatcherFactory that always returns a SpaceClassMatcher that always retuns true */
	object ConstTrueSpaceClassMatcherFactory extends SpaceClassMatcherFactory[Any] {
		def apply(s:String):SpaceClassMatcher[Any] = ConstTrueSpaceClassMatcher
	}
	
	/** A SpaceClassMatcherFactory that always returns a SpaceClassMatcher that always retuns false */
	object ConstFalseSpaceClassMatcherFactory extends SpaceClassMatcherFactory[Any] {
		def apply(s:String):SpaceClassMatcher[Any] = ConstFalseSpaceClassMatcher
	}
}
