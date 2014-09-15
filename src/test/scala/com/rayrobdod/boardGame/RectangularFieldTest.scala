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
package com.rayrobdod.boardGame

import java.util.NoSuchElementException
import scala.collection.immutable.Seq
import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks

class RectangularFieldTest extends FunSpec {
	
	val threeByThree:Seq[Seq[Char]] = Seq("abc", "def", "ghi")
	val sixByOne:Seq[Seq[Char]] = Seq("abcdef")
	
	
	describe ("The RectangularField Factory") {
		it ("resultant space class 0,0 matches input"){
			val a = RectangularField(threeByThree)
			assertResult('a'){a(0,0).typeOfSpace}
		}
		it ("resultant space class 0,1 matches input"){
			val a = RectangularField(threeByThree)
			assertResult('d'){a(0,1).typeOfSpace}
		}
		it ("resultant space class 1,0 matches input"){
			val a = RectangularField(threeByThree)
			assertResult('b'){a(1,0).typeOfSpace}
		}
		it ("resultant space 3,0 doesn't exist, for input 3x3"){
			val a = RectangularField(threeByThree)
			intercept[NoSuchElementException]{a(3,0)}
		}
		it ("resultant space 0,3 doesn't exist, for input 3x3"){
			val a = RectangularField(threeByThree)
			intercept[NoSuchElementException]{a(0,3)}
		}
		it ("resultant space 0,1 doesn't exist, for input 6x1"){
			val a = RectangularField(sixByOne)
			intercept[NoSuchElementException]{a(0,1)}
		}
		it ("resultant space 3,0 does exist, for input 6x1"){
			val a = RectangularField(sixByOne)
			assertResult('d'){a(3,0).typeOfSpace}
		}
	}
	describe ("RectangularField") {
		it ("space above 0,1 is space 0,0"){
			val a = RectangularField(threeByThree)
			assertResult(a(0,0)){a(0,1).up.get}
		}
		it ("space above 1,1 is space 1,0"){
			val a = RectangularField(threeByThree)
			assertResult(a(1,0)){a(1,1).up.get}
		}
		it ("space below 1,1 is space 1,2"){
			val a = RectangularField(threeByThree)
			assertResult(a(1,2)){a(1,1).down.get}
		}
		it ("space left of 1,1 is space 0,1"){
			val a = RectangularField(threeByThree)
			assertResult(a(0,1)){a(1,1).left.get}
		}
		it ("space right of 1,1 is space 2,1"){
			val a = RectangularField(threeByThree)
			assertResult(a(2,1)){a(1,1).right.get}
		}
		it ("space above of 0,0 is None"){
			val a = RectangularField(threeByThree)
			assertResult(None){a(0,0).up}
		}
	}
}
