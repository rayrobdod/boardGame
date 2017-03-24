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

final class RectangularRoomTest extends FunSpec
		with RoomTests
{
	
	singleElementRoom("A RectangularRoom containing a single space and no warps")(
		  idx = (0,0)
		, unequalIndex = (1,1)
		, clazz = "asdf"
		, generator = Room.rectangularSpaceGenerator[String]
	)
	
	describe("A room with one space that is surrounded by warps") {
		def targetSpaceFun(sc:String) = {() => new StrictRectangularSpaceViaFutures(sc, () => None, () => None, () => None, () => None)}
		
		val field = RectangularRoom(
			Map(
				(0,0) -> "center"
			),
			Map(
				(1,0) -> targetSpaceFun("east"),
				(-1,0) -> targetSpaceFun("west"),
				(0,-1) -> targetSpaceFun("north"),
				(0,1) -> targetSpaceFun("south")
			)
		)
		val center = field.space((0,0)).get
		
		it ("center.north warps to north") {
			assertResult("north"){center.north.get.typeOfSpace}
		}
		it ("center.south warps to south") {
			assertResult("south"){center.south.get.typeOfSpace}
		}
		it ("center.east warps to east") {
			assertResult("east"){center.east.get.typeOfSpace}
		}
		it ("center.west warps to west") {
			assertResult("west"){center.west.get.typeOfSpace}
		}
	}
	
	describe("A room with one space that is surrounded by local spaces") {
		
		val field = RectangularRoom(
			Map(
				(0,0) -> "center",
				(1,0) -> "east",
				(-1,0) -> "west",
				(0,-1) -> "north",
				(0,1) -> "south"
			),
			Map.empty[RectangularIndex, Function0[StrictRectangularSpace[String]]]
		)
		val center = field.space((0,0)).get
		
		it ("center.north warps to north") {
			assertResult("north"){center.north.get.typeOfSpace}
		}
		it ("center.south warps to south") {
			assertResult("south"){center.south.get.typeOfSpace}
		}
		it ("center.east warps to east") {
			assertResult("east"){center.east.get.typeOfSpace}
		}
		it ("center.west warps to west") {
			assertResult("west"){center.west.get.typeOfSpace}
		}
	}
	
	describe("A room with one space that is surrounded by local spaces (seq[seq] constructor)") {
		
		val field = RectangularRoom(
			Seq(
				Seq("nw", "north", "ne"),
				Seq("west", "center", "east"),
				Seq("sw", "south", "se")
			),
			Map.empty[RectangularIndex, Function0[StrictRectangularSpace[String]]]
		)
		val center = field.space((1,1)).get
		
		it ("center.north warps to north") {
			assertResult("north"){center.north.get.typeOfSpace}
		}
		it ("center.south warps to south") {
			assertResult("south"){center.south.get.typeOfSpace}
		}
		it ("center.east warps to east") {
			assertResult("east"){center.east.get.typeOfSpace}
		}
		it ("center.west warps to west") {
			assertResult("west"){center.west.get.typeOfSpace}
		}
	}
	
}
