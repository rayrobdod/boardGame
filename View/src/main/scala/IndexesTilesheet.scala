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
package view

import scala.util.Random

/**
 * A tilesheet that prints the index of each space
 * 
 * @group TrivialTilesheet
 * 
 * @constructor
 * @param fillIcon a icon that displays a background
 * @param stringIcon an icon that displays the specified string
 * @param iconDimensions the size of each tile
 * @tparam Index the index used to specify a space in a tiling
 * @tparam Dimension the dimensions of the thlesheet's tiles
 * @tparam Icon the icon produced by this tilesheet
 */
final class IndexesTilesheet[Index, Dimension, Icon](
	  fillIcon:Function1[Index, Icon]
	, stringIcon:Function1[String, Icon]
	, override val iconDimensions:Dimension
) extends Tilesheet[Any, Index, Dimension, Icon] {
	
	override def getIconFor(
			  f:Tiling[_ <: Any, Index, _]
			, idx:Index
			, rng:Random
	):TileLocationIcons[Icon] = TileLocationIcons(
			  fillIcon(idx)
			, stringIcon(idx.toString)
	)
}
