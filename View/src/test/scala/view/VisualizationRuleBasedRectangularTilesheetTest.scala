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
package com.rayrobdod.boardGame.view

import org.scalatest.FunSpec
import java.awt.{Color}
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import javax.swing.Icon
import scala.util.Random
import scala.collection.immutable.{Seq, Map}
import com.rayrobdod.boardGame.RectangularField
import com.rayrobdod.boardGame.view.{CoordinateFunctionSpecifierParser => coordinateFunctionParser}
import com.rayrobdod.boardGame.view.Swing.compostLayers

class VisualizationRuleBasedRectangularTilesheetTest extends FunSpec {
	import VisualizationRuleBasedRectangularTilesheetTest._
	
	describe("VisualizationRuleBasedRectangularTilesheet") {
		it ("empty input results in two transparent images") {
			val expected = (solidTrans, solidTrans)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Nil,
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareImages(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
		it ("single image on a negative layer is on the bottom icon") {
			val expected = (solidRed, solidTrans)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Seq(
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(-1 -> Seq(solidRed))
					)
				),
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareImages(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
		it ("single image on a positive layer is on the bottom icon") {
			val expected = (solidTrans, solidRed)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Seq(
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(0 -> Seq(solidRed))
					)
				),
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareImages(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
		it ("single image on a positive layer is on the top icon") {
			val expected = (solidTrans, solidRed)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Seq(
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(0 -> Seq(solidRed))
					)
				),
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareImages(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
		it ("one image each on -1 and 0") {
			val expected = (solidRed, solidRed)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Seq(
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(-1 -> Seq(solidRed), 0 -> Seq(solidRed))
					)
				),
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareImages(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
		it ("lower layers are below higher layers") {
			val expected = (topGreenBottomRed, solidTrans)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Seq(
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(-5 -> Seq(solidRed), -1 -> Seq(topGreen))
					)
				),
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareImages(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
		it ("lower layers are below higher layers (2)") {
			val expected = (solidRed, solidTrans)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Seq(
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(-5 -> Seq(topGreen), -1 -> Seq(solidRed))
					)
				),
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareImages(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
		it ("thing with higher priority is used") {
			val expected = (topGreen, solidTrans)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Seq(
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(-1 -> Seq(solidRed))
					),
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(-1 -> Seq(topGreen)),
						indexEquation = coordinateFunctionParser.parse("x == 0").right.get
					)
				),
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareImages(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
		it ("thing with non-matching parameters is ignored") {
			val expected = (topGreen, solidTrans)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Seq(
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(-1 -> Seq(topGreen))
					),
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(-1 -> Seq(solidBlue)),
						indexEquation = coordinateFunctionParser.parse("x == 1").right.get
					)
				),
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareImages(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
		it ("a single two-frame animation") {
			val expected = (Seq(solidRed, solidBlue), solidTrans)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Seq(
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(-1 -> Seq(solidRed, solidBlue))
					)
				),
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareAnimImage(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
		it ("combining a two-frame and a one-frame animations") {
			val expected = (Seq(solidRed, topGreenBottomRed), solidTrans)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Seq(
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(-5 -> Seq(solidRed), -1 -> Seq(solidTrans, topGreen))
					)
				),
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareAnimImage(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
		it ("combining a two-frame and a two-frame animations") {
			val expected = (Seq(solidBlue, topGreenBottomRed), solidTrans)
			val dut = new VisualizationRuleBasedRectangularTilesheet[String, Image, Icon]("blarg",
				Seq(
					new ParamaterizedRectangularVisualizationRule[String, Image](
						iconParts = Map(-5 -> Seq(solidBlue, solidRed), -1 -> Seq(solidTrans, topGreen))
					)
				),
				compostLayers
			)
			val result = dut.getIconFor(singleRectangularField, (0,0), Random)
			
			assert(compareAnimImage(expected._1, result._1))
			assert(compareImages(expected._2, result._2))
		}
	}
}

object VisualizationRuleBasedRectangularTilesheetTest {
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
	
	val singleRectangularField = RectangularField(Seq(Seq("")))
}
