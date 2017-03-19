package com.rayrobdod.boardGame

import scala.collection.immutable.{Seq, Map, Range}

trait HorizontalHexagonalTilable[SpaceClass] {
	
	def getSpaceAt(ew:Int, nwse:Int):Option[StrictHorizontalHexagonalSpace[SpaceClass]]
	
	def mapIndex[A](f:((Int, Int)) => A):Seq[A]
	def foreachIndex(f:((Int, Int)) => Unit):Unit
	
	lazy val minEW = this.mapIndex{_._1}.min
	lazy val maxEW = this.mapIndex{_._1}.max
	lazy val minNWSE = this.mapIndex{_._2}.min
	lazy val maxNWSE = this.mapIndex{_._2}.max
	lazy val minNESW = this.mapIndex{x => -x._2 - x._1}.min
	lazy val maxNESW = this.mapIndex{x => -x._2 - x._1}.max
	
	final def containsIndex(ew:Int, nwse:Int):Boolean = this.getSpaceAt(ew,nwse).isDefined
}
