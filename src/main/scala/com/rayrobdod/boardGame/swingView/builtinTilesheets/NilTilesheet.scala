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

import java.awt.{Component, Graphics}
import javax.swing.Icon
import scala.util.Random
import com.rayrobdod.boardGame.RectangularField

/**
 * A tilesheet that has only one rule: for anything, display blank image.
 * @author Raymond Dodge
 * @version 3.0.0
 */
object NilTilesheet extends RectangularTilesheet[Any]
{
	override val name:String = "Nil"
	override def getIconFor(f:RectangularField[_ <: Any], x:Int, y:Int, rng:Random):(Icon,Icon) = getIconFor
	
	private val getIconFor = ((BlankIcon, BlankIcon))
	
	object BlankIcon extends Icon{
		def getIconWidth:Int = 16
		def getIconHeight:Int = 16
		def paintIcon(c:Component, g:Graphics, x:Int, y:Int):Unit = {}
	}
}
