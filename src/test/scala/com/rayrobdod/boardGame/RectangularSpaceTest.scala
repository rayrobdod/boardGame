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

class RectangularSpaceTest extends FunSpec {
	
	describe ("RectangularSpaceViaFutures") {
		
		def directionTests(
				factory:Function1[Function0[Option[Space[Int]]], RectangularSpace[Int]],
				dirFunction:Function1[RectangularSpace[Int], Option[Space[Int]]]
		) {
			it ("is the result of calling the leftOption parameter"){
				val res = Some(new UnescapableSpace(0))
				val src = factory( {() => res} );
				
				assertResult(res){dirFunction(src)}
			}
			it ("factory from constructor is only evaluated once"){
				val src = factory( {() => Some(new UnescapableSpace(scala.util.Random.nextInt()))} );
				
				val res = dirFunction(src)
				assertResult(res){dirFunction(src)}
			}
		}
		
		
		describe ("left") {
			directionTests(
				{x => new RectangularSpaceViaFutures(1, x, noneFuture, noneFuture, noneFuture)},
				{x => x.left}
			)
		}
		describe ("up") {
			directionTests(
				{x => new RectangularSpaceViaFutures(1, noneFuture, x, noneFuture, noneFuture)},
				{x => x.up}
			)
		}
		describe ("right") {
			directionTests(
				{x => new RectangularSpaceViaFutures(1, noneFuture, noneFuture, x, noneFuture)},
				{x => x.right}
			)
		}
		describe ("down") {
			directionTests(
				{x => new RectangularSpaceViaFutures(1, noneFuture, noneFuture, noneFuture, x)},
				{x => x.down}
			)
		}
		
		
		describe ("adjacentSpaces") {
			describe ("when all four elements are Some") {
				def adsfasdf(x:Int) = {() => Option(new UnescapableSpace(x))}
				val src = new RectangularSpaceViaFutures(1, adsfasdf(34), adsfasdf(64), adsfasdf(134), adsfasdf(-134))
				
				it ("has a length of four") { assertResult(4){src.adjacentSpaces.size} }
				it ("contains the left value")  { assert(src.adjacentSpaces.map{x => x:Space[Int]} contains src.left.get)  }
				it ("contains the right value") { assert(src.adjacentSpaces.map{x => x:Space[Int]} contains src.right.get) }
				it ("contains the up value"   ) { assert(src.adjacentSpaces.map{x => x:Space[Int]} contains src.up.get)    }
				it ("contains the down value" ) { assert(src.adjacentSpaces.map{x => x:Space[Int]} contains src.down.get)  }
			}
			describe ("when all four elements are None") {
				def src = new RectangularSpaceViaFutures(1, noneFuture, noneFuture, noneFuture, noneFuture)
				
				it ("has a length of zero") { assertResult(0){src.adjacentSpaces.size} }
			}
			describe ("when there is a mix of Some and None") {
				def adsfasdf(x:Int) = {() => Option(new UnescapableSpace(x))}
				val src = new RectangularSpaceViaFutures(1, adsfasdf(7), adsfasdf(-345), noneFuture, noneFuture)
				
				it ("has a length equal to the count of Somes") { assertResult(2){src.adjacentSpaces.size} }
				it ("contains the left value" ) { assert(src.adjacentSpaces.map{x => x:Space[Int]} contains src.left.get) }
				it ("contains the up value"   ) { assert(src.adjacentSpaces.map{x => x:Space[Int]} contains src.up.get)   }
			}
		}
	}
	
