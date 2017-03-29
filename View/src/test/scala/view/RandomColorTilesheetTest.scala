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
import java.awt.Color
import javax.swing.Icon;
import scala.util.Random
import com.rayrobdod.boardGame.RectangularIndex

class RandomColorTilesheetTest extends FunSpec {
	
	describe ("Default RandomColorTilesheet") {
		it ("rng is 0") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0})
			
			val exp = (( (("", 0, 64, 24)), (("000000", 0xFFFFFF, 64, 24)) ))
			val dut = new RandomColorTilesheet[RectangularIndex, RectangularDimension, (String, Int, Int, Int)](
					  {(a,b,c) => (("", a.getRGB & 0xFFFFFF, b.width, b.height))}
					, {(a,b,c) => ((a, b.getRGB & 0xFFFFFF, c.width, c.height))}
					, RectangularDimension(64, 24)
			)
			val res = dut.getIconFor(null, (-1, -1), rng)
			assertResult(exp){res}
		}
		it ("rng is 0x123456") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0x123456})
			
			val exp = (( (("", 0x123456, 64, 24)), (("123456", 0xFFFFFF, 64, 24)) ))
			val dut = new RandomColorTilesheet[RectangularIndex, RectangularDimension, (String, Int, Int, Int)](
					  {(a,b,c) => (("", a.getRGB & 0xFFFFFF, b.width, b.height))}
					, {(a,b,c) => ((a, b.getRGB & 0xFFFFFF, c.width, c.height))}
					, RectangularDimension(64, 24)
			)
			val res = dut.getIconFor(null, (-1, -1), rng)
			assertResult(exp){res}
		}
		it ("rng is 0xFFFFFF") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0xFFFFFF})
			
			val exp = (( (("", 0xFFFFFF, 64, 24)), (("ffffff", 0, 64, 24)) ))
			val dut = new RandomColorTilesheet[RectangularIndex, RectangularDimension, (String, Int, Int, Int)](
					  {(a,b,c) => (("", a.getRGB & 0xFFFFFF, b.width, b.height))}
					, {(a,b,c) => ((a, b.getRGB & 0xFFFFFF, c.width, c.height))}
					, RectangularDimension(64, 24)
			)
			val res = dut.getIconFor(null, (-1, -1), rng)
			assertResult(exp){res}
		}
	}
	describe ("RandomColorTilesheet(13,21)") {
		val dut = new RandomColorTilesheet[RectangularIndex, RectangularDimension, (String, Int, Int, Int)](
				  {(a,b,c) => (("", a.getRGB & 0xFFFFFF, b.width, b.height))}
				, {(a,b,c) => ((a, b.getRGB & 0xFFFFFF, c.width, c.height))}
				, RectangularDimension(13, 21)
		)
		
		it ("getIconFor(null, 0, 0, null) has the new dimensions") {
			val rng = new Random(new java.util.Random(){override def next(bits:Int):Int = 0})
			
			val exp = (( (("", 0, 13, 21)), (("000000", 0xFFFFFF, 13, 21)) ))
			assertResult(exp){dut.getIconFor(null, (-1, -1), rng)}
		}
	}
}

