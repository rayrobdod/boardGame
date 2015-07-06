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
		describe ("distanceTo (uniform cost of 1)") {
			it ("to itself is zero"){
				assertResult(0){
					uniformField(2,2).distanceTo(uniformField(2,2), Space.constantCostFunction)
				}
			}
			it ("to a space six away is six"){
				assertResult(3 + 3){
					uniformField(0,0).distanceTo(uniformField(3,3), Space.constantCostFunction)
				}
			}
		}
		describe ("pathTo (uniform cost)") {
			it ("Should have the first space be the original space"){
				val path = uniformField(0,0).pathTo(uniformField(3,3), Space.constantCostFunction)
				
				assertResult(uniformField(0,0)){path.head}
			}
			it ("Should have the last space be the destination"){
				val path = uniformField(0,0).pathTo(uniformField(3,3), Space.constantCostFunction)
				
				assertResult(uniformField(3,3)){path.last}
			}
			it ("Should have a length one more than the distance to"){
				val path = uniformField(0,0).pathTo(uniformField(3,3), Space.constantCostFunction)
				val distanceTo = uniformField(0,0).distanceTo(uniformField(3,3), Space.constantCostFunction)
				
				assertResult(distanceTo + 1){path.length}
			}
		}
		describe ("spacesWithin (uniform cost)") {
			it ("3 spaces within 1 move of corner") {
				assertResult(3){uniformField(0,0).spacesWithin(1, Space.constantCostFunction).size}
			}
			it ("5 spaces within 2 move of corner") {
				assertResult(6){uniformField(0,0).spacesWithin(2, Space.constantCostFunction).size}
			}
			it ("5 spaces within 1 move of center") {
				assertResult(5){uniformField(1,1).spacesWithin(1, Space.constantCostFunction).size}
			}
			it ("11 spaces within 2 move of center") {
				assertResult(11){uniformField(1,1).spacesWithin(2, Space.constantCostFunction).size}
			}
			it ("only space within 0 move of a space is that space") {
				assertResult(Set(uniformField(1,1))){uniformField(1,1).spacesWithin(0, Space.constantCostFunction)}
			}
			it ("0 spaces are within -1 move of any space") {
				assertResult(Set.empty){uniformField(1,1).spacesWithin(-1, Space.constantCostFunction)}
			}
		}
		describe ("spacesAfter (uniform cost)") {
			it ("2 spaces after 1 move of corner") {
				assertResult(2){uniformField(0,0).spacesAfter(1, Space.constantCostFunction).size}
			}
			it ("4 spaces after 2 move of corner") {
				assertResult(4){uniformField(0,0).spacesAfter(2, Space.constantCostFunction).size}
			}
			it ("4 spaces after 1 move of center") {
				assertResult(4){uniformField(1,1).spacesAfter(1, Space.constantCostFunction).size}
			}
			it ("7 spaces after 2 move of center") {
				assertResult(7){uniformField(1,1).spacesAfter(2, Space.constantCostFunction).size}
			}
			it ("only space after 0 move of a space is that space") {
				assertResult(Set(uniformField(1,1))){uniformField(1,1).spacesAfter(0, Space.constantCostFunction)}
			}
			it ("0 spaces are after -1 move of any space") {
				assertResult(Set.empty){uniformField(1,1).spacesWithin(-1, Space.constantCostFunction)}
			}
		}
		describe ("rawDijkstraData (uniform cost)") {
			it ("do a thing") {
				val res = uniformField(0,0).rawDijkstraData(Space.constantCostFunction)
				
				res.foreach{x =>
					val (target, (distance, from)) = x
					
					if (target == uniformField(0,0)) {
						assertResult(null){from}
						assertResult(0){distance}
					} else {
						assert(
							(target.asInstanceOf[RectangularSpace[(Int,Int)]].up == Option(from)) ||
							(target.asInstanceOf[RectangularSpace[(Int,Int)]].left == Option(from))
						)
						assertResult(uniformField(0,0).distanceTo(target, Space.constantCostFunction)){distance}
					}
				}
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
