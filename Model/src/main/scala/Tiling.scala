package com.rayrobdod.boardGame

import scala.collection.immutable.Seq

/**
 * A set of spaces which can be accessed via the specified coordinate system
 */
trait Tiling[SpaceClass, Index, SpaceType <: Space[SpaceClass, SpaceType]] {
	
	def space(idx:Index):Option[SpaceType]
	def spaceClass(idx:Index):Option[SpaceClass] = this.space(idx).map{_.typeOfSpace}
	
	def mapIndex[A](f:Index => A):Seq[A]
	def foreachIndex(f:Index => Unit):Unit
	
	def contains(idx:Index):Boolean = this.space(idx).isDefined
}
