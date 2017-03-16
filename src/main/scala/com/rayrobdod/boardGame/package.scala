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
	
	/** A CostFunction with a constant (1) cost for every move. */
	val constantCostFunction:CostFunction[Any] = new CostFunction[Any]{def apply(from:Any, to:Any):Int = 1}
}

package boardGame {
	
	/**
	 * A function that defines the 'cost' of moving from one space to the second space, under the assumption that two spaces are adjacent
	 */
	trait CostFunction[-A] {
		def apply(from:A, to:A):Int
	}
	
	/** A boolean match against a class */
	trait SpaceClassMatcher[-SpaceClass] {
		/** Returns true if the provided space class fits the requirements of this matcher */
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
