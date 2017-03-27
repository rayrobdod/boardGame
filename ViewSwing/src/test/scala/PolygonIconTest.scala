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

final class PolygonIconTest extends FunSpec {
	describe ("rectangularIcon") {
		it ("getIconWidth") {
			assertResult(24){
				val poly = new java.awt.Polygon(Array(0, 24, 12), Array(10, 0, 10), 3)
				rgbToPolygonIcon(Color.black, poly).getIconWidth
			}
		}
		it ("getIconHeight") {
			assertResult(10){
				val poly = new java.awt.Polygon(Array(0, 24, 12), Array(10, 0, 10), 3)
				rgbToPolygonIcon(Color.black, poly).getIconHeight
			}
		}
		it ("draws asagsdaabbda") {
			val image = new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_ARGB);
			val graphics = image.getGraphics()
			
			val poly = new java.awt.Polygon(Array(0, 24, 12), Array(10, 0, 10), 3)
			val icon = rgbToPolygonIcon(Color.green, poly)
			icon.paintIcon(null, graphics, 10, 10);
			
			assert(0 == image.getRGB(0,0), "north east")
			assert(0 == image.getRGB(99,0), "north west")
			assert(0 == image.getRGB(0,99), "south east")
			assert(0 == image.getRGB(99,99), "south west")
			
			assert(0xFF00FF00 == image.getRGB(22, 15), "center of shape")
		}
	}
}
