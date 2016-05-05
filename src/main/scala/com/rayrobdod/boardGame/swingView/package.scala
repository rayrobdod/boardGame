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
import com.rayrobdod.boardGame.view.SpaceClassMatcherFactory

/**
 * 
 */
package object swingView {
	
	def blankIcon(w:Int, h:Int):Icon = new Icon {
		import java.awt.{Component, Graphics}
		def getIconWidth:Int = w
		def getIconHeight:Int = h
		def paintIcon(c:Component, g:Graphics, x:Int, y:Int):Unit = {}
	}
	def rgbToColor(rgb:Int):Color = new Color(rgb)
	def rgbToIcon(rgb:Int, w:Int, h:Int):Icon = new SolidColorIcon(rgbToColor(rgb), w, h)
	def stringIcon(text:String, rgb:Int, w:Int, h:Int):Icon = new Icon {
		import java.awt.{Component, Graphics}
		def getIconWidth:Int = w
		def getIconHeight:Int = h
		def paintIcon(c:Component, g:Graphics, x:Int, y:Int):Unit = {
			g.setColor(rgbToColor(rgb))
			g.drawString(text, x, y + h - 5)
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
	
	def sheeturl2images(sheetUrl:URL, tileWidth:Int, tileHeight:Int):Seq[Image] = {
		val sheetImage:BufferedImage = ImageIO.read(sheetUrl)
		val tilesX = sheetImage.getWidth / tileWidth
		val tilesY = sheetImage.getHeight / tileHeight
		
		Seq.empty ++ new BlitzAnimImage(sheetImage, tileWidth, tileHeight, 0, tilesX * tilesY).getImages
	}
	
	
}
