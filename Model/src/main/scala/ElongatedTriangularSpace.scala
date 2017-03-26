package com.rayrobdod.boardGame

import scala.collection.immutable.Seq

/**
 * A tile in an elongated triangular tiling
 * @tparam SpaceClass the type of domain object representing the properties of this space 
 * @group ElongatedTriangular
 */
sealed trait StrictElongatedTriangularSpace[SpaceClass] extends Space[SpaceClass, StrictElongatedTriangularSpace[SpaceClass]] {
	import StrictElongatedTriangularSpace._
	
	/**
	 * Applies the function that matches this's type to this
	 * 
	 * @param sf the function to apply if this is a Square
	 * @param nf the function to apply if this is a Triangle1
	 * @param df the function to apply if this is a Triangle2
	 * @return the results of applying the corresponding function
	 */
	final def fold[A](
		  nf:Function1[Triangle1[SpaceClass], A]
		, sf:Function1[Square[SpaceClass], A]
		, df:Function1[Triangle2[SpaceClass], A]
	) = this match {
		case x:Square[SpaceClass] => sf(x)
		case x:Triangle1[SpaceClass] => nf(x)
		case x:Triangle2[SpaceClass] => df(x)
	}
}

/**
 * The three types of [[StrictElongatedTriangularSpace]]s
 * @group ElongatedTriangular
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
		final override def adjacentSpaces:Seq[StrictElongatedTriangularSpace[SpaceClass]] = Seq(south, northEast, northWest).flatMap{_.to[Seq]}
	}
	/**
	 * The triangle to the south of a square in an Elongated Triangular Tiling
	 */
	trait Triangle2[SpaceClass] extends StrictElongatedTriangularSpace[SpaceClass] {
		def north:Option[Square[SpaceClass]]
		def southEast:Option[Triangle1[SpaceClass]]
		def southWest:Option[Triangle1[SpaceClass]]
		final override def adjacentSpaces:Seq[StrictElongatedTriangularSpace[SpaceClass]] = Seq(north, southEast, southWest).flatMap{_.to[Seq]}
	}
}
