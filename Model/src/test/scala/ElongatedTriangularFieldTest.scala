package com.rayrobdod.boardGame

import org.scalatest.FunSpec

final class ElongatedTriangularFieldTest extends FunSpec
		with FieldTests
{
	
	singleElementField("An ElongatedTriangularField containing a single square space")(
		  idx = ElongatedTriangularIndex(0,0,ElongatedTriangularType.Square)
		, unequalIndex = ElongatedTriangularIndex(0,1,ElongatedTriangularType.Square)
		, clazz = "123"
		, generator = Field.elongatedTriangularSpaceGenerator[String]
	)
	singleElementField("An ElongatedTriangularField containing a single NorthTriangular space")(
		  idx = ElongatedTriangularIndex(0,0,ElongatedTriangularType.NorthTri)
		, unequalIndex = ElongatedTriangularIndex(0,1,ElongatedTriangularType.Square)
		, clazz = "456"
		, generator = Field.elongatedTriangularSpaceGenerator[String]
	)
	singleElementField("An ElongatedTriangularField containing a single SouthTriangular space")(
		  idx = ElongatedTriangularIndex(0,0,ElongatedTriangularType.SouthTri)
		, unequalIndex = ElongatedTriangularIndex(0,1,ElongatedTriangularType.Square)
		, clazz = "456"
		, generator = Field.elongatedTriangularSpaceGenerator[String]
	)
	
	describe ("Spaces with full adjacency") {
		val clazzes = for (
			i ← -1 to 2;
			j ← -1 to 2;
			t ← ElongatedTriangularType.values
		) yield {
			ElongatedTriangularIndex(i, j, t) -> s"qwerty"
		}
		val field = ElongatedTriangularField(clazzes.toMap)
		
		describe ("the square one") {
			val index = ElongatedTriangularIndex(0, 0, ElongatedTriangularType.Square)
			val center = field.space(index).get.asInstanceOf[StrictElongatedTriangularSpace.Square[String]]
			
			it ("is adjacent to four spaces") {
				assertResult(4){center.adjacentSpaces.length}
			}
			it ("east is (1,0,square)") {
				assertResult(field.space( index.copy(x = 1) )){center.east}
			}
			it ("west is (-1,0,square)") {
				assertResult(field.space( index.copy(x = -1) )){center.west}
			}
			it ("north is (0,0,NorthTri)") {
				assertResult(field.space( index.copy(typ = ElongatedTriangularType.NorthTri) )){center.north}
			}
			it ("south is (0,0,SouthTri)") {
				assertResult(field.space( index.copy(typ = ElongatedTriangularType.SouthTri) )){center.south}
			}
		}
		describe ("the north tri one (even y)") {
			val index = ElongatedTriangularIndex(0, 0, ElongatedTriangularType.NorthTri)
			val center = field.space(index).get.asInstanceOf[StrictElongatedTriangularSpace.Triangle1[String]]
			
			it ("is adjacent to three spaces") {
				assertResult(3){center.adjacentSpaces.length}
			}
			it ("northeast is (-1,-1,southTri)") {
				assertResult(field.space( ElongatedTriangularIndex(-1, -1, ElongatedTriangularType.SouthTri) )){center.northEast}
			}
			it ("northwest is (0,-1,southTri)") {
				assertResult(field.space( ElongatedTriangularIndex(0, -1, ElongatedTriangularType.SouthTri) )){center.northWest}
			}
			it ("south is (0,0,Square)") {
				assertResult(field.space( index.copy(typ = ElongatedTriangularType.Square) )){center.south}
			}
			
			it ("this.northwest.southeast == this") {
				assertResult(center){center.northWest.get.southEast.get}
			}
			it ("this.northeast.southwest == this") {
				assertResult(center){center.northWest.get.southEast.get}
			}
		}
		describe ("the north tri one (odd y)") {
			val index = ElongatedTriangularIndex(0, 1, ElongatedTriangularType.NorthTri)
			val center = field.space(index).get.asInstanceOf[StrictElongatedTriangularSpace.Triangle1[String]]
			
			it ("is adjacent to three spaces") {
				assertResult(3){center.adjacentSpaces.length}
			}
			it ("northeast is (0,-1,southTri)") {
				assertResult(field.space( ElongatedTriangularIndex(0, 0, ElongatedTriangularType.SouthTri) )){center.northEast}
			}
			it ("northwest is (1,-1,southTri)") {
				assertResult(field.space( ElongatedTriangularIndex(1, 0, ElongatedTriangularType.SouthTri) )){center.northWest}
			}
			it ("south is (0,0,Square)") {
				assertResult(field.space( index.copy(typ = ElongatedTriangularType.Square) )){center.south}
			}
			
			it ("this.northwest.southeast == this") {
				assertResult(center){center.northWest.get.southEast.get}
			}
			it ("this.northeast.southwest == this") {
				assertResult(center){center.northWest.get.southEast.get}
			}
		}
		describe ("the south tri one (even y)") {
			val index = ElongatedTriangularIndex(0, 0, ElongatedTriangularType.SouthTri)
			val center = field.space(index).get.asInstanceOf[StrictElongatedTriangularSpace.Triangle2[String]]
			
			it ("is adjacent to three spaces") {
				assertResult(3){center.adjacentSpaces.length}
			}
			it ("southEast is (-1,1,northTri)") {
				assertResult(field.space( ElongatedTriangularIndex(-1, 1, ElongatedTriangularType.NorthTri) )){center.southEast}
			}
			it ("southWest is (0,1,northTri)") {
				assertResult(field.space( ElongatedTriangularIndex(0, 1, ElongatedTriangularType.NorthTri) )){center.southWest}
			}
			it ("north is (0,0,Square)") {
				assertResult(field.space( index.copy(typ = ElongatedTriangularType.Square) )){center.north}
			}
			
			it ("this.southwest.northeast == this") {
				assertResult(center){center.southWest.get.northEast.get}
			}
			it ("this.southeast.northwest == this") {
				assertResult(center){center.southWest.get.northEast.get}
			}
		}
		describe ("the south tri one (odd y)") {
			val index = ElongatedTriangularIndex(0, 1, ElongatedTriangularType.SouthTri)
			val center = field.space(index).get.asInstanceOf[StrictElongatedTriangularSpace.Triangle2[String]]
			
			it ("is adjacent to three spaces") {
				assertResult(3){center.adjacentSpaces.length}
			}
			it ("southEast is (0,1,southTri)") {
				assertResult(field.space( ElongatedTriangularIndex(0, 2, ElongatedTriangularType.NorthTri) )){center.southEast}
			}
			it ("southWest is (1,1,southTri)") {
				assertResult(field.space( ElongatedTriangularIndex(1, 2, ElongatedTriangularType.NorthTri) )){center.southWest}
			}
			it ("north is (0,0,Square)") {
				assertResult(field.space( index.copy(typ = ElongatedTriangularType.Square) )){center.north}
			}
			
			it ("this.southwest.northeast == this") {
				assertResult(center){center.southWest.get.northEast.get}
			}
			it ("this.southeast.northwest == this") {
				assertResult(center){center.southWest.get.northEast.get}
			}
		}
		
	}
}
