package com.rayrobdod.boardGame

import org.scalatest.FunSpec

final class RoomSpaceGeneratorTest extends FunSpec {
	
	val square1 = Room.rectangularSpaceGenerator[Float]
	val square2 = Room.rectangularSpaceGenerator[Long]
	val horizhex1 = Room.horizontalHexagonalSpaceGenerator[Float]
	val horizhex2 = Room.horizontalHexagonalSpaceGenerator[Long]
	
	describe ("Room.rectangularSpaceGenerator") {
		it ("are equal to same") {
			assertResult(square1)(square2)
		}
		it ("have same hashcode") {
			assertResult(square1.hashCode)(square2.hashCode)
		}
		it ("are not equal to different") {
			assert(square1 != horizhex1)
		}
	}
	describe ("Room.horizontalHexagonalSpaceGenerator") {
		it ("are equal to same") {
			assertResult(horizhex1)(horizhex2)
		}
		it ("have same hashcode") {
			assertResult(horizhex1.hashCode)(horizhex2.hashCode)
		}
		it ("are not equal to different") {
			assert(horizhex1 != square1)
		}
	}
}
