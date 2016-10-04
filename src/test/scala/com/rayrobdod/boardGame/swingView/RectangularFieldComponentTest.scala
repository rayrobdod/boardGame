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
import com.rayrobdod.boardGame.RectangularField

class RectangularFieldComponentTest extends FunSpec {
	
	describe ("4x4 field using a default CheckerboardTilesheet") {
		val uut = RectangularFieldComponent[Any](
			RectangularField[Any]((0 to 3).map{x => (0 to 3).map{y => x * y}}),
			new CheckerboardTilesheet
		)._1
		
		it ("MaxSize.width is (16 * 4)"){
			assertResult(4 * 16){uut.getMaximumSize.width}
		}
		it ("MaxSize.height is (16 * 4)"){
			assertResult(4 * 16){uut.getMaximumSize.height}
		}
	}
}
