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
package com.rayrobdod.boardGame
package view

import java.net.URL
import java.awt.{Dimension => AwtDimension, Color => AwtColor}
import scala.collection.immutable.{Seq, Map}
import javafx.scene.Node
import javafx.scene.image.{Image, ImageView, WritableImage}
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 * 
 */
object Javafx extends PackageObjectTemplate[Image, Node] {
	// Guess what the compiler cannot figure out?
	// `package object swingView extends PackageObjectTemplate[Image, Icon]`
	// Even though it is perfectly capable of figuring out
	// `object swingView extends PackageObjectTemplate[Image, Icon]`
	// how is that even possible?
	
	override type RenderableComponentType = javafx.scene.Node
	
	override def blankIcon:Node = new Rectangle(1, 1, Color.TRANSPARENT)
	
	def rgbToColor(rgb:AwtColor):Color = Color.rgb(rgb.getRed, rgb.getGreen, rgb.getBlue)
	
	override def rgbToRectangularIcon(rgb:AwtColor, size:RectangularDimension):Node = new Rectangle(size.width, size.height, rgbToColor(rgb))
	
	override def rgbToPolygonIcon(rgb:AwtColor, shape:java.awt.Polygon):Node = {
		val points = shape.xpoints.take(shape.npoints)
			.zip(shape.ypoints.take(shape.npoints))
			.flatMap{xy => Seq(xy._1, xy._2)}
			.map{_.doubleValue}
		
		val retVal = new javafx.scene.shape.Polygon(points:_*)
		retVal.setFill( rgbToColor(rgb) )
		retVal
	}
	
	override def stringIcon(text:String, rgb:AwtColor, size:RectangularDimension):Node = {
		val a = new javafx.scene.text.Text(text)
		a.setFill(rgbToColor(rgb))
		a
	}
	
	
	override def flattenImageLayers(layers:Seq[Image]):Node = {
		val imageWidth = (1.0 +: layers.map{_.getWidth}).max.intValue
		val imageHeight = (1.0 +: layers.map{_.getHeight}).max.intValue
		val canvas = new Canvas(imageWidth, imageHeight)
		layers.foreach{layer =>
			canvas.getGraphicsContext2D().drawImage(layer, 0, 0)
		}
		canvas
	}
	
	override def sheeturl2images(sheetUrl:URL, tileDimension:AwtDimension):Seq[Image] = {
		val sheetImage:Image = new Image(sheetUrl.toString)
		
		(0 to (sheetImage.getHeight.intValue - tileDimension.height) by tileDimension.height).flatMap{y:Int =>
			(0 to (sheetImage.getWidth.intValue - tileDimension.width) by tileDimension.width).map{x:Int =>
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
	
	
	
	
	override def renderable[Index, Dimension](
			  tiles:Map[Index, Node]
			, dimension:Dimension
	)(implicit
			iconLocation:IconLocation[Index, Dimension]
	):Renderable[Index, RenderableComponentType] = {
		new JavaFxRenderable(tiles, dimension)(iconLocation)
	}
}
