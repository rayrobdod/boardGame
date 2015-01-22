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
		describe ("left") {
			it ("is the result of calling the leftOption parameter"){
				val res = Some(new UnescapableSpace("a"))
				val src = new RectangularSpaceViaFutures("b", {() => res}, noneFuture, noneFuture, noneFuture)
				
				assertResult(res){src.left}
			}
			it ("leftOption is only evaluated once"){
				val src = new RectangularSpaceViaFutures(0, {() => Some(new UnescapableSpace(scala.util.Random.nextInt()))}, noneFuture, noneFuture, noneFuture)
				
				val res = src.left
				assertResult(res){src.left}
			}
		}
		describe ("up") {
			it ("is the result of calling the upOption parameter"){
				val res = Some(new UnescapableSpace("a"))
				val src = new RectangularSpaceViaFutures("b", noneFuture, {() => res}, noneFuture, noneFuture)
				
				assertResult(res){src.up}
			}
			it ("upOption is only evaluated once"){
				val src = new RectangularSpaceViaFutures(0, noneFuture, {() => Some(new UnescapableSpace(scala.util.Random.nextInt()))}, noneFuture, noneFuture)
				
				val res = src.up
				assertResult(res){src.up}
			}
		}
		describe ("right") {
			it ("is the result of calling the rightOption parameter"){
				val res = Some(new UnescapableSpace("a"))
				val src = new RectangularSpaceViaFutures("b", noneFuture, noneFuture, {() => res}, noneFuture)
				
				assertResult(res){src.right}
			}
			it ("rightOption is only evaluated once"){
				val src = new RectangularSpaceViaFutures(0, noneFuture, noneFuture, {() => Some(new UnescapableSpace(scala.util.Random.nextInt()))}, noneFuture)
				
				val res = src.right
				assertResult(res){src.right}
			}
		}
		describe ("down") {
			it ("is the result of calling the downOption parameter"){
				val res = Some(new UnescapableSpace("a"))
				val src = new RectangularSpaceViaFutures("b", noneFuture, noneFuture, noneFuture, {() => res})
				
				assertResult(res){src.down}
			}
			it ("downOption is only evaluated once"){
				val src = new RectangularSpaceViaFutures(0, noneFuture, noneFuture, noneFuture, {() => Some(new UnescapableSpace(scala.util.Random.nextInt()))})
				
				val res = src.down
				assertResult(res){src.down}
			}
		}
	}
	
	describe ("StrictRectangularSpaceViaFutures") {
		describe ("left") {
			it ("is the result of calling the leftOption parameter"){
				val res = Some(new UnescapableRectangularSpace("a"))
				val src = new StrictRectangularSpaceViaFutures("b", {() => res}, noneFuture, noneFuture, noneFuture)
				
				assertResult(res){src.left}
			}
			it ("leftOption is only evaluated once"){
				val src = new StrictRectangularSpaceViaFutures(0, {() => Some(new UnescapableRectangularSpace(scala.util.Random.nextInt()))}, noneFuture, noneFuture, noneFuture)
				
				val res = src.left
				assertResult(res){src.left}
			}
		}
		describe ("up") {
			it ("is the result of calling the upOption parameter"){
				val res = Some(new UnescapableRectangularSpace("a"))
				val src = new StrictRectangularSpaceViaFutures("b", noneFuture, {() => res}, noneFuture, noneFuture)
				
				assertResult(res){src.up}
			}
			it ("upOption is only evaluated once"){
				val src = new StrictRectangularSpaceViaFutures(0, noneFuture, {() => Some(new UnescapableRectangularSpace(scala.util.Random.nextInt()))}, noneFuture, noneFuture)
				
				val res = src.up
				assertResult(res){src.up}
			}
		}
		describe ("right") {
			it ("is the result of calling the rightOption parameter"){
				val res = Some(new UnescapableRectangularSpace("a"))
				val src = new StrictRectangularSpaceViaFutures("b", noneFuture, noneFuture, {() => res}, noneFuture)
				
				assertResult(res){src.right}
			}
			it ("rightOption is only evaluated once"){
				val src = new StrictRectangularSpaceViaFutures(0, noneFuture, noneFuture, {() => Some(new UnescapableRectangularSpace(scala.util.Random.nextInt()))}, noneFuture)
				
				val res = src.right
				assertResult(res){src.right}
			}
		}
		describe ("down") {
			it ("is the result of calling the downOption parameter"){
				val res = Some(new UnescapableRectangularSpace("a"))
				val src = new StrictRectangularSpaceViaFutures("b", noneFuture, noneFuture, noneFuture, {() => res})
				
				assertResult(res){src.down}
			}
			it ("downOption is only evaluated once"){
				val src = new StrictRectangularSpaceViaFutures(0, noneFuture, noneFuture, noneFuture, {() => Some(new UnescapableRectangularSpace(scala.util.Random.nextInt()))})
				
				val res = src.down
				assertResult(res){src.down}
			}
		}
	}
	
	
	
	val noneFuture = {() => None}
	final class UnescapableSpace[A](val typeOfSpace:A) extends Space[A] {
		val adjacentSpaces = Nil
		
		protected def canEquals(other:Any):Boolean = other.isInstanceOf[UnescapableSpace[_]]
		override def equals(other:Any):Boolean = {
			if (other.isInstanceOf[UnescapableSpace[_]]) {
				val other2 = other.asInstanceOf[UnescapableSpace[_]]
				other2.canEquals(this) && (other2.typeOfSpace == this.typeOfSpace)
			} else {false}
		}
	}
	final class UnescapableRectangularSpace[A](val typeOfSpace:A) extends StrictRectangularSpace[A] {
		val left  = None
		val up    = None
		val right = None
		val down  = None
		
		protected def canEquals(other:Any):Boolean = other.isInstanceOf[UnescapableSpace[_]]
		override def equals(other:Any):Boolean = {
			if (other.isInstanceOf[UnescapableRectangularSpace[_]]) {
				val other2 = other.asInstanceOf[UnescapableRectangularSpace[_]]
				other2.canEquals(this) && (other2.typeOfSpace == this.typeOfSpace)
			} else {false}
		}
	}
}
