package com.rayrobdod.boardGame

import org.scalatest.FunSpec

final class ElongatedTriangularIndexTest extends FunSpec {
	import ElongatedTriangularType._
	
	describe ("ElongatedTriangularIndex") {
		describe ("toString") {
			it ("squ") {
				assertResult("(1,2,■)"){
					ElongatedTriangularIndex(1, 2, Square).toString
				}
			}
			it ("tri1") {
				assertResult("(3,4,▲)"){
					ElongatedTriangularIndex(3, 4, NorthTri).toString
				}
			}
			it ("tri2") {
				assertResult("(5,6,▼)"){
					ElongatedTriangularIndex(5, 6, SouthTri).toString
				}
			}
		}
	}
}
