package com.rayrobdod.boardGame

import scala.collection.immutable.{Seq, Map}

/**
 * A rectangular board in which the connections between spaces may not be ecludian
 
 * This is suitable for common RPG maps, in which houses are often larger on the inside,
 * on in other cases where ecludian geometry does not apply. 
 * @since 4.0
 */
final class RectangularRoom[SpaceClass](
		private[boardGame] val classes:Map[(Int, Int), SpaceClass],
		private[boardGame] val warps:Map[(Int, Int), Function0[RectangularRoom.Space[SpaceClass]]] = Map.empty
) extends RectangularTilable[SpaceClass] {
	override def getSpaceAt(x:Int, y:Int):Option[StrictRectangularSpace[SpaceClass]] = classes.get( ((x,y)) ).map{sc => new RectangularRoom.Space(this,x,y)}
	
	override def mapIndex[A](f:((Int,Int)) => A):Seq[A] = classes.keySet.to[Seq].map(f)
	override def foreachIndex(f:((Int,Int)) => Unit):Unit = classes.keySet.foreach(f)
}

private object RectangularRoom {
	private[RectangularRoom] final class Space[SpaceClass](private val field:RectangularRoom[SpaceClass], private val x:Int, private val y:Int) extends StrictRectangularSpace[SpaceClass] {
		override def typeOfSpace:SpaceClass = field.classes.apply( ((x,y)) )
		
		override def east:Option[StrictRectangularSpace[SpaceClass]]  = field.warps.get( ((x - 1, y)) ).map{_.apply}.orElse(field.getSpaceAt(x - 1, y))
		override def north:Option[StrictRectangularSpace[SpaceClass]] = field.warps.get( ((x, y - 1)) ).map{_.apply}.orElse(field.getSpaceAt(x, y - 1))
		override def west:Option[StrictRectangularSpace[SpaceClass]]  = field.warps.get( ((x + 1, y)) ).map{_.apply}.orElse(field.getSpaceAt(x + 1, y))
		override def south:Option[StrictRectangularSpace[SpaceClass]] = field.warps.get( ((x, y + 1)) ).map{_.apply}.orElse(field.getSpaceAt(x, y + 1))
		
		override def toString:String = s"RectangularRoom.Space(typ = $typeOfSpace, x = $x, y = $y, field = ${field})"
		override def hashCode:Int = x * 31 * 31 + y * 31 + field.hashCode
		override def equals(other:Any):Boolean = other match {
			case other2:Space[_] =>
				other2.field == this.field &&
					other2.x == this.x &&
					other2.y == this.y
			case _ => false
		}
	}
}
