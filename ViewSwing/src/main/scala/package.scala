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
package view

import scala.collection.immutable.Seq
import java.awt.{Dimension => AwtDimension}
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.{TYPE_INT_ARGB => alphaImage}
import java.net.URL
import javax.swing.{Icon, ImageIcon}
import javax.imageio.ImageIO
import com.rayrobdod.animation.{AnimationIcon, ImageFrameAnimation}
import com.rayrobdod.swing.SolidColorIcon

/**
 * 
 */
object Swing extends PackageObjectTemplate[Image, Icon] {
	// Guess what the compiler cannot figure out?
	// `package object swingView extends PackageObjectTemplate[Image, Icon]`
	// Even though it is perfectly capable of figuring out
	// `object swingView extends PackageObjectTemplate[Image, Icon]`
	// how is that even possible?
	
	override type RenderableComponentType = javax.swing.JComponent
	
	override def blankIcon:Icon = new Icon{
		import java.awt.{Component, Graphics}
		def getIconWidth:Int = 1
		def getIconHeight:Int = 1
		def paintIcon(c:Component, g:Graphics, x:Int, y:Int):Unit = {}
	}
	
	def rgbToColor(rgb:Color):Color = rgb
	
	override def rgbToRectangularIcon(rgb:Color, size:RectangularDimension):Icon = new SolidColorIcon(rgbToColor(rgb), size.width, size.height)
	
	override def rgbToHorizontalHexagonalIcon(rgb:Color, size:HorizontalHexagonalDimension):Icon = new Icon {
		import java.awt.{Component, Graphics}
		def getIconWidth:Int = size.width
		def getIconHeight:Int = size.height
		def paintIcon(c:Component, g:Graphics, x:Int, y:Int):Unit = {
			g.setColor(rgbToColor(rgb))
			g.fillPolygon(
				  Array[Int](size.width / 2, size.width, size.width, size.width / 2, 0, 0)
				, Array[Int](0, size.hinset, size.height - size.hinset, size.height, size.height - size.hinset, size.hinset)
				, 6
			)
		}
	}
	
	override def stringIcon(text:String, rgb:Color, size:RectangularDimension):Icon = new Icon {
		import java.awt.{Component, Graphics}
		def getIconWidth:Int = size.width
		def getIconHeight:Int = size.height
		def paintIcon(c:Component, g:Graphics, x:Int, y:Int):Unit = {
			g.setColor(rgbToColor(rgb))
			g.drawString(text, x, y + size.height - 5)
		}
	}
	
	
	override def compostLayers(layersWithLCMFrames:Seq[Seq[Image]]):Icon = {
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
	
	/**
	 * Take an input `image` and split said image into a series of `tileWidth`×`tileHeight` sized subimages
	 */
	override def sheeturl2images(sheetUrl:URL, tileDimension:AwtDimension):Seq[Image] = {
		assert(tileDimension.width > 0)
		assert(tileDimension.height > 0)
		
		val sheetImage:BufferedImage = ImageIO.read(sheetUrl)
		
		assert(sheetImage.getWidth > 0)
		assert(sheetImage.getHeight > 0)
		
		val tilesInImageX = sheetImage.getWidth / tileDimension.width
		val tilesInImageY = sheetImage.getHeight / tileDimension.height
		
		for (
			frameX <- 0 until tilesInImageX;
			frameY <- 0 until tilesInImageY
		) yield {
			sheetImage.getSubimage(frameX * tileDimension.width, frameY * tileDimension.height, tileDimension.width, tileDimension.height);
		}
	}
	
	
	
	override def renderable[Index, Dimension](
			  tiles:Map[Index, Icon]
			, dimension:Dimension
	)(implicit
			iconLocation:IconLocation[Index, Dimension]
	):Renderable[Index, RenderableComponentType] = {
		new SwingRenderable(tiles, dimension)(iconLocation)
	}
}
