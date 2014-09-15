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

import javax.swing.Icon
import com.rayrobdod.swing.SolidColorIcon

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks

class RectangularTilemapLayerTest extends FunSpec {
	val icon32 = new SolidColorIcon(java.awt.Color.cyan, 32, 32)
	val icon1632 = new SolidColorIcon(java.awt.Color.cyan, 16, 32)
	
	def tiles(x:Range, y:Range, i:Icon):Map[(Int, Int), Icon] = {
		x.map{a => y.map{b => (( ((a,b)), i ))}}.flatten.toMap
	}
	
	
	
	describe ("RectangularTilemapLayer with icon32 and map(0,0)to(3,3)") {
		val uut = new RectangularTilemapLayer(tiles(0 to 3, 0 to 3, icon32))
		
		it ("getEast is zero"){
			assertResult(0){uut.getEast}
		}
		it ("getNorth is zero"){
			assertResult(0){uut.getNorth}
		}
		it ("getWest is (32 * 4)"){
			assertResult(4 * 32){uut.getWest}
		}
		it ("getSouth is (32 * 4)"){
			assertResult(4 * 32){uut.getSouth}
		}
	}
	describe ("RectangularTilemapLayer with icon32 and map(-2,-2)to(1,1)") {
		val uut = new RectangularTilemapLayer(tiles(-2 to 1, -2 to 1, icon32))
		
		it ("getEast is (32 * 2)"){
			assertResult(2 * 32){uut.getEast}
		}
		it ("getNorth is (32 * 2)"){
			assertResult(32 * 2){uut.getNorth}
		}
		it ("getWest is (32 * 2)"){
			assertResult(2 * 32){uut.getWest}
		}
		it ("getSouth is (32 * 2)"){
			assertResult(2 * 32){uut.getSouth}
		}
	}
	describe ("RectangularTilemapLayer with icon32 and map(0,2)to(0,5)") {
		val uut = new RectangularTilemapLayer(tiles(0 to 0, 2 to 5, icon32))
		                 
		it ("getEast is (0)"){
			assertResult(0){uut.getEast}
		}
		it ("getNorth is -64"){
			assertResult(-64){uut.getNorth}
		}
		it ("getWest is (32 * 1)"){
			assertResult(1 * 32){uut.getWest}
		}
		it ("getSouth is (32 * 4)"){
			assertResult(6 * 32){uut.getSouth}
		}
	}
	describe ("RectangularTilemapLayer with icon1632 and map(-2,-2)to(1,1)") {
		val uut = new RectangularTilemapLayer(tiles(-2 to 1, -2 to 1, icon1632))
		
		it ("getEast is (16 * 2)"){
			assertResult(2 * 16){uut.getEast}
		}
		it ("getNorth is (32 * 2)"){
			assertResult(32 * 2){uut.getNorth}
		}
		it ("getWest is (16 * 2)"){
			assertResult(2 * 16){uut.getWest}
		}
		it ("getSouth is (32 * 2)"){
			assertResult(2 * 32){uut.getSouth}
		}
	}
}
