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
import scala.collection.immutable.Seq
import java.awt.{Color}
import com.rayrobdod.boardGame.RectangularField
import com.rayrobdod.boardGame.RectangularIndex

class HashcodeColorTilesheetTest extends FunSpec {
	object MyDim {}
	
	describe ("HashcodeColorTilesheet()") {
		val dut = new HashcodeColorTilesheet[RectangularIndex, MyDim.type, Int]({() => -1}, {(x:Color, d:(Int, Int)) => x.getRGB & 0xFFFFFF}, MyDim)
		it ("getIconFor(...)._2 is transparentIcon") {
			val field = RectangularField(Seq(Seq(1)))
			val res = dut.getIconFor(field, (0, 0), null).belowFrames
			assertResult(Seq(-1)){res}
		}
		it ("getIconFor(...)._1 for item with hashcode 1 is 1 << 23") {
			val field = RectangularField(Seq(Seq(1)))
			val res = dut.getIconFor(field, (0, 0), null).aboveFrames
			assertResult(Seq(1 << 23)){res}
		}
		it ("getIconFor(...)._1 for item with hashcode 2 is 1 << 15") {
			val field = RectangularField(Seq(Seq(2)))
			val res = dut.getIconFor(field, (0, 0), null).aboveFrames
			assertResult(Seq(1 << 15)){res}
		}
		it ("getIconFor(...)._1 for item with hashcode 4 is 1 << 7") {
			val field = RectangularField(Seq(Seq(4)))
			val res = dut.getIconFor(field, (0, 0), null).aboveFrames
			assertResult(Seq(1 << 7)){res}
		}
		it ("dimension is dimension") {
			assertResult(MyDim){dut.iconDimensions}
		}
	}
}
