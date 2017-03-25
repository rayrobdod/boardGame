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
package com.rayrobdod.boardGame.javafxView

import org.scalatest.FunSpec
import java.awt.{Color => AwtColor}
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Color
import com.rayrobdod.boardGame.view.RectangularDimension
import com.rayrobdod.boardGame.view.Javafx._

class PackageTest extends FunSpec {
	
	describe ("blankIcon") {
		it ("returns a rectangle with the dimensions provided and a transparent fill") {
			assertRectangleResult( new Rectangle(21, 75, Color.TRANSPARENT) ) {
				blankIcon.asInstanceOf[Rectangle]
			}
		}
	}
	describe ("rgbToRectangularIcon") {
		it ("returns a rectangle with the specified properties") {
			assertRectangleResult( new Rectangle(21, 75, Color.RED) ) {
				rgbToRectangularIcon(AwtColor.red, RectangularDimension(21, 75)).asInstanceOf[Rectangle]
			}
		}
	}
	
	
	
	
	def assertRectangleResult(x:Rectangle)(y:Rectangle) {
		assertResult(x.getX)(y.getX)
		assertResult(x.getY)(y.getY)
		assertResult(x.getFill)(y.getFill)
	}
	
}

