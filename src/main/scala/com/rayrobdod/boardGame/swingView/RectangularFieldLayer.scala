/*
	Deduction Tactics
	Copyright (C) 2014  Raymond Dodge

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

import scala.util.Random
import javax.swing.Icon
import com.rayrobdod.boardGame.RectangularField




/**
 * @version 3.0.0
 */
object RectangularFieldLayer {
	def apply[A](
			field:RectangularField[A],
			tilesheet:RectangularTilesheet[A],
			rng:Random = Random
	):(RectangularTilemapLayer, RectangularTilemapLayer) = {
		
		val a:Map[(Int, Int), (Icon, Icon)] = field.map{x => ((x._1, tilesheet.getIconFor(field, x._1._1, x._1._2, rng) )) }
		val top = a.mapValues{_._1}
		val bot = a.mapValues{_._2}
		
		(( new RectangularTilemapLayer(top), new RectangularTilemapLayer(bot) ))
	}
}