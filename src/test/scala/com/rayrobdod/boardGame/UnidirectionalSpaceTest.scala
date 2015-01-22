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

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks
import scala.collection.immutable.{Set, List, Map}

class UnidirectionalSpaceTest extends FunSpec {

	val nilSpace = new UnidirectionalSpace("nilSpace", None)
	val aSpace = new UnidirectionalSpace("a", Some(nilSpace))
	val bSpace = new UnidirectionalSpace("b", Some(aSpace))
	val spaceStringCostValue = {(from:Space[_ <: String], to:Space[_ <: String]) => to.typeOfSpace.toInt}
	
	
	
	describe ("UnidirectionalSpace") {
		describe ("adjacentSpaces") {
			it ("is Set.empty when nextSpace is None"){
				val res = None
				val src = new UnidirectionalSpace("b", res)
				
				assertResult(Set.empty){src.adjacentSpaces}
			}
			it ("is Set(_) when nextSpace is Some(_)"){
				val res = Some(aSpace)
				val src = new UnidirectionalSpace("b", res)
				
				assertResult(Set(aSpace)){src.adjacentSpaces}
			}
		}
		describe ("spaceAfter") {
			val firstSpace = (0 to 39).foldRight(nilSpace){(x:Int,y:UnidirectionalSpace[String]) => new UnidirectionalSpace(x.toString, Some(y))}
			val spaceSeq = new UnidirectionalSpaceSeq(Some(firstSpace)) 
			
			describe ("is this when availiableCost is 0"){
				it (" 0") { assertResult( Some(spaceSeq( 0)) ){spaceSeq( 0).spaceAfter(0, Space.constantCostFunction)} }
				it (" 5") { assertResult( Some(spaceSeq( 5)) ){spaceSeq( 5).spaceAfter(0, Space.constantCostFunction)} }
				it ("12") { assertResult( Some(spaceSeq(12)) ){spaceSeq(12).spaceAfter(0, Space.constantCostFunction)} }
				it ("23") { assertResult( Some(spaceSeq(23)) ){spaceSeq(23).spaceAfter(0, Space.constantCostFunction)} }
				it ("40") { assertResult( Some(spaceSeq(40)) ){spaceSeq(40).spaceAfter(0, Space.constantCostFunction)} }
			}
			describe ("is None when availiableCost is negative"){
				it (" 0") { assertResult( None ){spaceSeq( 0).spaceAfter(-2, Space.constantCostFunction)} }
				it (" 5") { assertResult( None ){spaceSeq( 5).spaceAfter(-2, Space.constantCostFunction)} }
				it ("12") { assertResult( None ){spaceSeq(12).spaceAfter(-2, Space.constantCostFunction)} }
				it ("23") { assertResult( None ){spaceSeq(23).spaceAfter(-2, Space.constantCostFunction)} }
				it ("40") { assertResult( None ){spaceSeq(40).spaceAfter(-2, Space.constantCostFunction)} }
			}
			describe ("is the next value when cost is one space"){
				it (" 0") { assertResult( Some(spaceSeq( 1)) ){spaceSeq( 0).spaceAfter(1, Space.constantCostFunction)} }
				it (" 5") { assertResult( Some(spaceSeq( 6)) ){spaceSeq( 5).spaceAfter(1, Space.constantCostFunction)} }
				it ("12") { assertResult( Some(spaceSeq(13)) ){spaceSeq(12).spaceAfter(1, Space.constantCostFunction)} }
				it ("23") { assertResult( Some(spaceSeq(24)) ){spaceSeq(23).spaceAfter(1, Space.constantCostFunction)} }
				it ("40") { assertResult( None               ){spaceSeq(40).spaceAfter(1, Space.constantCostFunction)} }
			}
			describe ("is the next-next-next value when cost is three space"){
				it (" 0") { assertResult( Some(spaceSeq( 3)) ){spaceSeq( 0).spaceAfter(3, Space.constantCostFunction)} }
				it (" 5") { assertResult( Some(spaceSeq( 8)) ){spaceSeq( 5).spaceAfter(3, Space.constantCostFunction)} }
				it ("12") { assertResult( Some(spaceSeq(15)) ){spaceSeq(12).spaceAfter(3, Space.constantCostFunction)} }
				it ("23") { assertResult( Some(spaceSeq(26)) ){spaceSeq(23).spaceAfter(3, Space.constantCostFunction)} }
				it ("40") { assertResult( None               ){spaceSeq(40).spaceAfter(3, Space.constantCostFunction)} }
			}
			describe ("takes into account the costFunction"){
				it (" 0") { assertResult( Some(spaceSeq( 1)) ){spaceSeq( 0).spaceAfter( 1, spaceStringCostValue)} }
				it (" 5") { assertResult( Some(spaceSeq( 6)) ){spaceSeq( 5).spaceAfter( 6, spaceStringCostValue)} }
				it ("12") { assertResult( Some(spaceSeq(13)) ){spaceSeq(12).spaceAfter(13, spaceStringCostValue)} }
				it ("23") { assertResult( Some(spaceSeq(24)) ){spaceSeq(23).spaceAfter(24, spaceStringCostValue)} }
			}
			describe ("doesn't return a space if cannot land exactly on a space"){
				it ("Exactly one space of availiableCost")        { assertResult( Some(spaceSeq( 4)) ){spaceSeq( 3).spaceAfter( 4,   spaceStringCostValue)} }
				it ("Between one and two space of availableCost") { assertResult( None               ){spaceSeq( 3).spaceAfter( 4+2, spaceStringCostValue)} }
				it ("Exactly two space of availiableCost")        { assertResult( Some(spaceSeq( 5)) ){spaceSeq( 3).spaceAfter( 4+5, spaceStringCostValue)} }
			}
			
		}
	}
	
	
}
