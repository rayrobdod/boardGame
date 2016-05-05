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
import com.rayrobdod.boardGame.swingView

class ColorStringIconTest extends FunSpec {
	describe ("ColorStringIcon") {
		val icon = new swingView.ColorStringIcon(Color.red, new Dimension(13,21))
		
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
			assert(icon == new swingView.ColorStringIcon(Color.red, new Dimension(13,21)))
		}
		it ("icon does not equal a dissimilar instance (color)") {
			assert(icon != new swingView.ColorStringIcon(Color.blue, new Dimension(13,21)))
		}
		it ("icon does not equal a dissimilar instance (size)") {
			assert(icon != new swingView.ColorStringIcon(Color.red, new Dimension(14,14)))
		}
		it ("icon does not equal a string") {
			assert(icon != "apple")
		}
	}
}
