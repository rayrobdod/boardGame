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

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks
import java.awt.{Dimension, Color}
import javax.swing.Icon;
import scala.util.Random
import com.rayrobdod.swing.SolidColorIcon

class RandomColorTilesheetTest extends FunSpec {
	describe ("Default RandomColorTilesheet") {
		it ("name is 'Random Color'") {
			assertResult("Random Color"){new RandomColorTilesheet(Swing.rgbToIcon, Swing.stringIcon).name}
		}
		it ("toString is 'Random Color: ...'") {
			assertResult("Random Color"){new RandomColorTilesheet(Swing.rgbToIcon, Swing.stringIcon).toString}
		}
		it ("rng is 0") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0})
			
			val exp = (( (("", 0, 64, 24)), (("000000", 0xFFFFFF, 64, 24)) ))
			val dut = new RandomColorTilesheet(
					{(a,b) => (("", a.getRGB & 0xFFFFFF, b.width, b.height))},
					{(a,b,c) => ((a, b.getRGB & 0xFFFFFF, c.width, c.height))}
			)
			val res = dut.getIconFor(null, -1, -1, rng)
			assertResult(exp){res}
		}
		it ("rng is 0x123456") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0x123456})
			
			val exp = (( (("", 0x123456, 64, 24)), (("123456", 0xFFFFFF, 64, 24)) ))
			val dut = new RandomColorTilesheet(
					{(a,b) => (("", a.getRGB & 0xFFFFFF, b.width, b.height))},
					{(a,b,c) => ((a, b.getRGB & 0xFFFFFF, c.width, c.height))}
			)
			val res = dut.getIconFor(null, -1, -1, rng)
			assertResult(exp){res}
		}
		it ("rng is 0xFFFFFF") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0xFFFFFF})
			
			val exp = (( (("", 0xFFFFFF, 64, 24)), (("ffffff", 0, 64, 24)) ))
			val dut = new RandomColorTilesheet(
					{(a,b) => (("", a.getRGB & 0xFFFFFF, b.width, b.height))},
					{(a,b,c) => ((a, b.getRGB & 0xFFFFFF, c.width, c.height))}
			)
			val res = dut.getIconFor(null, -1, -1, rng)
			assertResult(exp){res}
		}
	}
	describe ("RandomColorTilesheet(13,21)") {
		val dut = new RandomColorTilesheet(Swing.rgbToIcon, Swing.stringIcon, new java.awt.Dimension(13, 21))
		
		it ("name is 'Random Color'") {
			assertResult("Random Color"){dut.name}
		}
		it ("toString is 'Random Color: ...'") {
			assertResult("Random Color"){dut.toString}
		}
		it ("getIconFor(null, 0, 0, null)._1 is a SolidColorIcon") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0})
			
			compareIcons(new SolidColorIcon(Color.black, 13,21)){dut.getIconFor(null, -1, -1, rng)._1}
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

