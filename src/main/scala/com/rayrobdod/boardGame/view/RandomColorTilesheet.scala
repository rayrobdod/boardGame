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
import com.rayrobdod.boardGame.RectangularField

/**
 * A tilesheet for testing randoms. Isolating the problem.
 * 
 */
final class RandomColorTilesheet[Icon](
		colorToIcon:Function3[Int, Int, Int, Icon],
		colorToStrIcon:Function4[String, Int, Int, Int, Icon],
		iconWidth:Int = 64,
		iconHeight:Int = 24
) extends RectangularTilesheet[Any, Icon] {
	override def name:String = "Random Color";
	override def toString:String = name;
	
	override def getIconFor(f:RectangularField[_ <: Any], x:Int, y:Int, rng:Random):(Icon, Icon) = {
		val background = rng.nextInt
		val foreground = this.foreground(background)
		val text = ("000000" + background.toHexString).takeRight(6)
		
		(( colorToIcon(background, iconWidth, iconHeight), colorToStrIcon(text, foreground, iconWidth, iconHeight) ))
	}
	
	/** Find a color that contrasts with the specified background color */
	@inline private[this] def foreground(background:Int):Int = {
		if (((background >> 16) & 0xFF) + ((background >> 8) & 0xFF) + ((background >> 0) & 0xFF) < (128 * 3)) {
			0xFFFFFF // white
		} else {
			0 // black
		}
	}
}
