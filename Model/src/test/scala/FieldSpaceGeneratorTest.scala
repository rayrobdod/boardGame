package com.rayrobdod.boardGame

import org.scalatest.FunSpec

final class FieldSpaceGeneratorTest extends FunSpec {
	
	val square1 = Field.rectangularSpaceGenerator[Float]
	val square2 = Field.rectangularSpaceGenerator[Long]
	val horizhex1 = Field.horizontalHexagonalSpaceGenerator[Float]
	val horizhex2 = Field.horizontalHexagonalSpaceGenerator[Long]
	val elotri1 = Field.elongatedTriangularSpaceGenerator[Float]
	val elotri2 = Field.elongatedTriangularSpaceGenerator[Long]
	
	describe ("Field.rectangularSpaceGenerator") {
		it ("are equal to same") {
			assertResult(square1)(square2)
		}
		it ("have same hashcode") {
			assertResult(square1.hashCode)(square2.hashCode)
		}
		it ("are not equal to different") {
			assert(square1 != elotri1)
		}
	}
	describe ("Field.horizontalHexagonalSpaceGenerator") {
		it ("are equal to same") {
			assertResult(horizhex1)(horizhex2)
		}
		it ("have same hashcode") {
			assertResult(horizhex1.hashCode)(horizhex2.hashCode)
		}
		it ("are not equal to different") {
			assert(horizhex1 != elotri1)
		}
	}
	describe ("Field.elongatedTriangularSpaceGenerator") {
		it ("are equal to same") {
			assertResult(elotri1)(elotri2)
		}
		it ("have same hashcode") {
			assertResult(elotri1.hashCode)(elotri2.hashCode)
		}
		it ("are not equal to different") {
			assert(elotri1 != horizhex2)
		}
	}
}
