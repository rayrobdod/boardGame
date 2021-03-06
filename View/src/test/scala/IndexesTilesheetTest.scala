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
import com.rayrobdod.boardGame.RectangularIndex

class IndexesTilesheetTest extends FunSpec {
	object MyDim {}
	
	describe ("IndexesTilesheet") {
		val dut = new IndexesTilesheet[RectangularIndex, MyDim.type, String](
			  {xy:(Int, Int) => if ((xy._1 + xy._2) % 2 == 0) {"light"} else {"dark"}}
			, {s => s}
			, MyDim
		)
		it ("getIconFor(...).aboveFrames is light for even index") {
			assertResult(Seq("light")){dut.getIconFor(null, (0, 0), null).aboveFrames}
		}
		it ("getIconFor(...).aboveFrames is dark for odd index") {
			assertResult(Seq("dark")){dut.getIconFor(null, (0, 1), null).aboveFrames}
		}
		it ("getIconFor(...)._2 for (0,0) is '(0,0)'") {
			assertResult(Seq("(0,0)")){dut.getIconFor(null, (0, 0), null).belowFrames}
		}
		it ("getIconFor(...)._2 for (2,3) is '(2,3)'") {
			assertResult(Seq("(2,3)")){dut.getIconFor(null, (2, 3), null).belowFrames}
		}
		it ("dimension is dimension") {
			assertResult(MyDim){dut.iconDimensions}
		}
	}
}
