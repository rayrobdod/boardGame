package com.rayrobdod.boardGame

import org.scalatest.FunSpec

final class ElongatedTriangularSpaceTest extends FunSpec {
	import ElongatedTriangularSpaceTest._
	
	describe ("StrictElongatedTriangularSpace.Square") {
		describe ("fold") {
			it ("calls the second function") {
				assert( IsolatedSquare.fold({x => false}, {x => true}, {x => false}) )
			}
		}
	}
	describe ("StrictElongatedTriangularSpace.Triangle1") {
		describe ("fold") {
			it ("calls the second function") {
				assert( IsolatedTriangle1.fold({x => true}, {x => false}, {x => false}) )
			}
		}
	}
	describe ("StrictElongatedTriangularSpace.Triangle2") {
		describe ("fold") {
			it ("calls the second function") {
				assert( IsolatedTriangle2.fold({x => false}, {x => false}, {x => true}) )
			}
		}
	}
}

object ElongatedTriangularSpaceTest {

	object IsolatedSquare extends StrictElongatedTriangularSpace.Square[None.type] {
		override def typeOfSpace = None
		override def north = None
		override def south = None
		override def east = None
		override def west = None
	}
	object IsolatedTriangle1 extends StrictElongatedTriangularSpace.Triangle1[None.type] {
		override def typeOfSpace = None
		override def south = None
		override def northEast = None
		override def northWest = None
	}
	object IsolatedTriangle2 extends StrictElongatedTriangularSpace.Triangle2[None.type] {
		override def typeOfSpace = None
		override def north = None
		override def southEast = None
		override def southWest = None
	}
	
}