	describe ("StrictRectangularSpaceViaFutures") {
		
		def directionTests(
				factory:Function1[Function0[Option[StrictRectangularSpace[Int]]], StrictRectangularSpace[Int]],
				dirFunction:Function1[StrictRectangularSpace[Int], Option[StrictRectangularSpace[Int]]]
		) {
			it ("is the result of calling the leftOption parameter"){
				val res = Some(new UnescapableRectangularSpace(0))
				val src = factory( {() => res} );
				
				assertResult(res){dirFunction(src)}
			}
			it ("factory from constructor is only evaluated once"){
				val src = factory( {() => Some(new UnescapableRectangularSpace(scala.util.Random.nextInt()))} );
				
				val res = dirFunction(src)
				assertResult(res){dirFunction(src)}
			}
		}
		
		
		describe ("left") {
			directionTests(
				{x => new StrictRectangularSpaceViaFutures(1, x, noneFuture, noneFuture, noneFuture)},
				{x => x.left}
			)
		}
		describe ("up") {
			directionTests(
				{x => new StrictRectangularSpaceViaFutures(1, noneFuture, x, noneFuture, noneFuture)},
				{x => x.up}
			)
		}
		describe ("right") {
			directionTests(
				{x => new StrictRectangularSpaceViaFutures(1, noneFuture, noneFuture, x, noneFuture)},
				{x => x.right}
			)
		}
		describe ("down") {
			directionTests(
				{x => new StrictRectangularSpaceViaFutures(1, noneFuture, noneFuture, noneFuture, x)},
				{x => x.down}
			)
		}
		
		
		describe ("adjacentSpaces") {
			describe ("when all four elements are Some") {
				def adsfasdf(x:Int) = {() => Option(new UnescapableRectangularSpace(x))}
				val src = new StrictRectangularSpaceViaFutures(1, adsfasdf(34), adsfasdf(64), adsfasdf(134), adsfasdf(-134))
				
				it ("has a length of four") { assertResult(4){src.adjacentSpaces.size} }
				it ("contains the left value")  { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.left.get)  }
				it ("contains the right value") { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.right.get) }
				it ("contains the up value"   ) { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.up.get)    }
				it ("contains the down value" ) { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.down.get)  }
			}
			describe ("when all four elements are None") {
				def src = new StrictRectangularSpaceViaFutures(1, noneFuture, noneFuture, noneFuture, noneFuture)
				
				it ("has a length of zero") { assertResult(0){src.adjacentSpaces.size} }
			}
			describe ("when there is a mix of Some and None") {
				def adsfasdf(x:Int) = {() => Option(new UnescapableRectangularSpace(x))}
				val src = new StrictRectangularSpaceViaFutures(1, adsfasdf(7), adsfasdf(-345), noneFuture, noneFuture)
				
				it ("has a length equal to the count of Somes") { assertResult(2){src.adjacentSpaces.size} }
				it ("contains the left value" ) { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.left.get) }
				it ("contains the up value"   ) { assert(src.adjacentSpaces.map{x => x:StrictRectangularSpace[Int]} contains src.up.get)   }
			}
		}
	}
	
	
	
	def noneFuture = {() => None}
	final class UnescapableSpace[A](val typeOfSpace:A) extends Space[A] {
		val adjacentSpaces = Nil
		
		protected def canEquals(other:Any):Boolean = other.isInstanceOf[UnescapableSpace[_]]
		override def equals(other:Any):Boolean = {
			if (other.isInstanceOf[UnescapableSpace[_]]) {
				val other2 = other.asInstanceOf[UnescapableSpace[_]]
				other2.canEquals(this) && (other2.typeOfSpace == this.typeOfSpace)
			} else {false}
		}
		override def toString = {"UnescapableSpace(" + typeOfSpace + ")"}
		override def hashCode = typeOfSpace.hashCode * 31
	}
	final class UnescapableRectangularSpace[A](val typeOfSpace:A) extends StrictRectangularSpace[A] {
		val left  = None
		val up    = None
		val right = None
		val down  = None
		
		protected def canEquals(other:Any):Boolean = other.isInstanceOf[UnescapableRectangularSpace[_]]
		override def equals(other:Any):Boolean = {
			if (other.isInstanceOf[UnescapableRectangularSpace[_]]) {
				val other2 = other.asInstanceOf[UnescapableRectangularSpace[_]]
				other2.canEquals(this) && (other2.typeOfSpace == this.typeOfSpace)
			} else {false}
		}
		override def toString = {"UnescapableRectangularSpace(" + typeOfSpace + ")"}
		override def hashCode = typeOfSpace.hashCode * 31
	}
}
