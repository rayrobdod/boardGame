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
 * @version 2.0.0
 * @see [[com.rayrobdod.boardGame.RectangularField]]
 
 * @constructor
 * @param typeOfSpace the class that defines how this space interacts with Tokens.
 * @param *Future a future that returns the spaces that border this.
 */
class RectangularSpace(
		typeOfSpace:SpaceClass,
		leftFuture:Future[Option[Space]],
		upFuture:Future[Option[Space]],
		rightFuture:Future[Option[Space]],
		downFuture:Future[Option[Space]]) extends Space(typeOfSpace)
{
	lazy val left:Option[Space] = leftFuture()
	lazy val up:Option[Space] = upFuture()
	lazy val right:Option[Space] = rightFuture()
	lazy val down:Option[Space] = downFuture()
	
	override def adjacentSpaces:Set[Space] = {
		val optionSpaces = Set(left,up,right,down)
		val someSpaces = optionSpaces - None
		someSpaces.map{_.get}
	}
}
