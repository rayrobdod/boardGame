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

import java.awt.Color
import scala.util.Random
import com.rayrobdod.boardGame.Tiling

/**
 * A tilesheet that gives every tile a randomly-determined color
 * 
 * @group TrivialTilesheet
 * 
 * @constructor
 * @param stringIcon an icon that displays the specified string
 * @param colorToIcon an icon that deisplays the specified color
 * @param iconDimensions the size of each tile
 * @tparam Index the index used to specify a space in a tiling
 * @tparam Dimension the dimensions of the thlesheet's tiles
 * @tparam Icon the icon produced by this tilesheet
 */
final class RandomColorTilesheet[Index, Dimension, Icon](
		  colorToIcon:Function3[java.awt.Color, Dimension, Index, Icon]
		, stringIcon:Function3[String, java.awt.Color, Dimension, Icon]
		, override val iconDimensions:Dimension
) extends Tilesheet[Any, Index, Dimension, Icon] {
	
	override def getIconFor(f:Tiling[_ <: Any, Index, _], x:Index, rng:Random):(Icon, Icon) = {
		val background = new java.awt.Color(rng.nextInt)
		val foreground = this.foreground(background)
		val text = ("000000" + background.getRGB.toHexString).takeRight(6)
		
		(( colorToIcon(background, iconDimensions, x), stringIcon(text, foreground, iconDimensions) ))
	}
	
	/** Find a color that contrasts with the specified background color */
	@inline private[this] def foreground(background:Color):Color = {
		if ((background.getRed + background.getGreen + background.getBlue) < (128 * 3)) {
			Color.white
		} else {
			Color.black
		}
	}
}
