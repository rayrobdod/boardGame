/*
	Deduction Tactics
	Copyright (C) 2012-2015  Raymond Dodge

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.rayrobdod

/**
 * 
 */
package object boardGame {
	type RectangularIndex = Tuple2[Int, Int]
	type HorizontalHexagonalIndex = Tuple2[Int, Int]
	
	type RectangularTiling[SpaceClass] = Tiling[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]]
	type HorizontalHexagonalTiling[SpaceClass] = Tiling[SpaceClass, HorizontalHexagonalIndex, StrictHorizontalHexagonalSpace[SpaceClass]]
	type ElongatedTriangularTiling[SpaceClass] = Tiling[SpaceClass, ElongatedTriangularIndex, StrictElongatedTriangularSpace[SpaceClass]]
	
	type RectangularField[SpaceClass] = Field[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]]
	type HorizontalHexagonalField[SpaceClass] = Field[SpaceClass, HorizontalHexagonalIndex, StrictHorizontalHexagonalSpace[SpaceClass]]
	type ElongatedTriangularField[SpaceClass] = Field[SpaceClass, ElongatedTriangularIndex, StrictElongatedTriangularSpace[SpaceClass]]
	
	def RectangularField[SpaceClass](
		classes:Map[RectangularIndex, SpaceClass]
	):RectangularField[SpaceClass] = {
		new Field(classes)
	}
	
	def RectangularField[SpaceClass](
		classes:Seq[Seq[SpaceClass]]
	):RectangularField[SpaceClass] = {
		val remap = for (
			(clss, j) <- classes.zipWithIndex;
			(cls, i) <- clss.zipWithIndex
		) yield {
			(i, j) -> cls
		}
		RectangularField(remap.toMap)
	}
	
	def HorizontalHexagonalField[SpaceClass](
		classes:Map[HorizontalHexagonalIndex, SpaceClass]
	):HorizontalHexagonalField[SpaceClass] = {
		new Field(classes)
	}
	
	def ElongatedTriangularField[SpaceClass](
		classes:Map[ElongatedTriangularIndex, SpaceClass]
	):ElongatedTriangularField[SpaceClass] = {
		new Field(classes)
	}
	
	def RectangularRoom[SpaceClass](
		classes:Map[RectangularIndex, SpaceClass],
		warps:Map[RectangularIndex, Function0[StrictRectangularSpace[SpaceClass]]]
	):RectangularTiling[SpaceClass] = {
		new Room(classes, warps)
	}
	
	def RectangularRoom[SpaceClass](
		classes:Seq[Seq[SpaceClass]],
		warps:Map[RectangularIndex, Function0[StrictRectangularSpace[SpaceClass]]]
	):RectangularTiling[SpaceClass] = {
		val remap = for (
			(clss, j) <- classes.zipWithIndex;
			(cls, i) <- clss.zipWithIndex
		) yield {
			(i, j) -> cls
		}
		RectangularRoom(remap.toMap, warps)
	}
	
	def HorizontalHexagonalRoom[SpaceClass](
		classes:Map[HorizontalHexagonalIndex, SpaceClass],
		warps:Map[HorizontalHexagonalIndex, Function0[StrictHorizontalHexagonalSpace[SpaceClass]]]
	):HorizontalHexagonalTiling[SpaceClass] = {
		new Room(classes, warps)
	}
	
	/**
	 * A SpaceClassMatcher that always returns true
	 * @version next
	 */
	val ConstTrueSpaceClassMatcher:SpaceClassMatcher[Any] = new ConstSpaceClassMatcher(true)
	
	/**
	 * A SpaceClassMatcher that always returns false
	 * @version next
	 */
	val ConstFalseSpaceClassMatcher:SpaceClassMatcher[Any] = new ConstSpaceClassMatcher(false)
	
	/** A CostFunction with a constant (1) cost for every move. */
	val constantCostFunction:CostFunction[Any] = new CostFunction[Any]{def apply(from:Any, to:Any):Int = 1}
}

package boardGame {
	
	/**
	 * A function that defines the 'cost' of moving from one space to the second space, under the assumption that two spaces are adjacent
	 */
	trait CostFunction[-A] {
		def apply(from:A, to:A):Int
	}
	
	/** A boolean match against a class */
	trait SpaceClassMatcher[-SpaceClass] {
		/** Returns true if the provided space class fits the requirements of this matcher */
		def unapply(sc:SpaceClass):Boolean
	}
	
	/**
	 * A SpaceClassMatcher that always returns the specified value
	 * @since next
	 */
	private[this] final class ConstSpaceClassMatcher(value:Boolean) extends SpaceClassMatcher[Any] {
		def unapply(sc:Any):Boolean = value
	}
	
	
	final case class ElongatedTriangularIndex(
		x:Int,
		y:Int,
		typ:ElongatedTriangularType
	)
	
	sealed trait ElongatedTriangularType
	object ElongatedTriangularType {
		final object Square extends ElongatedTriangularType
		final object NorthTri extends ElongatedTriangularType
		final object SouthTri extends ElongatedTriangularType
	}
}
