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
package object view {
	type IndexConverter = Function1[(Int, Int), (Int, Int)]
	
	/** Least Common Multiple */
	def lcm(x:Int, y:Int):Int = x / gcd(x,y) * y
	
	/** Greatest Common Denominator */
	@tailrec def gcd(x:Int, y:Int):Int = {
		if (y == 1) {1} else
		if (x == 1) {1} else
		if (x == y) {x} else
		if (x > y) {gcd(x - y, y)} else
		{gcd(x, y - x)}
	}
}

package view {
	trait SpaceClassMatcherFactory[-SpaceClass] {
		def apply(reference:String):SpaceClassMatcher[SpaceClass]
	}
	
	/** A tilesheet which will always return the Icon specified in the constructor */
	final class NilTilesheet[Icon](val tile:Function0[Icon]) extends RectangularTilesheet[Any, Icon] {
		override val name:String = "Nil"
		override def getIconFor(f:RectangularTiling[_ <: Any], x:Int, y:Int, rng:scala.util.Random):(Icon, Icon) = ((tile(), tile()))
	}
}
