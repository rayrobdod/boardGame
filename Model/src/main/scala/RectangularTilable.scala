package com.rayrobdod.boardGame

import scala.collection.immutable.{Seq, Map, Range}

trait RectangularTilable[SpaceClass] {
	
	def getSpaceAt(x:Int, y:Int):Option[StrictRectangularSpace[SpaceClass]]
	
	def mapIndex[A](f:((Int, Int)) => A):Seq[A]
	def foreachIndex(f:((Int, Int)) => Unit):Unit
	
	//final lazy val rangeX:Range = this.mapIndex{_._1}.min to this.mapIndex{_._1}.max
	
	/** @todo a lot of things assume minX and minY are 0. Either fix, or canonialize */
	final lazy val minX:Int = this.mapIndex{_._1}.min
	final lazy val maxX:Int = this.mapIndex{_._1}.max
	/** @todo a lot of things assume minX and minY are 0. Either fix, or canonialize */
	final lazy val minY:Int = this.mapIndex{_._2}.min
	final lazy val maxY:Int = this.mapIndex{_._2}.max
	final def width:Int = maxX - minX
	final def height:Int = maxY - minY
	
	final def containsIndex(x:Int, y:Int):Boolean = this.getSpaceAt(x,y).isDefined
}
