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
package com.rayrobdod.boardGame

import org.scalatest.FunSpec

class SpaceTest extends FunSpec {

	describe ("Space") {
		describe ("distanceTo (uniform cost of 1)") {
			it ("to itself is zero"){
				assertResult(0){
					uniformField.space((2,2)).get.distanceTo(uniformField.space((2,2)).get, constantCostFunction)
				}
			}
			it ("to a space six away is six"){
				assertResult(3 + 3){
					uniformField.space((0,0)).get.distanceTo(uniformField.space((3,3)).get, constantCostFunction)
				}
			}
		}
		describe ("pathTo (uniform cost)") {
			it ("Should have the first space be the original space"){
				val path = uniformField.space((0,0)).get.pathTo(uniformField.space((3,3)).get, constantCostFunction)
				
				assertResult(uniformField.space((0,0)).get){path.head}
			}
			it ("Should have the last space be the destination"){
				val path = uniformField.space((0,0)).get.pathTo(uniformField.space((3,3)).get, constantCostFunction)
				
				assertResult(uniformField.space((3,3)).get){path.last}
			}
			it ("Should have a length one more than the distance to"){
				val path = uniformField.space((0,0)).get.pathTo(uniformField.space((3,3)).get, constantCostFunction)
				val distanceTo = uniformField.space((0,0)).get.distanceTo(uniformField.space((3,3)).get, constantCostFunction)
				
				assertResult(distanceTo + 1){path.length}
			}
		}
		describe ("spacesWithin (uniform cost)") {
			it ("3 spaces within 1 move of corner") {
				assertResult(3){uniformField.space((0,0)).get.spacesWithin(1, constantCostFunction).size}
			}
			it ("5 spaces within 2 move of corner") {
				assertResult(6){uniformField.space((0,0)).get.spacesWithin(2, constantCostFunction).size}
			}
			it ("5 spaces within 1 move of center") {
				assertResult(5){uniformField.space((1,1)).get.spacesWithin(1, constantCostFunction).size}
			}
			it ("11 spaces within 2 move of center") {
				assertResult(11){uniformField.space((1,1)).get.spacesWithin(2, constantCostFunction).size}
			}
			it ("only space within 0 move of a space is that space") {
				assertResult(Seq(uniformField.space((1,1)).get)){uniformField.space((1,1)).get.spacesWithin(0, constantCostFunction)}
			}
			it ("0 spaces are within -1 move of any space") {
				assertResult(Seq.empty){uniformField.space((1,1)).get.spacesWithin(-1, constantCostFunction)}
			}
		}
		describe ("spacesAfter (uniform cost)") {
			it ("2 spaces after 1 move of corner") {
				assertResult(2){uniformField.space((0,0)).get.spacesAfter(1, constantCostFunction).size}
			}
			it ("4 spaces after 2 move of corner") {
				assertResult(4){uniformField.space((0,0)).get.spacesAfter(2, constantCostFunction).size}
			}
			it ("4 spaces after 1 move of center") {
				assertResult(4){uniformField.space((1,1)).get.spacesAfter(1, constantCostFunction).size}
			}
			it ("7 spaces after 2 move of center") {
				assertResult(7){uniformField.space((1,1)).get.spacesAfter(2, constantCostFunction).size}
			}
			it ("only space after 0 move of a space is that space") {
				assertResult(Seq(uniformField.space((1,1)).get)){uniformField.space((1,1)).get.spacesAfter(0, constantCostFunction)}
			}
			it ("0 spaces are after -1 move of any space") {
				assertResult(Seq.empty){uniformField.space((1,1)).get.spacesAfter(-1, constantCostFunction)}
			}
		}
		describe ("rawDijkstraData (uniform cost)") {
			it ("do a thing") {
				val res = uniformField.space((0,0)).get.rawDijkstraData(constantCostFunction)
				
				res.foreach{x =>
					val (target, (distance, from)) = x
					
					if (target == uniformField.space((0,0)).get) {
						assertResult(null){from}
						assertResult(0){distance}
					} else {
						assert(
							(target.asInstanceOf[RectangularSpaceLike[(Int,Int), _]].north == Option(from)) ||
							(target.asInstanceOf[RectangularSpaceLike[(Int,Int), _]].west == Option(from))
						)
						assertResult(uniformField.space((0,0)).get.distanceTo(target, constantCostFunction)){distance}
					}
				}
			}
		}
	}
	
	
	val uniformField = RectangularField[(Int,Int)](
		(0 to 3).map{(a) => (0 to 3).map{(b) => ((a, b))}}
	)
	val unreachableSpace = new SpaceLike[Any, Nothing]() {
		val typeOfSpace = "Unreachable"
		val adjacentSpaces = Nil
	}
}
