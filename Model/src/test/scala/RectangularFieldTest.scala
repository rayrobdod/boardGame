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

import java.util.NoSuchElementException
import scala.collection.immutable.Seq
import org.scalatest.FunSpec

class RectangularFieldTest extends FunSpec {
	
	val twoByTwoMap:Map[RectangularFieldIndex, Char] = Map(
		(0,0) → 'a', (0,1) → 'b', (1,0) → 'c', (1,1) → 'd'
	)
	val threeByThree:Seq[Seq[Char]] = Seq("abc", "def", "ghi")
	val sixByOne:Seq[Seq[Char]] = Seq("abcdef")
	val twoByTwoCsv:Seq[Seq[String]] = Seq(Seq("a", "b"), Seq("c", "d"))
	
	
	describe ("The RectangularField Map[Index, _] Factory") {
		it ("resultant space class 0,0 matches input"){
			val a = RectangularField(twoByTwoMap)
			assertResult('a'){a.getSpaceAt(0,0).get.typeOfSpace}
		}
		it ("resultant space class 0,1 matches input"){
			val a = RectangularField(twoByTwoMap)
			assertResult('b'){a.getSpaceAt(0,1).get.typeOfSpace}
		}
		it ("resultant space class 1,0 matches input"){
			val a = RectangularField(twoByTwoMap)
			assertResult('c'){a.getSpaceAt(1,0).get.typeOfSpace}
		}
		it ("resultant space 3,0 doesn't exist, for input 2x2"){
			val a = RectangularField(twoByTwoMap)
			intercept[NoSuchElementException]{a.getSpaceAt(3,0).get}
		}
	}
	describe ("The RectangularField Seq[Seq[_]] Factory") {
		it ("resultant space class 0,0 matches input"){
			val a = RectangularField(threeByThree)
			assertResult('a'){a.getSpaceAt(0,0).get.typeOfSpace}
		}
		it ("resultant space class 0,1 matches input"){
			val a = RectangularField(threeByThree)
			assertResult('d'){a.getSpaceAt(0,1).get.typeOfSpace}
		}
		it ("resultant space class 1,0 matches input"){
			val a = RectangularField(threeByThree)
			assertResult('b'){a.getSpaceAt(1,0).get.typeOfSpace}
		}
		it ("resultant space 3,0 doesn't exist, for input 3x3"){
			val a = RectangularField(threeByThree)
			intercept[NoSuchElementException]{a.getSpaceAt(3,0).get}
		}
		it ("resultant space 0,3 doesn't exist, for input 3x3"){
			val a = RectangularField(threeByThree)
			intercept[NoSuchElementException]{a.getSpaceAt(0,3).get}
		}
		it ("resultant space 0,1 doesn't exist, for input 6x1"){
			val a = RectangularField(sixByOne)
			intercept[NoSuchElementException]{a.getSpaceAt(0,1).get}
		}
		it ("resultant space 3,0 does exist, for input 6x1"){
			val a = RectangularField(sixByOne)
			assertResult('d'){a.getSpaceAt(3,0).get.typeOfSpace}
		}
	}
	describe ("The RectangularField Seq[Seq[_]] Factory, from a CSV") {
		it ("resultant space class 0,0 matches input"){
			val a = RectangularField(twoByTwoCsv)
			assertResult("a"){a.getSpaceAt(0,0).get.typeOfSpace}
		}
		it ("resultant space class 1,0 matches input"){
			val a = RectangularField(twoByTwoCsv)
			assertResult("b"){a.getSpaceAt(1,0).get.typeOfSpace}
		}
		it ("resultant space class 0,1 matches input"){
			val a = RectangularField(twoByTwoCsv)
			assertResult("c"){a.getSpaceAt(0,1).get.typeOfSpace}
		}
		it ("resultant space 3,0 doesn't exist, for input 2x2"){
			val a = RectangularField(twoByTwoCsv)
			intercept[NoSuchElementException]{a.getSpaceAt(3,0).get}
		}
	}
	describe ("RectangularField") {
		it ("space above 0,1 is space 0,0"){
			val a = RectangularField(threeByThree)
			assertResult(a.getSpaceAt(0,0)){a.getSpaceAt(0,1).get.north}
		}
		it ("space above 1,1 is space 1,0"){
			val a = RectangularField(threeByThree)
			assertResult(a.getSpaceAt(1,0)){a.getSpaceAt(1,1).get.north}
		}
		it ("space below 1,1 is space 1,2"){
			val a = RectangularField(threeByThree)
			assertResult(a.getSpaceAt(1,2)){a.getSpaceAt(1,1).get.south}
		}
		it ("space west of 1,1 is space 0,1"){
			val a = RectangularField(threeByThree)
			assertResult(a.getSpaceAt(0,1)){a.getSpaceAt(1,1).get.west}
		}
		it ("space east of 1,1 is space 2,1"){
			val a = RectangularField(threeByThree)
			assertResult(a.getSpaceAt(2,1)){a.getSpaceAt(1,1).get.east}
		}
		it ("space east space west of 1,1 is space 1,1"){
			val a = RectangularField(threeByThree)
			assertResult(a.getSpaceAt(1,1)){a.getSpaceAt(1,1).get.west.get.east}
		}
		it ("space above of 0,0 is None"){
			val a = RectangularField(threeByThree)
			assertResult(None){a.getSpaceAt(0,0).get.north}
		}
		describe ("Space") {
			it ("is equal to itself") {
				val a = RectangularField(threeByThree)
				assert(a.getSpaceAt(0,0) == a.getSpaceAt(0,0))
			}
			it ("is not equal to a string") {
				val a = RectangularField(threeByThree)
				assert(! (a.getSpaceAt(0,0) equals "Hello"))
			}
		}
	}
}
