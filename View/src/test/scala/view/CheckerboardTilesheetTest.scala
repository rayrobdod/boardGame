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

class CheckerboardTilesheetTest extends FunSpec {
	describe ("CheckerboardTilesheet()") {
		val dut = new CheckerboardTilesheet(() => Color.yellow, () => Color.red, () => Color.blue, new RectangularDimension(12,34))
		it ("getIconFor(null, 0, 0, null)._1 is lightIcon") {
			assertResult(Color.red){dut.getIconFor(null, (0, 0), null)._1}
		}
		it ("getIconFor(null, 0, 1, null)._1 is darkIcon") {
			assertResult(Color.blue){dut.getIconFor(null, (0, 1), null)._1}
		}
		it ("getIconFor(null, 0, 0, null)._2 is transparentIcon") {
			assertResult(Color.yellow){dut.getIconFor(null, (0, 0), null)._2}
		}
		it ("dimension is dimension") {
			assertResult(new RectangularDimension(12,34)){dut.iconDimensions}
		}
	}
}
