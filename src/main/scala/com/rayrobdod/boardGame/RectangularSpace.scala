/*
	Deduction Tactics
	Copyright (C) 2012-2013  Raymond Dodge

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
 * Ecludian geometery says that {@code this.left.right == this}, similarly for each other
 * direction, but this makes no specific checks to that effect.
 * 
 * @author Raymond Dodge
 * @version 3.0.0
 * @see [[com.rayrobdod.boardGame.RectangularField]]
 
 * @typeParam A the type of spaceclass used by this class
 */
trait RectangularSpace[A] extends Space[A] {
	def left:Option[Space[A]]
	def up:Option[Space[A]]
	def right:Option[Space[A]]
	def down:Option[Space[A]]
	
	override def adjacentSpaces:Set[_ <: Space[A]] = {
		val optionSpaces = Set(left,up,right,down)
		val someSpaces = optionSpaces - None
		someSpaces.map{_.get}
	}
}


trait StrictRectangularSpace[A] extends RectangularSpace[A] {
	override def left:Option[StrictRectangularSpace[A]]
	override def up:Option[StrictRectangularSpace[A]]
	override def right:Option[StrictRectangularSpace[A]]
	override def down:Option[StrictRectangularSpace[A]]
	
	override def adjacentSpaces:Set[_ <: StrictRectangularSpace[A]] = {
		val optionSpaces = Set(left,up,right,down)
		val someSpaces = optionSpaces - None
		someSpaces.map{_.get}
	}
}

final class RectangularSpaceViaFutures[A](
		val typeOfSpace:A,
		leftFuture:Future[Option[Space[A]]],
		upFuture:Future[Option[Space[A]]],
		rightFuture:Future[Option[Space[A]]],
		downFuture:Future[Option[Space[A]]]) extends RectangularSpace[A]
{
	lazy val left:Option[Space[A]] = leftFuture()
	lazy val up:Option[Space[A]] = upFuture()
	lazy val right:Option[Space[A]] = rightFuture()
	lazy val down:Option[Space[A]] = downFuture()
}

final class StrictRectangularSpaceViaFutures[A](
		val typeOfSpace:A,
		leftFuture:Future[Option[StrictRectangularSpace[A]]],
		upFuture:Future[Option[StrictRectangularSpace[A]]],
		rightFuture:Future[Option[StrictRectangularSpace[A]]],
		downFuture:Future[Option[StrictRectangularSpace[A]]]) extends StrictRectangularSpace[A]
{
	lazy val left:Option[StrictRectangularSpace[A]] = leftFuture()
	lazy val up:Option[StrictRectangularSpace[A]] = upFuture()
	lazy val right:Option[StrictRectangularSpace[A]] = rightFuture()
	lazy val down:Option[StrictRectangularSpace[A]] = downFuture()
}
