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
import java.awt.Color
import com.rayrobdod.boardGame.view.RectangularDimension
import com.rayrobdod.boardGame.view.Swing._

final class RectangularIconTest extends FunSpec {
	describe ("rectangularIcon") {
		it ("getIconWidth == parameter") {
			assertResult(24){
				rgbToRectangularIcon(Color.black, RectangularDimension(24, 32)).getIconWidth
			}
		}
		it ("getIconHeight == parameter") {
			assertResult(32){
				rgbToRectangularIcon(Color.black, RectangularDimension(24, 32)).getIconHeight
			}
		}
		it ("draws asagsdaabbda") {
			val image = new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_ARGB);
			val graphics = image.getGraphics()
			
			val icon = rgbToRectangularIcon(Color.red, RectangularDimension(24, 32))
			icon.paintIcon(null, graphics, 10, 10);
			
			assert(0 == image.getRGB(0,0), "north east")
			assert(0 == image.getRGB(9,10), "north east")
			assert(0 == image.getRGB(10,9), "north east")
			assert(0xFFFF0000 == image.getRGB(10,10), "north east")
			
			assert(0 == image.getRGB(99,0), "north west")
			assert(0 == image.getRGB(34,10), "north west")
			assert(0 == image.getRGB(33,9), "north west")
			assert(0xFFFF0000 == image.getRGB(33,10), "north west")
			
			assert(0 == image.getRGB(0,99), "south east")
			assert(0 == image.getRGB(9,41), "south east")
			assert(0 == image.getRGB(10,42), "south east")
			assert(0xFFFF0000 == image.getRGB(10,41), "south east")
			
			assert(0 == image.getRGB(99,99), "south west")
			assert(0 == image.getRGB(34,41), "south west")
			assert(0 == image.getRGB(33,42), "south west")
			assert(0xFFFF0000 == image.getRGB(33,41), "south west")
		}
	}
}
