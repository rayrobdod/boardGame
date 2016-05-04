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
import javax.swing.{Icon, ImageIcon}
import com.rayrobdod.util.BlitzAnimImage
import com.rayrobdod.animation.{AnimationIcon, ImageFrameAnimation}
import com.rayrobdod.swing.SolidColorIcon

/**
 * 
 */
package object swingView {
	type IndexConverter = Function1[(Int, Int), (Int, Int)]
	type SpaceClassMatcherFactory[-SpaceClass] = view.SpaceClassMatcherFactory[SpaceClass]
	type RectangularTilesheet[A] = view.RectangularTilesheet[A, javax.swing.Icon]
	type VisualizationRuleBasedRectangularTilesheet[A] = view.VisualizationRuleBasedRectangularTilesheet[A, Image, Icon]
	type ParamaterizedRectangularVisualizationRule[A] = view.ParamaterizedRectangularVisualizationRule[A, Image]
	type RectangularVisualziationRuleBuilder[A] = view.RectangularVisualziationRuleBuilder[A, Image]
	
	def VisualizationRuleBasedRectangularTilesheet[A](name:String, visualizationRules:Seq[view.RectangularVisualizationRule[A, Image]]) = {
		view.VisualizationRuleBasedRectangularTilesheet(name, visualizationRules, this.compostLayers _)
	}
	
	val NilTilesheet = new view.NilTilesheet[Icon](BlankIcon)
	def HashcodeColorTilesheet(dim:java.awt.Dimension) = new view.HashcodeColorTilesheet(
		dim.width,
		dim.height,
		BlankIcon,
		{(c:Int, w:Int, h:Int) => new SolidColorIcon(rgbToColor(c), dim.width, dim.height)}
	)
	
	
	
	def lcm(x:Int, y:Int):Int = view.lcm(x,y)
	def gcd(x:Int, y:Int):Int = view.gcd(x,y)
	def rgbToColor(rgb:Int) = new Color(rgb)

	
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
	
	
}

package swingView {
	
	/** A SpaceClassMatcherFactory that always returns a SpaceClassMatcher that always retuns true */
	object ConstTrueSpaceClassMatcherFactory extends SpaceClassMatcherFactory[Any] {
		def apply(s:String):SpaceClassMatcher[Any] = ConstTrueSpaceClassMatcher
	}
	
	/** A SpaceClassMatcherFactory that always returns a SpaceClassMatcher that always retuns false */
	object ConstFalseSpaceClassMatcherFactory extends SpaceClassMatcherFactory[Any] {
		def apply(s:String):SpaceClassMatcher[Any] = ConstFalseSpaceClassMatcher
	}
	
	private[swingView] object BlankIcon extends Icon {
		import java.awt.{Component, Graphics}
		def getIconWidth:Int = 16
		def getIconHeight:Int = 16
		def paintIcon(c:Component, g:Graphics, x:Int, y:Int):Unit = {}
	}
}
