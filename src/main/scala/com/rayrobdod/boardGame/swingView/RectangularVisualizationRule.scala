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
package com.rayrobdod.boardGame.swingView

import com.rayrobdod.boardGame.RectangularField
import scala.util.Random

/**
 * A single rule for matching spaces on a rectangular field
 * 
 * @author Raymond Dodge
 * @version 3.0.0
 */
abstract class RectangularVisualizationRule[A] {
	
	def indexiesMatch(x:Int, y:Int, width:Int, height:Int):Boolean
	def surroundingTilesMatch(field:RectangularField[_ <: A], x:Int, y:Int):Boolean
	def randsMatch(rng:Random):Boolean
	
	final def matches(field:RectangularField[_ <: A], x:Int, y:Int, rng:Random):Boolean = {
		indexiesMatch(x, y, field.spaces.size, field.spaces(0).size) &&
				surroundingTilesMatch(field, x, y) &&
				randsMatch(rng)
	}
	
	def priority:Int
}
