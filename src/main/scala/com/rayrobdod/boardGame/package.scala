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
 * 
 */
package object boardGame {
	type RectangularFieldIndex = Tuple2[Int, Int]
	type RectangularField[A] = Map[RectangularFieldIndex, StrictRectangularSpace[A]]
	
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
}

package boardGame {
	
	/** A boolean match against a class */
	trait SpaceClassMatcher[-SpaceClass] {
		def unapply(sc:SpaceClass):Boolean
	}
	
	/**
	 * A SpaceClassMatcher that always returns the specified value
	 * @since next
	 */
	private[this] final class ConstSpaceClassMatcher(value:Boolean) extends SpaceClassMatcher[Any] {
		def unapply(sc:Any):Boolean = value
	}
}
