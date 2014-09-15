/*
	Deduction Tactics
	Copyright (C) 2012-2013  Raymond Dodge

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
package com.rayrobdod.boardGame

import scala.collection.immutable.Seq
import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks

class SpaceTest extends FunSpec {

	describe ("Space") {
		describe ("distanceTo (uniform cost)") {
			it ("to itself is zero"){
				assertResult(0){
					uniformField.space(2,2).distanceTo(uniformField.space(2,2), Space.constantCostFunction)
				}
			}
			it ("jkljl"){
				assertResult(3 + 3){
					uniformField.space(0,0).distanceTo(uniformField.space(3,3), Space.constantCostFunction)
				}
			}
		}
		describe ("pathTo (uniform cost)") {
			it ("Should have the first space be the original space"){
				val path = uniformField.space(0,0).pathTo(uniformField.space(3,3), Space.constantCostFunction)
				
				assertResult(uniformField.space(0,0)){path.head}
			}
			it ("Should have the last space be the destination"){
				val path = uniformField.space(0,0).pathTo(uniformField.space(3,3), Space.constantCostFunction)
				
				assertResult(uniformField.space(3,3)){path.last}
			}
			it ("Should have a length one more than the distance to"){
				val path = uniformField.space(0,0).pathTo(uniformField.space(3,3), Space.constantCostFunction)
				val distanceTo = uniformField.space(0,0).distanceTo(uniformField.space(3,3), Space.constantCostFunction)
				
				assertResult(distanceTo + 1){path.length}
			}
		}
		describe ("spacesWithin (uniform cost)") {
			it ("3 spaces within 1 move of corner") {
				assertResult(3){uniformField.space(0,0).spacesWithin(1, Space.constantCostFunction).size}
			}
			it ("5 spaces within 2 move of corner") {
				assertResult(6){uniformField.space(0,0).spacesWithin(2, Space.constantCostFunction).size}
			}
			it ("5 spaces within 1 move of center") {
				assertResult(5){uniformField.space(1,1).spacesWithin(1, Space.constantCostFunction).size}
			}
			it ("11 spaces within 2 move of center") {
				assertResult(11){uniformField.space(1,1).spacesWithin(2, Space.constantCostFunction).size}
			}
			it ("only space within 0 move of a space is that space") {
				assertResult(Set(uniformField.space(1,1))){uniformField.space(1,1).spacesWithin(0, Space.constantCostFunction)}
			}
		}
	}
	
	
	val uniformField = RectangularField[(Int,Int)](
		(0 to 3).map{(a) => (0 to 3).map{(b) => ((a, b))}}
	)
	val unreachableSpace = new Space[Any]() {
		val typeOfSpace = "Unreachable"
		val adjacentSpaces = Nil
	}
}
