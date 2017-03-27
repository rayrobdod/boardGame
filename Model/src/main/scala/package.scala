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

import scala.collection.immutable.Seq

/**
 * The classes which consist the "model", as opposed to the "view"
 * 
 * @groupprio Generic 100
 * @groupdesc Generic classes that work on shapes of any shape
 * @groupprio SpaceLike 200
 * @groupdesc SpaceLike spaces which have a shape but don't commit on a `Repr`
 * @groupprio Rectangular 900
 * @groupdesc Rectangular Rectangular Tiling-related classes
 * @groupprio HorizontalHexagonal 901
 * @groupdesc HorizontalHexagonal HorizontalHexagonal Tiling-related classes
 * @groupprio ElongatedTriangular 902
 * @groupdesc ElongatedTriangular ElongatedTriangular Tiling-related classes
 * @groupprio Unidirectional 903
 * @groupdesc Unidirectional Unidirectional Space classes
 */
package object boardGame {
	/**
	 * The type of index used for Rectangular fields
	 * @group Rectangular
	 */
	type RectangularIndex = Tuple2[Int, Int]
	/**
	 * The type of index used for HorizontalHexagonal fields
	 * @group HorizontalHexagonal
	 */
	type HorizontalHexagonalIndex = Tuple2[Int, Int]
	
	/**
	 * A Tiling of RectangularSpaces
	 * @documentable
	 * @group Rectangular
	 */
	type RectangularTiling[SpaceClass] = Tiling[SpaceClass, RectangularIndex, RectangularSpace[SpaceClass]]
	/**
	 * A Tiling of HorizontalHexagonalSpaces
	 * @documentable
	 * @group HorizontalHexagonal
	 */
	type HorizontalHexagonalTiling[SpaceClass] = Tiling[SpaceClass, HorizontalHexagonalIndex, HorizontalHexagonalSpace[SpaceClass]]
	/**
	 * A Tiling of ElongatedTriangularSpaces
	 * @documentable
	 * @group ElongatedTriangular
	 */
	type ElongatedTriangularTiling[SpaceClass] = Tiling[SpaceClass, ElongatedTriangularIndex, ElongatedTriangularSpace[SpaceClass]]
	
	/**
	 * A Field of RectangularSpaces
	 * @documentable
	 * @group Rectangular
	 */
	type RectangularField[SpaceClass] = Field[SpaceClass, RectangularIndex, RectangularSpace[SpaceClass]]
	/**
	 * A Field of HorizontalHexagonalSpaces
	 * @documentable
	 * @group HorizontalHexagonal
	 */
	type HorizontalHexagonalField[SpaceClass] = Field[SpaceClass, HorizontalHexagonalIndex, HorizontalHexagonalSpace[SpaceClass]]
	/**
	 * A Field of ElongatedTriangularSpaces
	 * @documentable
	 * @group ElongatedTriangular
	 */
	type ElongatedTriangularField[SpaceClass] = Field[SpaceClass, ElongatedTriangularIndex, ElongatedTriangularSpace[SpaceClass]]
	
	/**
	 * Create a Field of RectangularSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 * @group Rectangular
	 */
	def RectangularField[SpaceClass](
		classes:Map[RectangularIndex, SpaceClass]
	):RectangularField[SpaceClass] = {
		new Field(classes)
	}
	
	/**
	 * Create a Field of RectangularSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 * @group Rectangular
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
	 * @group HorizontalHexagonal
	 */
	def HorizontalHexagonalField[SpaceClass](
		classes:Map[HorizontalHexagonalIndex, SpaceClass]
	):HorizontalHexagonalField[SpaceClass] = {
		new Field(classes)
	}
	
	/**
	 * Create a Field of ElongatedTriangularSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 * @group ElongatedTriangular
	 */
	def ElongatedTriangularField[SpaceClass](
		classes:Map[ElongatedTriangularIndex, SpaceClass]
	):ElongatedTriangularField[SpaceClass] = {
		new Field(classes)
	}
	
	/**
	 * Create a Room of RectangularSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 * @group Rectangular
	 */
	def RectangularRoom[SpaceClass](
		classes:Map[RectangularIndex, SpaceClass],
		warps:Map[RectangularIndex, Function0[RectangularSpace[SpaceClass]]]
	):RectangularTiling[SpaceClass] = {
		new Room(classes, warps)
	}
	
	/**
	 * Create a Room of RectangularSpaces using the specified classes
	 * @tparam SpaceClass the space model
	 * @group Rectangular
	 */
	def RectangularRoom[SpaceClass](
		classes:Seq[Seq[SpaceClass]],
		warps:Map[RectangularIndex, Function0[RectangularSpace[SpaceClass]]]
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
	 * @group HorizontalHexagonal
	 */
	def HorizontalHexagonalRoom[SpaceClass](
		classes:Map[HorizontalHexagonalIndex, SpaceClass],
		warps:Map[HorizontalHexagonalIndex, Function0[HorizontalHexagonalSpace[SpaceClass]]]
	):HorizontalHexagonalTiling[SpaceClass] = {
		new Room(classes, warps)
	}
	
	/**
	 * A SpaceClassMatcher that always returns true
	 * @group Generic
	 * @version next
	 */
	val ConstTrueSpaceClassMatcher:SpaceClassMatcher[Any] = new ConstSpaceClassMatcher(true)
	
	/**
	 * A SpaceClassMatcher that always returns false
	 * @group Generic
	 * @version next
	 */
	val ConstFalseSpaceClassMatcher:SpaceClassMatcher[Any] = new ConstSpaceClassMatcher(false)
	
	/**
	 * A CostFunction with a constant (1) cost for every move.
	 * @group Generic
	 */
	val constantCostFunction:CostFunction[Any] = new CostFunction[Any]{def apply(from:Any, to:Any):Int = 1}
}

package boardGame {
	
	/**
	 * A function that defines the 'cost' of moving from one space to the second space, under the assumption that two spaces are adjacent
	 * @group Generic
	 */
	trait CostFunction[-A] {
		def apply(from:A, to:A):Int
	}
	
	/**
	 * A boolean match against a class
	 * @group Generic
	 */
	trait SpaceClassMatcher[-SpaceClass] {
		/** Returns true if the provided space class fits the requirements of this matcher */
		def unapply(sc:SpaceClass):Boolean
	}
	
	/**
	 * A SpaceClassMatcher that always returns the specified value
	 * @group Generic
	 * @since next
	 */
	private[this] final class ConstSpaceClassMatcher(value:Boolean) extends SpaceClassMatcher[Any] {
		def unapply(sc:Any):Boolean = value
	}
	
	/**
	 * The type of index used for ElongatedTriangular fields
	 * @group ElongatedTriangular
	 */
	final case class ElongatedTriangularIndex(
		x:Int,
		y:Int,
		typ:ElongatedTriangularType
	) {
		override def toString:String = {
			val typChar = typ match {
				case ElongatedTriangularType.NorthTri => '▲'  
				case ElongatedTriangularType.Square => '■'
				case ElongatedTriangularType.SouthTri => '▼'
			}
			s"($x,$y,$typChar)"
		}
	}
	
	/**
	 * The third axis in an ElongatedTriangularIndex
	 * @group ElongatedTriangular
	 */
	sealed trait ElongatedTriangularType
	/**
	 * The three instances of the [[ElongatedTriangularType]] enum
	 * @group ElongatedTriangular
	 */
	object ElongatedTriangularType {
		object Square extends ElongatedTriangularType
		object NorthTri extends ElongatedTriangularType
		object SouthTri extends ElongatedTriangularType
		
		val values:Seq[ElongatedTriangularType] = Seq(
			NorthTri, Square, SouthTri
		)
	}
}
