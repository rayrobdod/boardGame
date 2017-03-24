package com.rayrobdod.boardGame

import scala.collection.immutable.Seq

/**
 * A "pointy-topped" hexagon-shaped [[com.rayrobdod.boardGame.Space]]
 * 
 * $horizhexsvg
 * 
 * @define horizhexsvg
 * 	Conceptually, the space is one like in the image:
 * 	<samp><svg width="80" height="80" viewbox="0 0 80 80" overflow="hidden" stroke="black" stroke-width="2">
 * 		<path d="M8,20 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M40,20 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="red" />
 * 		<path d="M72,20 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 *	
 * 		<path d="M-8,-8 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M24,-8 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M56,-8 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M88,-8 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 *	
 * 		<path d="M-8,48 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M24,48 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M56,48 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 		<path d="M88,48 l16,12 0,16 -16,12 -16,-12 0,-16 Z" fill="none" />
 * 	</svg></samp>
 * 
 * @since 4.0
 * @tparam SpaceClass the type of spaceclass used by this class
 * @tparam Repr the type of space representing every other space reachable from this space
 */
trait HorizontalHexagonalSpace[SpaceClass, Repr <: Space[SpaceClass, Repr]] extends Space[SpaceClass, Repr] {
	/** The space that is located to the immediate northwest of this space */
	def northwest:Option[Repr]
	/** The space that is located to the immediate northeast of this space */
	def northeast:Option[Repr]
	/** The space that is located to the immediate southwest of this space */
	def southwest:Option[Repr]
	/** The space that is located to the immediate southeast of this space */
	def southeast:Option[Repr]
	/** The space that is located to the immediate east  of this space */
	def east:Option[Repr]
	/** The space that is located to the immediate west  of this space */
	def west:Option[Repr]
	
	override def adjacentSpaces:Seq[Repr] = {
		Seq(northwest, west, southwest, southeast, east, northeast).flatMap{_.to[Seq]}
	}
}

/**
 * A "pointy-topped" hexagon-shaped [[com.rayrobdod.boardGame.Space]]
 * in which all adjacent spaces are also StrictHorizontalHexagonalSpaces
 * 
 * $horizhexsvg
 * 
 * @since 4.0
 * @tparam SpaceClass the type of spaceclass used by this class
 */
trait StrictHorizontalHexagonalSpace[SpaceClass] extends HorizontalHexagonalSpace[SpaceClass, StrictHorizontalHexagonalSpace[SpaceClass]]
