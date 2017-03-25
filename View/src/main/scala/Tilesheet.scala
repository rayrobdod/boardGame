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
import com.rayrobdod.boardGame.Tiling

/**
 * A function describing how to generate icons  
 * 
 * @group Tilesheet
 * 
 * @tparam SpaceClass the space types that this can provide a view for
 * @tparam Index the index used to specify a space in a tiling
 * @tparam Dimension the dimensions of the thlesheet's tiles
 * @tparam Icon the icon produced by this tilesheet
 */
trait Tilesheet[-SpaceClass, Index, Dimension, Icon] {
	/**
	 * Finds the icon for a particular space in a Tiling
	 * 
	 * @param field the tiling in which to generate an icon for
	 * @param idx the index of the space to generate an icon for
	 * @param rng the rng
	 * @return 
		- the part of the image that goes below the movable controlled tokens
		- the part of the image that goes above the movable controlled tokens
	 */
	def getIconFor(
		  field:Tiling[_ <: SpaceClass, Index, _]
		, idx:Index
		, rng:Random
	):(Icon, Icon)
	
	/**
	 * the size of each tile
	 */
	def iconDimensions:Dimension
}
