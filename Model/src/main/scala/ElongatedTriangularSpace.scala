package com.rayrobdod.boardGame

import scala.collection.immutable.Seq

/**
 * A tile in an elongated triangular tiling
 * @tparam SpaceClass the type of domain object representing the properties of this space 
 */
sealed trait StrictElongatedTriangularSpace[SpaceClass] extends Space[SpaceClass, StrictElongatedTriangularSpace[SpaceClass]]

/**
 * The three types of [[StrictElongatedTriangularSpace]]s
 */
object StrictElongatedTriangularSpace {
	/**
	 * The square-type space in an Elongated Triangular Tiling
	 */
	trait Square[SpaceClass] extends StrictElongatedTriangularSpace[SpaceClass] with RectangularSpace[SpaceClass, StrictElongatedTriangularSpace[SpaceClass]] {
		override def north:Option[Triangle1[SpaceClass]]
		override def south:Option[Triangle2[SpaceClass]]
		override def east:Option[Square[SpaceClass]]
		override def west:Option[Square[SpaceClass]]
	}
	/**
	 * The triangle to the north of a square in an Elongated Triangular Tiling
	 */
	trait Triangle1[SpaceClass] extends StrictElongatedTriangularSpace[SpaceClass] {
		def south:Option[Square[SpaceClass]]
		def northEast:Option[Triangle2[SpaceClass]]
		def northWest:Option[Triangle2[SpaceClass]]
		override def adjacentSpaces:Seq[StrictElongatedTriangularSpace[SpaceClass]] = Seq(south, northEast, northWest).flatMap{_.to[Seq]}
	}
	/**
	 * The triangle to the south of a square in an Elongated Triangular Tiling
	 */
	trait Triangle2[SpaceClass] extends StrictElongatedTriangularSpace[SpaceClass] {
		def north:Option[Square[SpaceClass]]
		def southEast:Option[Triangle1[SpaceClass]]
		def southWest:Option[Triangle1[SpaceClass]]
		override def adjacentSpaces:Seq[StrictElongatedTriangularSpace[SpaceClass]] = Seq(north, southEast, southWest).flatMap{_.to[Seq]}
	}
}
