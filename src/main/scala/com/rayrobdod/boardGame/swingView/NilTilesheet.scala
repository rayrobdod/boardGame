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

import java.awt.{Component => Comp, Graphics => Graph}
import javax.swing.Icon
import scala.util.Random
import com.rayrobdod.boardGame.{RectangularField => Field}

/**
 * A tilesheet that has only one rule: for anything, display blank image.
 * @author Raymond Dodge
 * @version 19 Aug 2011
 * @version 15 Dec 2011 - moved from {@code net.verizon.rayrobdod.boardGame.view} to {@code com.rayrobdod.boardGame.view}
 * @version 11 Jun 2012 - changing due to the change of image in RectangularVisualizationRule
 * @version 25 Aug 2012 - change to match new type of Tilesheet
 */
object NilTilesheet extends RectangularTilesheet
{
	override val name = "Nil"
	override def getIconFor(f:Field, x:Int, y:Int, rng:Random) = getIconFor
	
	private val getIconFor = ((BlankIcon, BlankIcon))
	
	object BlankIcon extends Icon{
		def getIconWidth = 16
		def getIconHeight = 16
		def paintIcon(c:Comp, g:Graph, x:Int, y:Int) {}
	}
}
