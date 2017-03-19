package com.rayrobdod.boardGame

import scala.collection.immutable.Seq

/** A tile in an elongated triangular tesselation */
sealed trait StrictElongatedTriangularSpace[SpaceClass] extends Space[SpaceClass, StrictElongatedTriangularSpace[SpaceClass]]
object StrictElongatedTriangularSpace {
	trait Square[SpaceClass] extends StrictElongatedTriangularSpace[SpaceClass] with RectangularSpace[SpaceClass, StrictElongatedTriangularSpace[SpaceClass]] {
		override def north:Option[Triangle1[SpaceClass]]
		override def south:Option[Triangle2[SpaceClass]]
		override def east:Option[Square[SpaceClass]]
		override def west:Option[Square[SpaceClass]]
	}
	trait Triangle1[SpaceClass] extends StrictElongatedTriangularSpace[SpaceClass] {
		def south:Option[Square[SpaceClass]]
		def northEast:Option[Triangle2[SpaceClass]]
		def northWest:Option[Triangle2[SpaceClass]]
		override def adjacentSpaces:Seq[StrictElongatedTriangularSpace[SpaceClass]] = Seq(south, northEast, northWest).flatMap{_.to[Seq]}
	}
	trait Triangle2[SpaceClass] extends StrictElongatedTriangularSpace[SpaceClass] {
		def north:Option[Square[SpaceClass]]
		def southEast:Option[Triangle1[SpaceClass]]
		def southWest:Option[Triangle1[SpaceClass]]
		override def adjacentSpaces:Seq[StrictElongatedTriangularSpace[SpaceClass]] = Seq(north, southEast, southWest).flatMap{_.to[Seq]}
	}
}
