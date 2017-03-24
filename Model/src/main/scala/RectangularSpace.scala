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
package com.rayrobdod.boardGame

import scala.collection.immutable.Seq

/**
 * A [[com.rayrobdod.boardGame.Space]] in a rectangular board, such that
 * it can have zero or one bordering space in each of the four cardinal directions.
 * 
 * Euclidean geometry says that `this.west.east == this` and `this.north.south == this`, similarly for each other
 * direction, but this makes no specific checks to that effect.
 * 
 * @author Raymond Dodge
 * @version 4.0
 * @see [[com.rayrobdod.boardGame.RectangularField]]
 * @tparam SpaceClass the type of spaceclass used by this class
 * @tparam Repr the type of space representing every other space reachable from this space
 */
trait RectangularSpace[SpaceClass, Repr <: Space[SpaceClass, Repr]] extends Space[SpaceClass, Repr] {
	/** The space that is located to the west  of this space */
	def west:Option[Repr]
	/** The space that is located to the north of this space */
	def north:Option[Repr]
	/** The space that is located to the east  of this space */
	def east:Option[Repr]
	/** The space that is located to the south of this space */
	def south:Option[Repr]
	
	/**
	 * the union of `this.west`, `this.north`, `this.east`, `this.south`
	 */
	override def adjacentSpaces:Seq[Repr] = {
		Seq(west, north, east, south).flatMap{_.to[Seq]}
	}
}


/**
 * A RectangularSpace with the additional requirement that every
 * adjacent space also be a StrictRectangularSpace
 * @version 4.0
 */
trait StrictRectangularSpace[SpaceClass] extends RectangularSpace[SpaceClass, StrictRectangularSpace[SpaceClass]] {
}

/**
 * A RectangularSpace where the values of `west`, `east`, `north` and `south` are
 * lazily evaluated from `scala.Function0`s
 * 
 * @version 4.0
 * @constructor
 * @tparam SpaceClass the domain object representing the properties of this space 
 * @param westFuture  A function that is called to determine the result of the `west`  method
 * @param northFuture A function that is called to determine the result of the `north` method
 * @param eastFuture  A function that is called to determine the result of the `east`  method
 * @param southFuture A function that is called to determine the result of the `south` method 
 */
class RectangularSpaceViaFutures[SpaceClass, Repr <: Space[SpaceClass, Repr]](
		val typeOfSpace:SpaceClass,
		westFuture:Function0[Option[Repr]],
		northFuture:Function0[Option[Repr]],
		eastFuture:Function0[Option[Repr]],
		southFuture:Function0[Option[Repr]]
) extends RectangularSpace[SpaceClass, Repr] {
	lazy override val west:Option[Repr] = westFuture()
	lazy override val north:Option[Repr] = northFuture()
	lazy override val east:Option[Repr] = eastFuture()
	lazy override val south:Option[Repr] = southFuture()
}

/**
 * A StrictRectangularSpace where the values of `west`, `east`, `north` and `south` are
 * lazily evaluated from scala.Function0s
 * 
 * @version 4.0
 * @constructor
 * @tparam SpaceClass the domain object representing the properties of this space
 * @param westFuture  A function that is called to determine the result of the `west`  method
 * @param northFuture A function that is called to determine the result of the `north` method
 * @param eastFuture  A function that is called to determine the result of the `east`  method
 * @param southFuture A function that is called to determine the result of the `south` method 
 */
final class StrictRectangularSpaceViaFutures[SpaceClass](
		typeOfSpace:SpaceClass,
		westFuture:Function0[Option[StrictRectangularSpace[SpaceClass]]],
		northFuture:Function0[Option[StrictRectangularSpace[SpaceClass]]],
		eastFuture:Function0[Option[StrictRectangularSpace[SpaceClass]]],
		southFuture:Function0[Option[StrictRectangularSpace[SpaceClass]]]
) extends RectangularSpaceViaFutures[SpaceClass, StrictRectangularSpace[SpaceClass]](typeOfSpace, westFuture, northFuture, eastFuture, southFuture) with StrictRectangularSpace[SpaceClass]
