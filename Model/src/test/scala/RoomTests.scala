package com.rayrobdod.boardGame

import org.scalatest.FunSpec

trait RoomTests { this: FunSpec =>
	
	def singleElementRoom[SpaceClass, Index, SpaceType <: Space[SpaceClass, SpaceType]](
		name:String
	)(	idx:Index,
		unequalIndex:Index,
		clazz:SpaceClass,
		generator:Room.SpaceGenerator[SpaceClass, Index, SpaceType]
	) {
		describe(name) {
			val emptyWarps = Map.empty[Index, Function0[SpaceType]]
			val dut = new Room(Map(idx -> clazz), emptyWarps)(generator)
			
			describe("the field") {
				it ("spaceClass at `idx` is `clazz`") {
					assertResult(Some(clazz)){dut.spaceClass( idx )}
				}
				it ("spaceClass at (1,1) is None") {
					assertResult(None){dut.spaceClass( unequalIndex )}
				}
				it ("space at (1,1) is None") {
					assertResult(None){dut.space( unequalIndex )}
				}
				it ("mapIndex has one value") {
					assertResult(Seq(idx)){dut.mapIndex{x => x}}
				}
				it ("foreachIndex has one value") {
					dut.foreachIndex{x =>
						assertResult(idx){x}
					}
				}
			}
			
			describe("the space") {
				val space = dut.space(idx).get
				
				it ("has no adjacent spaces") {
					assertResult(Seq.empty){space.adjacentSpaces}
				}
				it ("type of space sdffafs") {
					assertResult(clazz){space.typeOfSpace}
				}
				it ("equals itself") {
					assert(space == space)
				}
				it ("equals is stable") {
					assert(dut.space(idx).get == dut.space(idx).get)
				}
				it ("not equals a string") {
					assert(space != " ")
				}
				it ("hash is consistent") {
					assert(dut.space(idx).get.hashCode == dut.space(idx).get.hashCode)
				}
				it ("has a toString method") {
					// no idea how to test it though…
					space.toString
				}
			}
		}
	}
}