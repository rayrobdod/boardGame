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
package com.rayrobdod.boardGame.view

import scala.util.Random
import scala.collection.immutable.{Seq, Map}
import com.rayrobdod.boardGame.Tiling

/**
 * A single rule for associating images with spots on a Tiling
 * 
 * @group VisualizationRuleTilesheet
 * 
 * @tparam SpaceClass the types of spaces to deal with
 * @tparam Index the index used to specify a space in a tiling
 * @tparam IconPart icon parts
 */
abstract class VisualizationRule[SpaceClass, Index, IconPart] {
	
	/**
	 * Returns the images to be used if this visualization rule applies
	 *
	 * The map key is the 'layer', where layers with greater numbers cover
	 * layers with lower numbers (and the "token layer" is zero). The map
	 * value is a sequence of IconParts to be played in an animation.
	 */
	def iconParts:Map[Int, Seq[IconPart]]
	
	protected def indexiesMatch(xy:Index):Boolean
	protected def surroundingTilesMatch(field:Tiling[_ <: SpaceClass, Index, _], xy:Index):Boolean
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
	final def matches(field:Tiling[_ <: SpaceClass, Index, _], xy:Index, rng:Random):Boolean = {
		indexiesMatch(xy) &&
				surroundingTilesMatch(field, xy) &&
				randsMatch(rng)
	}
	
	/**
	 * A ranking indicating which rules should take precedence should multiple rules
	 * apply at the same time.
	 * A rule with a higher priority overrides a rule with a lower priority
	 */
	def priority:Int
}
