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
package com.rayrobdod.boardGame.swingView

import org.scalatest.FunSpec
import java.awt.{Color}
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import javax.swing.Icon
import scala.collection.immutable.Seq
import com.rayrobdod.boardGame.view.Swing.flattenImageLayers

final class FlattenLayersTest extends FunSpec {
	import FlattenLayersTest._
	
	describe("Swing::flattenImageLayers") {
		it ("empty input results in transparent image") {
			val expected = solidTrans
			val result = flattenImageLayers(Nil)
			assert(compareImages(expected, result))
		}
		it ("single image results in that image") {
			val expected = solidRed
			val result = flattenImageLayers(Seq(solidRed))
			assert(compareImages(expected, result))
		}
		it ("images combine via transparency blending") {
			val expected = topGreenBottomRed
			val result = flattenImageLayers(Seq(solidRed, topGreen))
			assert(compareImages(expected, result))
		}
		it ("images combine via transparency blending (2)") {
			val expected = solidRed
			val result = flattenImageLayers(Seq(topGreen, solidRed))
			assert(compareImages(expected, result))
		}
		
	}
}

object FlattenLayersTest {
	val solidTrans = new BufferedImage(64, 64, TYPE_INT_ARGB)
	val solidRed = new BufferedImage(64, 64, TYPE_INT_ARGB)
	val solidBlue = new BufferedImage(64, 64, TYPE_INT_ARGB)
	val topGreen = new BufferedImage(64, 64, TYPE_INT_ARGB)
	val topGreenBottomRed = new BufferedImage(64, 64, TYPE_INT_ARGB)
	
	solidBlue.createGraphics().setColor(Color.blue)
	solidBlue.createGraphics().fillRect(0,0,64,64)
	
	solidRed.createGraphics().setColor(Color.red)
	solidRed.createGraphics().fillRect(0,0,64,64)
	
	solidRed.createGraphics().setColor(Color.green)
	topGreen.createGraphics().fillRect(0,0,64,32)
	
	solidRed.createGraphics().setColor(Color.red)
	topGreenBottomRed.createGraphics().fillRect(0,0,64,64)
	solidRed.createGraphics().setColor(Color.green)
	topGreenBottomRed.createGraphics().fillRect(0,0,64,32)
	
	
	/** assumes 64 by 64 images */
	def compareImages(b:BufferedImage, a:Icon):Boolean = {
		val a2 = new BufferedImage(64, 64, TYPE_INT_ARGB)
		a.paintIcon(null, a2.createGraphics(), 0, 0)
		
		compareImages(b, a2)
	}
	
	/** assumes 64 by 64 images */
	def compareImages(b:BufferedImage, a:BufferedImage):Boolean = {
		(0 until 64).flatMap{x => (0 until 64).map{y =>
			a.getRGB(x,y) == b.getRGB(x,y)
		}}.forall{x => x}
	}
	
}
