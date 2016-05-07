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

class NilTilesheetTest extends FunSpec {
	describe ("NilTilesheet") {
		it ("name is 'Nil'") {
			assertResult("Nil"){NilTilesheet(1f).name}
		}
		it ("getIconFor(null, 0, 0, null)._1 is BlankIcon") {
			assertResult(1f){NilTilesheet(1f).getIconFor(null, 0, 0, null)._1}
		}
		it ("getIconFor(null, 0, 1, null)._1 is BlankIcon") {
			assertResult(1f){NilTilesheet(1f).getIconFor(null, 0, 1, null)._1}
		}
		it ("getIconFor(null, 0, 0, null)._2 is BlankIcon") {
			assertResult(1f){NilTilesheet(1f).getIconFor(null, 0, 0, null)._2}
		}
	}
	
}
