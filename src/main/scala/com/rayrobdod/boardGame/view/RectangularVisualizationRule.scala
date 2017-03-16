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
package com.rayrobdod.boardGame.view

import scala.util.Random
import scala.collection.immutable.{Seq, Map}
import com.rayrobdod.boardGame.RectangularField

/**
 * A single rule for associating images with spots on a RectangularField
 * 
 * @since next
 * @tparam SpaceClass the types of spaces to deal with
 * @tparam ImagePart icon parts
 */
abstract class RectangularVisualizationRule[SpaceClass, IconPart] {
	
	/**
	 * Returns the images to be used if this visualization rule applies 
	 */
	def iconParts:Map[Int, Seq[IconPart]]
	
	protected def indexiesMatch(x:Int, y:Int, width:Int, height:Int):Boolean
	protected def surroundingTilesMatch(field:RectangularField[_ <: SpaceClass], x:Int, y:Int):Boolean
	protected def randsMatch(rng:Random):Boolean
	
	/**
	 * Returns true if this rule matches the particular space on the provided field
	 * 
	 * Pretty much the union of `indexiesMatch`, `surroundingTilesMatch` and `randsMatch`
	 * @param field the field containing the tile to check
	 * @param x the x-coordinate of the tile to check
	 * @param y the y-coordinate of the tile to check
	 * @param rng a Random to allow for randomness
	 */
	final def matches(field:RectangularField[_ <: SpaceClass], x:Int, y:Int, rng:Random):Boolean = {
		indexiesMatch(x, y, field.width, field.height) &&
				surroundingTilesMatch(field, x, y) &&
				randsMatch(rng)
	}
	
	/**
	 * A ranking indicating which rules should take precedence should multiple rules
	 * apply at the same time.
	 * A rule with a higher priority overrides a rule with a lower priority
	 */
	def priority:Int
}
