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

import scala.annotation.tailrec

/**
 * 
 */
package object swingView {
	type IndexConverter = Function1[(Int, Int), (Int, Int)]
	
	/** calculates the Least Common Multiple of two values */
	def lcm(x:Int, y:Int):Int = x / gcd(x,y) * y
	
	/** calculates the Greatest Common Denominator of two values */
	@tailrec def gcd(x:Int, y:Int):Int = {
		if (y == 1) {1} else
		if (x == 1) {1} else
		if (x == y) {x} else
		if (x > y) {gcd(x - y, y)} else
		{gcd(x, y - x)}
	}
	
	/**
	 * A SpaceClassMatcherFactory that always returns a SpaceClassMatcher that always returns true
	 * @version next
	 */
	val ConstTrueSpaceClassMatcherFactory:SpaceClassMatcherFactory[Any] = new ConstSpaceClassMatcherFactory[Any](ConstTrueSpaceClassMatcher)
	
	/**
	 * A SpaceClassMatcherFactory that always returns a SpaceClassMatcher that always returns false
	 * @version next
	 */
	val ConstFalseSpaceClassMatcherFactory:SpaceClassMatcherFactory[Any] = new ConstSpaceClassMatcherFactory[Any](ConstFalseSpaceClassMatcher)
}

package swingView {
	trait SpaceClassMatcherFactory[-SpaceClass] {
		/** Returns the SpaceClassMatcher that the provided reference string represents */
		def apply(reference:String):SpaceClassMatcher[SpaceClass]
	}
	
	/**
	 * A SpaceClassMatcherFactory that always returns the specified value
	 * @since next
	 */
	private[this] final class ConstSpaceClassMatcherFactory[SpaceClass](value:SpaceClassMatcher[SpaceClass])
				extends SpaceClassMatcherFactory[SpaceClass] {
		def apply(reference:String):SpaceClassMatcher[SpaceClass] = value
	}
}
