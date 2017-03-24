package com.rayrobdod.boardGame

import org.scalatest.FunSpec

final class HorizontalHexagonalRoomTest extends FunSpec
		with RoomTests
{
	
	singleElementRoom("A Room containing a single space and no warps")(
		  idx = (0,0)
		, unequalIndex = (1,1)
		, clazz = " "
		, generator = Room.horizontalHexagonalSpaceGenerator[String]
	)
	
	describe("A room with one space that is surrounded by warps") {
		def targetSpaceFun(sc:String) = {() => new NoAdjacentsHorizHexSpace(sc)}
		
		val field = HorizontalHexagonalRoom(
			Map(
				(0,0) -> "center"
			),
			Map(
				(1,0) -> targetSpaceFun("east"),
				(-1,0) -> targetSpaceFun("west"),
				(0,-1) -> targetSpaceFun("northwest"),
				(0,1) -> targetSpaceFun("southeast"),
				(1,-1) -> targetSpaceFun("northeast"),
				(-1,1) -> targetSpaceFun("southwest")
			)
		)
		val center = field.space((0,0)).get
		
		it ("center.northwest warps to northwest") {
			assertResult("northwest"){center.northwest.get.typeOfSpace}
		}
		it ("center.southwest warps to southwest") {
			assertResult("southwest"){center.southwest.get.typeOfSpace}
		}
		it ("center.northeast warps to northeast") {
			assertResult("northeast"){center.northeast.get.typeOfSpace}
		}
		it ("center.southeast warps to southeast") {
			assertResult("southeast"){center.southeast.get.typeOfSpace}
		}
		it ("center.east warps to east") {
			assertResult("east"){center.east.get.typeOfSpace}
		}
		it ("center.west warps to west") {
			assertResult("west"){center.west.get.typeOfSpace}
		}
	}
	
	describe("A room with one space that is surrounded by local spaces") {
		
		val field = HorizontalHexagonalRoom(
			Map(
				(0,0) -> "center",
				(1,0) -> "east",
				(-1,0) -> "west",
				(0,-1) -> "northwest",
				(0,1) -> "southeast",
				(1,-1) -> "northeast",
				(-1,1) -> "southwest"
			),
			Map.empty[RectangularIndex, Function0[StrictHorizontalHexagonalSpace[String]]]
		)
		val center = field.space((0,0)).get
		
		it ("center.northwest warps to northwest") {
			assertResult("northwest"){center.northwest.get.typeOfSpace}
		}
		it ("center.southwest warps to southwest") {
			assertResult("southwest"){center.southwest.get.typeOfSpace}
		}
		it ("center.northeast warps to northeast") {
			assertResult("northeast"){center.northeast.get.typeOfSpace}
		}
		it ("center.southeast warps to southeast") {
			assertResult("southeast"){center.southeast.get.typeOfSpace}
		}
		it ("center.east warps to east") {
			assertResult("east"){center.east.get.typeOfSpace}
		}
		it ("center.west warps to west") {
			assertResult("west"){center.west.get.typeOfSpace}
		}
	}
	
	
	private final class NoAdjacentsHorizHexSpace(override val typeOfSpace:String) extends StrictHorizontalHexagonalSpace[String] {
		override def northwest:Option[Nothing] = None
		override def northeast:Option[Nothing] = None
		override def southwest:Option[Nothing] = None
		override def southeast:Option[Nothing] = None
		override def east:Option[Nothing] = None
		override def west:Option[Nothing] = None
	}
}
