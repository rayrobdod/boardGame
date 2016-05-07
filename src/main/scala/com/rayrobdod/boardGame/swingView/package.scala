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
import scala.util.Random
import java.awt.Dimension
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.{TYPE_INT_RGB => nonAlphaImage, TYPE_INT_ARGB => alphaImage}
import java.net.URL
import javax.swing.{Icon, ImageIcon}
import javax.imageio.ImageIO
import com.rayrobdod.util.BlitzAnimImage
import com.rayrobdod.animation.{AnimationIcon, ImageFrameAnimation}
import com.rayrobdod.swing.SolidColorIcon
import com.rayrobdod.boardGame.view.RectangularTilesheet
import com.rayrobdod.boardGame.view.SpaceClassMatcherFactory

/**
 * 
 */
package object swingView {
	
	def blankIcon(size:Dimension):Icon = new Icon {
		import java.awt.{Component, Graphics}
		def getIconWidth:Int = size.width
		def getIconHeight:Int = size.height
		def paintIcon(c:Component, g:Graphics, x:Int, y:Int):Unit = {}
	}
	def rgbToColor(rgb:Color):Color = rgb
	def rgbToIcon(rgb:Color, size:Dimension):Icon = new SolidColorIcon(rgbToColor(rgb), size.width, size.height)
	def stringIcon(text:String, rgb:Color, size:Dimension):Icon = new Icon {
		import java.awt.{Component, Graphics}
		def getIconWidth:Int = size.width
		def getIconHeight:Int = size.height
		def paintIcon(c:Component, g:Graphics, x:Int, y:Int):Unit = {
			g.setColor(rgbToColor(rgb))
			g.drawString(text, x, y + size.height - 5)
		}
	}
	
	
	def compostLayers(layersWithLCMFrames:Seq[Seq[Image]]):Icon = {
		val a:Seq[Image] = if (! layersWithLCMFrames.isEmpty) {
			// FIXTHIS: assumes all images are the same size
			val imageWidth = layersWithLCMFrames.head.head.getWidth(null)
			val imageHeight = layersWithLCMFrames.head.head.getHeight(null)
			
			// merge all the layers in each frame into one image per frame
			val frames:Seq[java.awt.Image] = layersWithLCMFrames.foldLeft(
				Seq.fill(layersWithLCMFrames.head.size){
					new BufferedImage(imageWidth, imageHeight, alphaImage)
				}
			){(newImage:Seq[BufferedImage], layer:Seq[Image]) =>
				newImage.zip(layer).map({(newImage:BufferedImage, layer:Image) =>
					newImage.getGraphics.drawImage(layer, 0, 0, null)
					newImage
				}.tupled)
			}
			
			frames
		} else {
			Seq(new BufferedImage(1,1,alphaImage))
		}
		
		if (a.length == 1) {
			new ImageIcon(a.head)
		} else {
			new AnimationIcon(new ImageFrameAnimation(a, 1000/5, true))
		}
	}
	
	def sheeturl2images(sheetUrl:URL, tileDimension:Dimension):Seq[Image] = {
		val sheetImage:BufferedImage = ImageIO.read(sheetUrl)
		val tilesX = sheetImage.getWidth / tileDimension.width
		val tilesY = sheetImage.getHeight / tileDimension.height
		
		Seq.empty ++ new BlitzAnimImage(sheetImage, tileDimension.width, tileDimension.height, 0, tilesX * tilesY).getImages
	}
	
	
	def RectangularFieldComponent[A](
			field:RectangularField[A],
			tilesheet:RectangularTilesheet[A, Icon],
			rng:Random = Random
	):(RectangularTilemapComponent, RectangularTilemapComponent) = {
		
		val a:Map[(Int, Int), (Icon, Icon)] = field.map{x => ((x._1, tilesheet.getIconFor(field, x._1._1, x._1._2, rng) )) }
		val top = a.mapValues{_._1}
		val bot = a.mapValues{_._2}
		
		(( new RectangularTilemapComponent(top), new RectangularTilemapComponent(bot) ))
	}
}
