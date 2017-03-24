package com.rayrobdod.boardGame

import org.scalatest.FunSpec

final class HorizontalHexagonalFieldTest extends FunSpec with FieldTests {
	
	singleElementField("An HorizontalHexgaonal Field containing a single space")(
		  idx = (0,0)
		, unequalIndex = (1,1)
		, clazz = "asdf"
		, generator = Field.horizontalHexagonalSpaceGenerator[String]
	)
	
	describe("A space with full adjacency") {
		val clazzes = for (
			i ← -1 to 1;
			j ← -1 to 1
		) yield { 
			(i, j) → (i, j)
		}
		val field = HorizontalHexagonalField(clazzes.toMap)
		val center = field.space(0,0).get
		
		it ("is adjacent to six spaces") {
			assertResult(6){center.adjacentSpaces.length}
		}
		it ("east is (1,0)") {
			assertResult(field.space(1,0)){center.east}
		}
		it ("west is (-1,0)") {
			assertResult(field.space(-1,0)){center.west}
		}
		it ("southeast is (0,1)") {
			assertResult(field.space(0,1)){center.southeast}
		}
		it ("northwest is (0,-1)") {
			assertResult(field.space(0,-1)){center.northwest}
		}
		it ("northeast is (1,-1)") {
			assertResult(field.space(1,-1)){center.northeast}
		}
		it ("southwest is (-1,1)") {
			assertResult(field.space(-1,1)){center.southwest}
		}
	}
	
}
