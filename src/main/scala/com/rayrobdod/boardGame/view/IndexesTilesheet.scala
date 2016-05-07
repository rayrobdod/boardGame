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
package com.rayrobdod.boardGame
package view

import scala.collection.immutable.BitSet
import scala.util.Random

/**
 * @constructor
 * @param lightIcon the icon that is returned for even `getIconFor(...)._1`
 * @param darkIcon the icon that is returned for odd `getIconFor(...)._1`
 */
final case class IndexesTilesheet[Icon](
	lightIcon:Icon,
	darkIcon:Icon,
	stringIcon:Function1[String, Icon]
) extends RectangularTilesheet[Any, Icon] {
	override def name:String = "IndexesTilesheet"
	override def toString:String = name
	
	def getIconFor(f:RectangularField[_ <: Any], x:Int, y:Int, rng:Random):(Icon, Icon) = {
		(( if ((x + y) % 2 == 0) {lightIcon} else {darkIcon}, stringIcon(s"""($x, $y)""") ))
	}
}
