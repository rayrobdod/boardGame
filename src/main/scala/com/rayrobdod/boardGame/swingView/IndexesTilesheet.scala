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

import java.awt.Color
import java.awt.Dimension
import scala.util.Random
import com.rayrobdod.swing.SolidColorIcon
import com.rayrobdod.boardGame.RectangularField

/**
 * A tilesheet that prints indexies on a tile
 * 
 * @author Raymond Dodge
 * @version 3.0.0
 */
object IndexesTilesheet extends RectangularTilesheet[Any] {
	override def name = "IndexesTilesheet"
	val dim = new Dimension(32,32)
	
	val lightIcon = new SolidColorIcon(Color.magenta, dim.width, dim.height)
	val darkIcon  = new SolidColorIcon(Color.cyan, dim.width, dim.height)
	
	def getIconFor(f:RectangularField[_], x:Int, y:Int, rng:Random) = {
		((
			if ((x+y)%2 == 0) {lightIcon} else {darkIcon},
			new IndexIcon(x,y)
		))
	}
	
	class IndexIcon(xIndex:Int, yIndex:Int) extends javax.swing.Icon {
		override def getIconWidth = dim.width
		override def getIconHeight = dim.height
		
		import java.awt.{Component, Graphics}
		override def paintIcon(c:Component, g:Graphics, x:Int, y:Int)
		{
			g.setColor(Color.black)
			g.drawString("(x,y)", x, y + dim.height - 18)
			g.drawString("(" + xIndex + "," + yIndex + ")" , x, y + dim.height - 5)
		}
	}
}
