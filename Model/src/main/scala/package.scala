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
 * The classes which consist the "model", as opposed to the "view"
 */
package object boardGame {
	type RectangularIndex = Tuple2[Int, Int]
	type HorizontalHexagonalIndex = Tuple2[Int, Int]
	
	/** A Tiling of RectangularSpaces */
	type RectangularTiling[SpaceClass] = Tiling[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]]
	/** A Tiling of HorizontalHexagonalSpaces */
	type HorizontalHexagonalTiling[SpaceClass] = Tiling[SpaceClass, HorizontalHexagonalIndex, StrictHorizontalHexagonalSpace[SpaceClass]]
	/** A Tiling of ElongatedTriangularSpaces */
	type ElongatedTriangularTiling[SpaceClass] = Tiling[SpaceClass, ElongatedTriangularIndex, StrictElongatedTriangularSpace[SpaceClass]]
	
	/** A Field of RectangularSpaces */
	type RectangularField[SpaceClass] = Field[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]]
	/** A Field of HorizontalHexagonalSpaces */
	type HorizontalHexagonalField[SpaceClass] = Field[SpaceClass, HorizontalHexagonalIndex, StrictHorizontalHexagonalSpace[SpaceClass]]
	/** A Field of ElongatedTriangularSpaces */
	type ElongatedTriangularField[SpaceClass] = Field[SpaceClass, ElongatedTriangularIndex, StrictElongatedTriangularSpace[SpaceClass]]
	
	/**
	 * Create a Field of RectangularSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 */
	def RectangularField[SpaceClass](
		classes:Map[RectangularIndex, SpaceClass]
	):RectangularField[SpaceClass] = {
		new Field(classes)
	}
	
	/**
	 * Create a Field of RectangularSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 */
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
	
	/**
	 * Create a Field of HorizontalHexagonalSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 */
	def HorizontalHexagonalField[SpaceClass](
		classes:Map[HorizontalHexagonalIndex, SpaceClass]
	):HorizontalHexagonalField[SpaceClass] = {
		new Field(classes)
	}
	
	/**
	 * Create a Field of ElongatedTriangularSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 */
	def ElongatedTriangularField[SpaceClass](
		classes:Map[ElongatedTriangularIndex, SpaceClass]
	):ElongatedTriangularField[SpaceClass] = {
		new Field(classes)
	}
	
	/**
	 * Create a Room of RectangularSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 */
	def RectangularRoom[SpaceClass](
		classes:Map[RectangularIndex, SpaceClass],
		warps:Map[RectangularIndex, Function0[StrictRectangularSpace[SpaceClass]]]
	):RectangularTiling[SpaceClass] = {
		new Room(classes, warps)
	}
	
	/**
	 * Create a Room of RectangularSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 */
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
	
	/**
	 * Create a Room of HorizontalHexagonalSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 */
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
		
		val values:Seq[ElongatedTriangularType] = Seq(
			Square, NorthTri, SouthTri
		)
	}
}
