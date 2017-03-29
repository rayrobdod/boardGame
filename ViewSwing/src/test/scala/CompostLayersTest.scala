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
import com.rayrobdod.boardGame.view.Swing.compostLayers

class CompostLayersTest extends FunSpec {
	import CompostLayersTest._
	
	describe("Swing::compostLayers") {
		it ("empty input results in transparent image") {
			val expected = (solidTrans)
			val result = compostLayers(Nil)
			assert(compareImages(expected, result))
		}
		it ("single image results in that image") {
			val expected = (solidRed)
			val result = compostLayers(Seq(Seq(solidRed)))
			assert(compareImages(expected, result))
		}
		it ("images combine via transparency blending") {
			val expected = (topGreenBottomRed)
			val result = compostLayers(Seq(Seq(solidRed), Seq(topGreen)))
			assert(compareImages(expected, result))
		}
		it ("images combine via transparency blending (2)") {
			val expected = (solidRed)
			val result = compostLayers(Seq(Seq(topGreen), Seq(solidRed)))
			assert(compareImages(expected, result))
		}
		it ("something-something animation") {
			val expected = Seq(solidRed, solidBlue)
			val result = compostLayers(Seq(Seq(solidRed, solidBlue)))
			assert(compareAnimImage(expected, result))
		}
		/* 
		it ("combining a two-frame and a one-frame animations") {
			val expected = Seq(solidRed, topGreenBottomRed)
			val result = compostLayers(Seq(Seq(solidRed), Seq(solidTrans, topGreen)))
			assert(compareAnimImage(expected, result))
		}
		*/
		it ("combining a two-frame and a two-frame animations") {
			val expected = Seq(solidBlue, topGreenBottomRed)
			val result = compostLayers(Seq(Seq(solidBlue, solidRed), Seq(solidTrans, topGreen)))
			assert(compareAnimImage(expected, result))
		}
		
	}
}

object CompostLayersTest {
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
	
	/** assumes 64 by 64 images */
	def compareAnimImage(b:Seq[BufferedImage], a:Icon):Boolean = {
		import com.rayrobdod.animation._
		val a2 = a.asInstanceOf[AnimationIcon].animation.asInstanceOf[ImageFrameAnimation]
		val a3 = b.map{x => 
			val retVal = new BufferedImage(64, 64, TYPE_INT_ARGB)
			a2.paintCurrentFrame(null, retVal.createGraphics(), 0, 0)
			a2.incrementFrame()
			retVal
		}
		a3.zip(b).map{x => compareImages(x._1, x._2)}.forall{x => x}
	}
	
}
