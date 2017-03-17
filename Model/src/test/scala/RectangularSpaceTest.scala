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

import org.scalatest.{FunSpec}

class RectangularSpaceTest extends FunSpec {
	
	describe ("StrictRectangularSpaceViaFutures") {
		
		def directionTests(
				factory:Function1[Function0[Option[StrictRectangularSpace[Int]]], StrictRectangularSpace[Int]],
				dirFunction:Function1[StrictRectangularSpace[Int], Option[StrictRectangularSpace[Int]]]
		) {
			it ("is the result of calling the westOption parameter"){
				val res = Some(unescapableSpace(0))
				val src = factory( {() => res} );
				
				assertResult(res){dirFunction(src)}
			}
			it ("factory from constructor is only evaluated once"){
				val src = factory( {() => Some(unescapableSpace(scala.util.Random.nextInt()))} );
				
				val res = dirFunction(src)
				assertResult(res){dirFunction(src)}
			}
		}
		
		
		describe ("west") {
			directionTests(
				{x => new StrictRectangularSpaceViaFutures(1, x, noneFuture, noneFuture, noneFuture)},
				{x => x.west}
			)
		}
		describe ("north") {
			directionTests(
				{x => new StrictRectangularSpaceViaFutures(1, noneFuture, x, noneFuture, noneFuture)},
				{x => x.north}
			)
		}
		describe ("east") {
			directionTests(
				{x => new StrictRectangularSpaceViaFutures(1, noneFuture, noneFuture, x, noneFuture)},
				{x => x.east}
			)
		}
		describe ("south") {
			directionTests(
				{x => new StrictRectangularSpaceViaFutures(1, noneFuture, noneFuture, noneFuture, x)},
				{x => x.south}
			)
		}
		
		
		describe ("adjacentSpaces") {
			describe ("when all four elements are Some") {
				val src = new StrictRectangularSpaceViaFutures(1, unescapableSpaceFuture(34), unescapableSpaceFuture(64), unescapableSpaceFuture(134), unescapableSpaceFuture(-134))
				
				it ("has a length of four") { assertResult(4){src.adjacentSpaces.size} }
				it ("contains the west value")  { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.west.get)  }
				it ("contains the east value") { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.east.get) }
				it ("contains the north value" ) { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.north.get)    }
				it ("contains the south value" ) { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.south.get)  }
			}
			describe ("when all four elements are None") {
				def src = new StrictRectangularSpaceViaFutures(1, noneFuture, noneFuture, noneFuture, noneFuture)
				
				it ("has a length of zero") { assertResult(0){src.adjacentSpaces.size} }
			}
			describe ("when there is a mix of Some and None") {
				val src = new StrictRectangularSpaceViaFutures(1, unescapableSpaceFuture(7), unescapableSpaceFuture(-345), noneFuture, noneFuture)
				
				it ("has a length equal to the count of Somes") { assertResult(2){src.adjacentSpaces.size} }
				it ("contains the west value" ) { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.west.get) }
				it ("contains the north value"   ) { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.north.get)   }
			}
		}
	}
	
	
	def noneFuture = {() => None}
	def unescapableSpace[A](typ:A) = new StrictRectangularSpaceViaFutures(typ, noneFuture, noneFuture, noneFuture, noneFuture)
	def unescapableSpaceFuture[A](typ:A) = {() => Option(unescapableSpace(typ))}
}
