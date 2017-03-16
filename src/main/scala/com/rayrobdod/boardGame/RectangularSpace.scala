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
import scala.{Function0 => Future}

/**
 * A [[com.rayrobdod.boardGame.Space]] in a rectangular board, such that
 * it can have zero or one bordering space in each of the four cardinal directions.
 * 
 * Euclidean geometry says that `this.left.right == this` and `this.up.down == this`, similarly for each other
 * direction, but this makes no specific checks to that effect.
 * 
 * @author Raymond Dodge
 * @version 4.0
 * @see [[com.rayrobdod.boardGame.RectangularField]]
 * @tparam SpaceClass the type of spaceclass used by this class
 */
trait RectangularSpace[SpaceClass, Repr <: Space[SpaceClass, Repr]] extends Space[SpaceClass, Repr] {
	/** The space that is located to the left  of this space, whatever 'left'  means */
	def left:Option[Repr]
	/** The space that is located to the up    of this space, whatever 'up'    means */
	def up:Option[Repr]
	/** The space that is located to the right of this space, whatever 'right' means */
	def right:Option[Repr]
	/** The space that is located to the down  of this space, whatever 'down'  means */
	def down:Option[Repr]
	
	/**
	 * the union of `this.left`, `this.up`, `this.right`, `this.down`
	 */
	override def adjacentSpaces:Seq[Repr] = {
		Seq(left, up, right, down).flatMap{_.to[Seq]}
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
 * A RectangularSpace where the values of `left`, `right`, `up` and `down` are
 * lazily evaluated from `scala.Function0`s
 * 
 * @version 4.0
 * @constructor
 * @tparam SpaceClass the domain object representing the properties of this space 
 * @param leftFuture  A function that is called to determine the result of the `left`  method
 * @param upFuture    A function that is called to determine the result of the `up`    method
 * @param rightFuture A function that is called to determine the result of the `right` method
 * @param downFuture  A function that is called to determine the result of the `down`  method 
 */
class RectangularSpaceViaFutures[SpaceClass, Repr <: Space[SpaceClass, Repr]](
		val typeOfSpace:SpaceClass,
		leftFuture:Future[Option[Repr]],
		upFuture:Future[Option[Repr]],
		rightFuture:Future[Option[Repr]],
		downFuture:Future[Option[Repr]]
) extends RectangularSpace[SpaceClass, Repr] {
	lazy override val left:Option[Repr] = leftFuture()
	lazy override val up:Option[Repr] = upFuture()
	lazy override val right:Option[Repr] = rightFuture()
	lazy override val down:Option[Repr] = downFuture()
}

/**
 * A StrictRectangularSpace where the values of `left`, `right`, `up` and `down` are
 * lazily evaluated from scala.Function0s
 * 
 * @version 4.0
 * @constructor
 * @tparam SpaceClass the domain object representing the properties of this space
 * @param leftFuture  A function that is called to determine the result of the `left`  method
 * @param upFuture    A function that is called to determine the result of the `up`    method
 * @param rightFuture A function that is called to determine the result of the `right` method
 * @param downFuture  A function that is called to determine the result of the `down`  method 
 */
final class StrictRectangularSpaceViaFutures[SpaceClass](
		typeOfSpace:SpaceClass,
		leftFuture:Future[Option[StrictRectangularSpace[SpaceClass]]],
		upFuture:Future[Option[StrictRectangularSpace[SpaceClass]]],
		rightFuture:Future[Option[StrictRectangularSpace[SpaceClass]]],
		downFuture:Future[Option[StrictRectangularSpace[SpaceClass]]]
) extends RectangularSpaceViaFutures[SpaceClass, StrictRectangularSpace[SpaceClass]](typeOfSpace, leftFuture, upFuture, rightFuture, downFuture) with StrictRectangularSpace[SpaceClass]
