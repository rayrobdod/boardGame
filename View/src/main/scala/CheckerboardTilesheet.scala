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
 * A Tilesheet for Rectangular boards which displays icon in a checker pattern
 * 
 * @group TrivialTilesheet
 * 
 * @constructor
 * @param transparentIcon the icon that is always returned for `getIconFor(...)._2`
 * @param lightIcon the icon that is returned for even `getIconFor(...)._1`
 * @param darkIcon the icon that is returned for odd `getIconFor(...)._1`
 * @param iconDimensions the size of each tile
 */
final class CheckerboardTilesheet[Icon](
	transparentIcon:Function0[Icon],
	lightIcon:Function0[Icon],
	darkIcon:Function0[Icon],
	override val iconDimensions:RectangularDimension
) extends Tilesheet[Any, RectangularIndex, RectangularDimension, Icon] {
	
	override def getIconFor(
			  f:Tiling[_ <: Any, RectangularIndex, _]
			, xy:RectangularIndex
			, rng:Random
	):TileLocationIcons[Icon] = TileLocationIcons(
		  if ((xy._1 + xy._2) % 2 == 0) {lightIcon()} else {darkIcon()}
		, transparentIcon()
	)
	
}
