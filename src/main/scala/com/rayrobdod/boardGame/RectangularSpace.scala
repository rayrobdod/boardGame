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

import scala.{Function0 => Future}

/**
 * A [[com.rayrobdod.boardGame.Space]] in a rectangular board, such that
 * it can have zero or one bordering space in each of the four cardinal directions.
 * 
 * Euclidean geometry says that `this.left.right == this` and `this.up.down == this`, similarly for each other
 * direction, but this makes no specific checks to that effect.
 * 
 * @author Raymond Dodge
 * @version 3.0.0
 * @see [[com.rayrobdod.boardGame.RectangularField]]
 
 * @tparam SpaceClass the type of spaceclass used by this class
 */
trait RectangularSpace[SpaceClass] extends Space[SpaceClass] {
	/** The space that is located to the left  of this space, whatever 'left'  means */
	def left:Option[Space[SpaceClass]]
	/** The space that is located to the up    of this space, whatever 'up'    means */
	def up:Option[Space[SpaceClass]]
	/** The space that is located to the right of this space, whatever 'right' means */
	def right:Option[Space[SpaceClass]]
	/** The space that is located to the down  of this space, whatever 'down'  means */
	def down:Option[Space[SpaceClass]]
	
	/**
	 * the union of `this.left`, `this.up`, `this.right`, `this.down`
	 */
	override def adjacentSpaces:Set[_ <: Space[SpaceClass]] = {
		val optionSpaces = Set(left,up,right,down)
		val someSpaces = optionSpaces - None
		someSpaces.map{_.get}
	}
}


/**
 * A RectangularSpace with the additional requirement that every
 * adjacent space also be a StrictRectangularSpace
 */
trait StrictRectangularSpace[SpaceClass] extends RectangularSpace[SpaceClass] {
	override def left:Option[StrictRectangularSpace[SpaceClass]]
	override def up:Option[StrictRectangularSpace[SpaceClass]]
	override def right:Option[StrictRectangularSpace[SpaceClass]]
	override def down:Option[StrictRectangularSpace[SpaceClass]]
	
	override def adjacentSpaces:Set[_ <: StrictRectangularSpace[SpaceClass]] = {
		val optionSpaces = Set(left,up,right,down)
		val someSpaces = optionSpaces - None
		someSpaces.map{_.get}
	}
}

/**
 * A RectangularSpace where the values of `left`, `right`, `up` and `down` are
 * lazily evaluated from scala.Function0s
 * 
 * @constructor
 * @tparam SpaceClass the domain object representing the properties of this space 
 * @param leftFuture  A function that is called to determine the result of the `left`  method
 * @param upFuture    A function that is called to determine the result of the `up`    method
 * @param rightFuture A function that is called to determine the result of the `right` method
 * @param downFuture  A function that is called to determine the result of the `down`  method 
 */
final class RectangularSpaceViaFutures[SpaceClass](
		val typeOfSpace:SpaceClass,
		leftFuture:Future[Option[Space[SpaceClass]]],
		upFuture:Future[Option[Space[SpaceClass]]],
		rightFuture:Future[Option[Space[SpaceClass]]],
		downFuture:Future[Option[Space[SpaceClass]]]) extends RectangularSpace[SpaceClass]
{
	lazy override val left:Option[Space[SpaceClass]] = leftFuture()
	lazy override val up:Option[Space[SpaceClass]] = upFuture()
	lazy override val right:Option[Space[SpaceClass]] = rightFuture()
	lazy override val down:Option[Space[SpaceClass]] = downFuture()
}

/**
 * A StrictRectangularSpace where the values of `left`, `right`, `up` and `down` are
 * lazily evaluated from scala.Function0s
 * 
 * @constructor
 * @tparam SpaceClass the domain object representing the properties of this space
 * @param leftFuture  A function that is called to determine the result of the `left`  method
 * @param upFuture    A function that is called to determine the result of the `up`    method
 * @param rightFuture A function that is called to determine the result of the `right` method
 * @param downFuture  A function that is called to determine the result of the `down`  method 
 */
final class StrictRectangularSpaceViaFutures[SpaceClass](
		val typeOfSpace:SpaceClass,
		leftFuture:Future[Option[StrictRectangularSpace[SpaceClass]]],
		upFuture:Future[Option[StrictRectangularSpace[SpaceClass]]],
		rightFuture:Future[Option[StrictRectangularSpace[SpaceClass]]],
		downFuture:Future[Option[StrictRectangularSpace[SpaceClass]]]) extends StrictRectangularSpace[SpaceClass]
{
	lazy override val left:Option[StrictRectangularSpace[SpaceClass]] = leftFuture()
	lazy override val up:Option[StrictRectangularSpace[SpaceClass]] = upFuture()
	lazy override val right:Option[StrictRectangularSpace[SpaceClass]] = rightFuture()
	lazy override val down:Option[StrictRectangularSpace[SpaceClass]] = downFuture()
}
