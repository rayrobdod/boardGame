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

import scala.annotation.tailrec

/**
 * 
 */
package object swingView {
	type IndexConverter = Function1[(Int, Int), (Int, Int)]
	
	def lcm(x:Int, y:Int) = x / gcd(x,y) * y
	
	@tailrec def gcd(x:Int, y:Int):Int = {
		System.out.println(x,y)
		if (y == 1) {1} else
		if (x == 1) {1} else
		if (x == y) {x} else
		if (x > y) {gcd(x - y, y)} else
		{gcd(x, y - x)}
	}
}

package swingView {
	trait SpaceClassMatcherFactory[-SpaceClass] {
		def apply(reference:String):SpaceClassMatcher[SpaceClass]
	}
	
	/** A SpaceClassMatcherFactory that always returns a SpaceClassMatcher that always retuns true */
	object ConstTrueSpaceClassMatcherFactory extends SpaceClassMatcherFactory[Any] {
		def apply(s:String):SpaceClassMatcher[Any] = ConstTrueSpaceClassMatcher
	}
	
	/** A SpaceClassMatcherFactory that always returns a SpaceClassMatcher that always retuns false */
	object ConstFalseSpaceClassMatcherFactory extends SpaceClassMatcherFactory[Any] {
		def apply(s:String):SpaceClassMatcher[Any] = ConstFalseSpaceClassMatcher
	}
}
