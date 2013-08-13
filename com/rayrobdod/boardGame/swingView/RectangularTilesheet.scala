package com.rayrobdod.boardGame.swingView

import scala.util.Random
import javax.swing.Icon
import com.rayrobdod.boardGame.RectangularField

/**
 * This takes a space in a RectangularField and converts it to images for
 * display in a user interface
 * 
 * @author Raymond Dodge
 * @param 2012 Aug 23
 */
trait RectangularTilesheet
{
	/** a name for the tilesheet */
	def name:String
	
	/**
	 * @param field the field on the space to lookup
	 * @param x the x coordinate of the space to lookup
	 * @param y the y coordinate of the space to lookup
	 * @param rng the 
	 * @return # the part of the image that goes below the movable controlled tokens
			# the part of the image that goes above the movable controlled tokens
	 */
	def getIconFor(field:RectangularField, x:Int, y:Int, rng:Random):(Icon, Icon) 
}
