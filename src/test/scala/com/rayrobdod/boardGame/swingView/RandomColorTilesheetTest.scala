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

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks
import java.awt.{Dimension, Color}
import javax.swing.Icon;
import scala.util.Random
import com.rayrobdod.swing.SolidColorIcon

class RandomColorTilesheetTest extends FunSpec {
	describe ("Default RandomColorTilesheet") {
		it ("dim is (64,24)") {
			assertResult(new Dimension(64,24)){new RandomColorTilesheet().dim}
		}
		it ("name is 'Random Color'") {
			assertResult("Random Color"){new RandomColorTilesheet().name}
		}
		it ("toString is 'Random Color: ...'") {
			assertResult("Random Color, java.awt.Dimension[width=64,height=24]"){new RandomColorTilesheet().toString}
		}
		it ("getIconFor(null, 0, 0, null)._1 is a SolidColorIcon") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0})
			
			compareIcons(new SolidColorIcon(Color.black, 64,24)){new RandomColorTilesheet().getIconFor(null, -1, -1, rng)._1}
		}
		it ("getIconFor(null, 0, 0, null)._2 is ColorStringIcon") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0})
			val tilesheet = new RandomColorTilesheet()
			
			assertResult(new RandomColorTilesheet.ColorStringIcon(Color.black, new Dimension(64,24))){tilesheet.getIconFor(null, 0, 0, rng)._2}
		}
	}
	describe ("RandomColorTilesheet(13,21)") {
		val dut = new RandomColorTilesheet(new Dimension(13,21))
		
		it ("dim is (13,21)") {
			assertResult(new Dimension(13,21)){dut.dim}
		}
		it ("name is 'Random Color'") {
			assertResult("Random Color"){dut.name}
		}
		it ("toString is 'Random Color: ...'") {
			assertResult("Random Color, java.awt.Dimension[width=13,height=21]"){dut.toString}
		}
		it ("getIconFor(null, 0, 0, null)._1 is a SolidColorIcon") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0})
			
			compareIcons(new SolidColorIcon(Color.black, 13,21)){dut.getIconFor(null, -1, -1, rng)._1}
		}
		it ("getIconFor(null, 0, 0, null)._2 is ColorStringIcon") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0})
			
			assertResult(new RandomColorTilesheet.ColorStringIcon(Color.black, new Dimension(13,21))){dut.getIconFor(null, 0, 0, rng)._2}
		}
	}
	describe ("RandomColorTilesheet.ColorStringIcon") {
		val icon = new RandomColorTilesheet.ColorStringIcon(Color.red, new Dimension(13,21))
		
		it ("has a width equal to tilesheet's icon width") {
			assertResult(13){icon.getIconWidth}
		}
		it ("has a width equal to tilesheet's icon height") {
			assertResult(21){icon.getIconHeight}
		}
		it ("icon equals itself") {
			assert(icon == icon)
		}
		it ("icon equals a similar instance") {
			assert(icon == new RandomColorTilesheet.ColorStringIcon(Color.red, new Dimension(13,21)))
		}
		it ("icon does not equal a dissimilar instance (color)") {
			assert(icon != new RandomColorTilesheet.ColorStringIcon(Color.blue, new Dimension(13,21)))
		}
		it ("icon does not equal a dissimilar instance (size)") {
			assert(icon != new RandomColorTilesheet.ColorStringIcon(Color.red, new Dimension(14,14)))
		}
		it ("icon does not equal a string") {
			assert(icon != "apple")
		}
	}
	
	
	def compareIcons(a:Icon)(b:Icon) = {
		val a2 = a.asInstanceOf[SolidColorIcon]
		val b2 = b.asInstanceOf[SolidColorIcon]
		
		assertResult(a2.getIconColor){b2.getIconColor}
		assertResult(a2.getIconWidth){b2.getIconWidth}
		assertResult(a2.getIconHeight){b2.getIconHeight}
	}
}

