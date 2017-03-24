package com.rayrobdod.boardGame

import org.scalatest.FunSpec

trait FieldTests { this: FunSpec =>
	
	def singleElementField[SpaceClass, Index, SpaceType <: Space[SpaceClass, SpaceType]](
		name:String
	)(	idx:Index,
		unequalIndex:Index,
		clazz:SpaceClass,
		generator:Field.SpaceGenerator[SpaceClass, Index, SpaceType]
	) {
		describe(name) {
			val dut = new Field(Map(idx -> clazz))(generator)
			
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
				it ("hashcode is consistent") {
					assert(
						new Field( Map(idx -> clazz) )( generator ).hashCode ==
						new Field( Map(idx -> clazz) )( generator ).hashCode
					)
				}
				it ("equals (same)") {
					assert(
						new Field( Map(idx -> clazz) )( generator ) ==
						new Field( Map(idx -> clazz) )( generator )
					)
				}
				it ("equals (not)") {
					assert(
						new Field( Map(unequalIndex -> clazz) )( generator ) !=
						new Field( Map(idx -> clazz) )( generator )
					)
				}
				it ("equals (not 2)") {
					assert(new Field( Map(idx -> clazz) )( generator ) != "apple")
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