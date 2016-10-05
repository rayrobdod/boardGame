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
package com.rayrobdod.boardGame.swingView

import java.awt.Color
import java.awt.Dimension
import javax.swing.Icon
import scala.util.Random
import com.rayrobdod.swing.SolidColorIcon
import com.rayrobdod.boardGame.RectangularField

/**
 * A tilesheet that creates a Checked pattern.
 * 
 * Spaces where the sum of the indexes is even uses a square of the 'light' color,
 * Spaces where the sum of the indexes is odd uses a square of the 'dark' color
 * 
 * @author Raymond Dodge
 * @version 3.0.0
 * 
 * @constructor Creates a CheckerboardTilesheet
 * @param light,dark the two colors the checkerboard should show
 * @param dim the size of each tile in the checkerboard
 */
final case class CheckerboardTilesheet(
		val light:Color = Color.white,
		val dark:Color = Color.black,
		val dim:Dimension = new Dimension(16,16)
) extends RectangularTilesheet[Any] {
	override def name:String = "Checkerboard: " + light + "/" + dark;
	override def toString:String = name + ", " + dim;
	
	val lightIcon = new SolidColorIcon(light, dim.width, dim.height)
	val darkIcon  = new SolidColorIcon(dark,  dim.width, dim.height)
	val transparentIcon = new SolidColorIcon(new Color(0,0,0,0), dim.width, dim.height)
	
	def getIconFor(f:RectangularField[_ <: Any], x:Int, y:Int, rng:Random):(Icon, Icon) = {
		(( if ((x + y) % 2 == 0) {lightIcon} else {darkIcon}, transparentIcon ))
	}
}
