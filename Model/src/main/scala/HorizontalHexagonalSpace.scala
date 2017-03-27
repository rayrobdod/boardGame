/*
	Deduction Tactics
	Copyright (C) 2012-2017  Raymond Dodge

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
 * A "pointy-topped" hexagon-shaped [[com.rayrobdod.boardGame.SpaceLike]]
 * 
 * $horizhexsvg
 * 
 * @group SpaceLike
 * 
 * @define horizhexsvg
 * 	Conceptually, the space is one like in the image:
 * 	<samp><svg width="80" height="80" viewbox="0 0 80 80" overflow="hidden" stroke="black" stroke-width="2">
 * 		<path d="M8,20 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M40,20 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="red" />
 * 		<path d="M72,20 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 *	
 * 		<path d="M-8,-8 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M24,-8 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M56,-8 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M88,-8 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 *	
 * 		<path d="M-8,48 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M24,48 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M56,48 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M88,48 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 	</svg></samp>
 * 
 * @tparam SpaceClass the type of spaceclass used by this class
 * @tparam Repr the type of space representing every other space reachable from this space
 */
trait HorizontalHexagonalSpaceLike[SpaceClass, Repr <: SpaceLike[SpaceClass, Repr]] extends SpaceLike[SpaceClass, Repr] {
	/** The space that is located to the immediate northwest of this space */
	def northwest:Option[Repr]
	/** The space that is located to the immediate northeast of this space */
	def northeast:Option[Repr]
	/** The space that is located to the immediate southwest of this space */
	def southwest:Option[Repr]
	/** The space that is located to the immediate southeast of this space */
	def southeast:Option[Repr]
	/** The space that is located to the immediate east  of this space */
	def east:Option[Repr]
	/** The space that is located to the immediate west  of this space */
	def west:Option[Repr]
	
	override def adjacentSpaces:Seq[Repr] = {
		Seq(northwest, west, southwest, southeast, east, northeast).flatMap{_.to[Seq]}
	}
}

/**
 * A "pointy-topped" hexagon-shaped [[com.rayrobdod.boardGame.SpaceLike]]
 * in which all adjacent spaces are also HorizontalHexagonalSpaces
 * 
 * $horizhexsvg
 * 
 * @group HorizontalHexagonal
 * @tparam SpaceClass the type of spaceclass used by this class
 */
trait HorizontalHexagonalSpace[SpaceClass] extends HorizontalHexagonalSpaceLike[SpaceClass, HorizontalHexagonalSpace[SpaceClass]]
