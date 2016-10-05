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
 * A tilesheet for testing randoms. Created to help isolate problems with the Random parameter
 * 
 * @version next
 */
final class RandomColorTilesheet(
		val dim:Dimension = new Dimension(64,24)
) extends RectangularTilesheet[Any] {
	override def name:String = "Random Color";
	override def toString:String = name + ", " + dim;
	
	def getIconFor(f:RectangularField[_ <: Any], x:Int, y:Int, rng:Random):(Icon, Icon) = {
		val color = new Color(rng.nextInt)
		
		(( new SolidColorIcon(color, dim.width, dim.height), new RandomColorTilesheet.ColorStringIcon(color, dim) ))
	}
	
	protected def canEquals(other:Any):Option[RandomColorTilesheet] = {
		Option(other).collect{case x:RandomColorTilesheet => x}
	}
	override def equals(other:Any):Boolean = {
		this.canEquals(other).foldLeft(false){(b, x:RandomColorTilesheet) =>
			x.canEquals(this).foldLeft(false){(b, y) => true}
		}
	}
	override def hashCode:Int = dim.hashCode
}

object RandomColorTilesheet {
	/**
	 * An icon that prints the hex value of a color in whichever of black or white contrasts with that color
	 * @version next
	 */
	final class ColorStringIcon(val color:Color, val dim:Dimension) extends javax.swing.Icon {
		override def getIconWidth:Int = dim.width
		override def getIconHeight:Int = dim.height
		
		import java.awt.{Component, Graphics}
		override def paintIcon(c:Component, g:Graphics, x:Int, y:Int) {
			g.setColor(foreground)
			g.drawString("" + color.getRGB.toHexString, x + 2, y + dim.height - 5)
		}
		
		@inline private lazy val foreground = {
			if ((color.getRed + color.getBlue + color.getGreen) < (128 * 3)) {
				Color.white
			} else {Color.black}
		}
		
		
		protected def canEquals(other:Any):Option[ColorStringIcon] = {
			Option(other).collect{case x:ColorStringIcon => x}
		}
		override def equals(other:Any):Boolean = {
			this.canEquals(other).foldLeft(false){(b, x:ColorStringIcon) =>
				x.canEquals(this).foldLeft(false){(b, y:ColorStringIcon) =>
					y.color == x.color &&
					y.dim == x.dim
				}
			}
		}
		override def hashCode:Int = color.hashCode
	}
}
