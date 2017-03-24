package com.rayrobdod.boardGame

import scala.collection.immutable.Seq

/**
 * A set of spaces which can be accessed via the specified coordinate system
 * 
 * @tparam SpaceClass the space model
 * @tparam Index the key used to specify a space from this field
 * @tparam SpaceType the spaces contained in this tiling
 */
trait Tiling[SpaceClass, Index, SpaceType <: Space[SpaceClass, SpaceType]] {
	
	/**  Returns the space at the specified index */
	def space(idx:Index):Option[SpaceType]
	/**  Returns the spaceclass of the space at the specified index */
	def spaceClass(idx:Index):Option[SpaceClass] = this.space(idx).map{_.typeOfSpace}
	
	/** Calls the specified function at every index in which this tiling has a space */
	def mapIndex[A](f:Index => A):Seq[A]
	/** Calls the specified function at every index in which this tiling has a space */
	def foreachIndex(f:Index => Unit):Unit
	
	/** Returns true iff there is a space at the specified index  */
	def contains(idx:Index):Boolean = this.space(idx).isDefined
}
