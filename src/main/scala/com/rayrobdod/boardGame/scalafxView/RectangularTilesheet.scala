/*
	Deduction Tactics
	Copyright (C) 2012-2014  Raymond Dodge

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
package com.rayrobdod.boardGame.scalafxView

import scala.util.Random
import scalafx.scene.Node
import com.rayrobdod.boardGame.RectangularField

/**
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
	def getImageFor(field:RectangularField, x:Int, y:Int, rng:Random):(Node, Node) 
}
