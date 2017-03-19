package com.rayrobdod.boardGame

import scala.collection.immutable.Seq

/**
 * A "pointy-topped" hexagon-shaped [[com.rayrobdod.boardGame.Space]]
 * 
 * @since 4.0
 * @tparam SpaceClass the type of spaceclass used by this class
 */
trait HorizontalHexagonalSpace[SpaceClass, Repr <: Space[SpaceClass, Repr]] extends Space[SpaceClass, Repr] {
	def northwest:Option[Repr]
	def northeast:Option[Repr]
	def southwest:Option[Repr]
	def southeast:Option[Repr]
	/** The space that is located to the east  of this space */
	def east:Option[Repr]
	/** The space that is located to the west  of this space */
	def west:Option[Repr]
	
	/**
	 * 
	 */
	override def adjacentSpaces:Seq[Repr] = {
		Seq(northwest, west, southwest, southeast, east, northeast).flatMap{_.to[Seq]}
	}
}

trait StrictHorizontalHexagonalSpace[SpaceClass] extends HorizontalHexagonalSpace[SpaceClass, StrictHorizontalHexagonalSpace[SpaceClass]]
