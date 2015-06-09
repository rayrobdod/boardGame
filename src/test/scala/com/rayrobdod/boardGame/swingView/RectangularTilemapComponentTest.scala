/*
	Deduction Tactics
	Copyright (C) 2014  Raymond Dodge

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

import java.awt.{Component, Graphics}
import javax.swing.Icon
import com.rayrobdod.swing.SolidColorIcon

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks

class RectangularTilemapComponentTest extends FunSpec {
	val icon32 = new SolidColorIcon(java.awt.Color.cyan, 32, 32)
	val icon1632 = new SolidColorIcon(java.awt.Color.cyan, 16, 32)
	
	def tiles(x:Range, y:Range, i:Icon):Map[(Int, Int), Icon] = {
		x.map{a => y.map{b => (( ((a,b)), i ))}}.flatten.toMap
	}
	
	
	
	describe ("Bounds checking") {
	describe ("RectangularTilemapComponent with icon32 and map(0,0)to(3,3)") {
		val uut = new RectangularTilemapComponent(tiles(0 to 3, 0 to 3, icon32))
		
		it ("getWest is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.width}
		}
		it ("getSouth is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.height}
		}
	}
	describe ("RectangularTilemapComponent with icon32 and map(-2,-2)to(1,1)") {
		val uut = new RectangularTilemapComponent(tiles(-2 to 1, -2 to 1, icon32))
		
		it ("getWest is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.width}
		}
		it ("getSouth is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.height}
		}
	}
	describe ("RectangularTilemapComponent with icon32 and map(0,2)to(0,5)") {
		val uut = new RectangularTilemapComponent(tiles(0 to 0, 2 to 5, icon32))
		
		it ("getWest is (32 * 1)"){
			assertResult(1 * 32){uut.getMaximumSize.width}
		}
		it ("getSouth is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.height}
		}
	}
	describe ("RectangularTilemapComponent with icon1632 and map(-2,-2)to(1,1)") {
		val uut = new RectangularTilemapComponent(tiles(-2 to 1, -2 to 1, icon1632))
		
		it ("getWest is (16 * 4)"){
			assertResult(4 * 16){uut.getMaximumSize.width}
		}
		it ("getSouth is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.height}
		}
	}
	}
	
	
	
	
	class MockPaintIcon(width:Int, height:Int, expectedX:Int, expectedY:Int) extends javax.swing.Icon {
		def getIconWidth = width
		def getIconHeight = height
		def paintIcon(c:Component, g:Graphics, x:Int, y:Int) = {
			assertResult(expectedX){x}
			assertResult(expectedY){y}
		}
	}
}
