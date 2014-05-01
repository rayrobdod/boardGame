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
 
 * @constructor
 * @param typeOfSpace the class that defines how this space interacts with Tokens.
 * @param *Future a future that returns the spaces that border this.
 */
trait RectangularSpace[A] extends Space[A] {
	def left:Option[Space[A]]
	def up:Option[Space[A]]
	def right:Option[Space[A]]
	def down:Option[Space[A]]
	
	override def adjacentSpaces:Set[Space[A]] = {
		val optionSpaces = Set(left,up,right,down)
		val someSpaces = optionSpaces - None
		someSpaces.map{_.get}
	}
}


trait StrictRectangularSpace[A] extends RectangularSpace[A] {
	override def left:Option[RectangularSpace[A]]
	override def up:Option[RectangularSpace[A]]
	override def right:Option[RectangularSpace[A]]
	override def down:Option[RectangularSpace[A]]
	
	override def adjacentSpaces:Set[RectangularSpace[A]] = {
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
