package com.rayrobdod.boardGame

import org.scalatest.FunSpec
import scala.collection.immutable.Seq

final class HorizontalHexagonalRoomTest extends FunSpec {
	
	describe ("a Room containing a single space") {
		val dut = HorizontalHexagonalRoom( Map((0,0) -> " "), Map.empty )
		
		describe("the Room") {
			it ("spaceClass at (0,0) is ' '") {
				assertResult(Some(" ")){dut.spaceClass( (0,0) )}
			}
			it ("spaceClass at (1,1) is None") {
				assertResult(None){dut.spaceClass( (1,1) )}
			}
			it ("space at (1,1) is None") {
				assertResult(None){dut.space( (1,1) )}
			}
			it ("mapIndex has one value") {
				assertResult(Seq((0,0))){dut.mapIndex{x => x}}
			}
			it ("foreachIndex has one value") {
				dut.foreachIndex{x =>
					assertResult((0,0)){x}
				}
			}
		}
		
		describe("the space") {
			val space = dut.space((0,0)).get
			
			it ("has no adjacent spaces") {
				assertResult(Seq.empty){space.adjacentSpaces}
			}
			it ("type of space sdffafs") {
				assertResult(" "){space.typeOfSpace}
			}
			it ("equals itself") {
				assert(space == space)
			}
			it ("not equals a string") {
				assert(space != " ")
			}
			it ("hashes to 0") {
				assertResult(0){space.hashCode}
			}
			it ("toString") {
				assertResult(
					s"HorizontalHexagonalRoom.Space(typ =  , ew = 0, nwse = 0, field = $dut)"
				) {
					space.toString
				}
			}
		}
	}
}
