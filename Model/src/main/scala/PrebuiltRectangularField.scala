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
 * @param zeroZeroSpace the space at zero-zero.
 * @since 4.0
 */
final class PrebuiltRectangularField[SpaceClass](
	zeroZeroSpace:StrictRectangularSpace[SpaceClass]
) extends Tiling[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]] {
	private[this] val map:Map[(Int, Int), StrictRectangularSpace[SpaceClass]] = {
		
		val closed = MMap.empty[(Int, Int), StrictRectangularSpace[SpaceClass]]
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
	
	override def space(xy:(Int, Int)):Option[StrictRectangularSpace[SpaceClass]] = map.get( xy )
	
	override def mapIndex[A](f:((Int,Int)) => A):Seq[A] = map.keySet.to[Seq].map(f)
	override def foreachIndex(f:((Int,Int)) => Unit):Unit = map.keySet.foreach(f)
}
