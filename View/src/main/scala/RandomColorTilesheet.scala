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

import java.awt.Color
import scala.util.Random
import com.rayrobdod.boardGame.Space
import com.rayrobdod.boardGame.Tiling

/**
 * A tilesheet for testing randoms. Isolating the problem.
 * 
 */
final class RandomColorTilesheet[Index, Dimension, Icon](
		  colorToIcon:Function2[java.awt.Color, Dimension, Icon]
		, stringIcon:Function3[String, java.awt.Color, Dimension, Icon]
		, override val iconDimensions:Dimension
) extends Tilesheet[Any, Index, Dimension, Icon] {
	override def toString:String = "Random Color"
	
	override def getIconFor(f:Tiling[_ <: Any, Index, _], x:Index, rng:Random):(Icon, Icon) = {
		val background = new java.awt.Color(rng.nextInt)
		val foreground = this.foreground(background)
		val text = ("000000" + background.getRGB.toHexString).takeRight(6)
		
		(( colorToIcon(background, iconDimensions), stringIcon(text, foreground, iconDimensions) ))
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
