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
 * A set of spaces which can be accessed via the specified coordinate system
 * 
 * @group Generic
 * @tparam SpaceClass the space model
 * @tparam Index the key used to specify a space from this field
 * @tparam SpaceType the spaces contained in this tiling
 */
trait Tiling[SpaceClass, Index, SpaceType <: Space[SpaceClass, SpaceType]] {
	
	/**  Returns the space at the specified index */
	def space(idx:Index):Option[SpaceType]
	/**  Returns the spaceclass of the space at the specified index */
	def spaceClass(idx:Index):Option[SpaceClass] = this.space(idx).map{_.typeOfSpace}
	
	/** Calls the specified function at every index in which this tiling has a space */
	def mapIndex[A](f:Index => A):Seq[A]
	/** Calls the specified function at every index in which this tiling has a space */
	def foreachIndex(f:Index => Unit):Unit
	
	/** Returns true iff there is a space at the specified index  */
	def contains(idx:Index):Boolean = this.space(idx).isDefined
}
