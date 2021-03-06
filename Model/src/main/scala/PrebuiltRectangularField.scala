/*
	Deduction Tactics
	Copyright (C) 2012-2017  Raymond Dodge

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

import scala.collection.immutable.{Seq, Map}
import scala.collection.mutable.{Map => MMap}

/**
 * A RectangularTilable which takes a space as the input
 * 
 * The space provided is space at (0,0). The other spaces are found via `left`,
 * right, etc. The spaces are assumed to be ecludian and bounded. If
 * `zeroZeroSpace.north.east` provides a different space than 
 * `zeroZeroSpace.east.north`, this will only find one to call (-1, -1). This
 * will also never find `zeroZeroSpace.north.south`
 * 
 * I use [[RectangularField]] since I found building the spaces first more
 * difficult, but I haven't deprecated [[RectangularSpaceViaFutures]] yet,
 * and might as well allow an wasy way to use it.
 * 
 * @group Rectangular
 * @param zeroZeroSpace the space at zero-zero.
 * @tparam SpaceClass the space model
 */
final class PrebuiltRectangularField[SpaceClass](
	zeroZeroSpace:RectangularSpace[SpaceClass]
) extends Tiling[SpaceClass, RectangularIndex, RectangularSpace[SpaceClass]] {
	private[this] val map:Map[(Int, Int), RectangularSpace[SpaceClass]] = {
		
		val closed = MMap.empty[(Int, Int), RectangularSpace[SpaceClass]]
		val open = MMap( ((0,0)) -> zeroZeroSpace )
		
		while (! open.isEmpty) {
			val (idx, space) = open.head
			open -= idx
			
			space.north.map{northSpace =>
				val northIdx = (idx._1, idx._2 - 1)
				if (!closed.isDefinedAt(northIdx) && !open.isDefinedAt(northIdx)) {
					open += (northIdx -> northSpace)
				}
			}
			space.south.map{southSpace =>
				val southIdx = (idx._1, idx._2 + 1)
				if (!closed.isDefinedAt(southIdx) && !open.isDefinedAt(southIdx)) {
					open += (southIdx -> southSpace)
				}
			}
			space.east.map{eastSpace =>
				val eastIdx = (idx._1 - 1, idx._2)
				if (!closed.isDefinedAt(eastIdx) && !open.isDefinedAt(eastIdx)) {
					open += (eastIdx -> eastSpace)
				}
			}
			space.west.map{westSpace =>
				val westIdx = (idx._1 + 1, idx._2)
				if (!closed.isDefinedAt(westIdx) && !open.isDefinedAt(westIdx)) {
					open += (westIdx -> westSpace)
				}
			}
			
			closed += (idx -> space)
		}
		
		closed.toMap
	}
	
	override def space(xy:(Int, Int)):Option[RectangularSpace[SpaceClass]] = map.get( xy )
	
	override def mapIndex[A](f:((Int,Int)) => A):Seq[A] = map.keySet.to[Seq].map(f)
	override def foreachIndex(f:((Int,Int)) => Unit):Unit = map.keySet.foreach(f)
}
