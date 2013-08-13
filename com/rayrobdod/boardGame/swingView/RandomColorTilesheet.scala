package com.rayrobdod.boardGame.swingView

import java.awt.Color
import java.awt.Dimension
import scala.util.Random
import com.rayrobdod.swing.SolidColorIcon
import com.rayrobdod.boardGame.RectangularField

/**
 * A tilesheet for testing randoms. Isolating the problem.
 * 
 * @author Raymond Dodge
 * @version 2013 Mar 04
 */
class RandomColorTilesheet(
		val dim:Dimension = new Dimension(64,24)
) extends RectangularTilesheet {
	override def name = "Random Color";
	override def toString = name + ", " + dim;
	
	def getIconFor(f:RectangularField, x:Int, y:Int, rng:Random) = {
		val color = new Color(rng.nextInt)
		
		(( new SolidColorIcon(color, dim.width, dim.height), new ColorStringIcon(color) ))
	}
	
	
	class ColorStringIcon(color:Color) extends javax.swing.Icon {
		override def getIconWidth = dim.width
		override def getIconHeight = dim.height
		
		import java.awt.{Component, Graphics}
		override def paintIcon(c:Component, g:Graphics, x:Int, y:Int)
		{
			g.setColor(foreground)
			g.drawString("" + color.getRGB.toHexString , x+2, y + dim.height - 5)
		}
		
		@inline private lazy val foreground = {
			if ((color.getRed + color.getBlue + color.getGreen) < (128 * 3)) {
				Color.white
			} else {Color.black}
		}
	}
}
