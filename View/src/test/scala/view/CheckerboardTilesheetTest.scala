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
import java.awt.{Color}

class CheckerboardTilesheetTest extends FunSpec {
	describe ("CheckerboardTilesheet()") {
		val dut = CheckerboardTilesheet(() => Color.yellow, () => Color.red, () => Color.blue)
		it ("name is \"Checkerboard\"") {
			assertResult("Checkerboard"){dut.name}
		}
		it ("toString is \"Checkerboard\"") {
			assertResult("Checkerboard"){dut.toString}
		}
		it ("light color is red") {
			assertResult(Color.red){dut.lightIcon()}
		}
		it ("dark color is blue") {
			assertResult(Color.blue){dut.darkIcon()}
		}
		it ("transparentIcon is yellow") {
			assertResult(Color.yellow){dut.transparentIcon()}
		}
		it ("getIconFor(null, 0, 0, null)._1 is lightIcon") {
			assertResult(dut.lightIcon()){dut.getIconFor(null, 0, 0, null)._1}
		}
		it ("getIconFor(null, 0, 1, null)._1 is darkIcon") {
			assertResult(dut.darkIcon()){dut.getIconFor(null, 0, 1, null)._1}
		}
		it ("getIconFor(null, 0, 0, null)._2 is transparentIcon") {
			assertResult(dut.transparentIcon()){dut.getIconFor(null, 0, 0, null)._2}
		}
	}
}
