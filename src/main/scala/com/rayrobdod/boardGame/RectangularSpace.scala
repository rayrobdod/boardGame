package com.rayrobdod.boardGame

import scala.{Function0 => Future}

/**
 * A [[com.rayrobdod.boardGame.Space]] in a rectangular board, such that
 * it can have zero or one bordering space in each of the four cardinal directions.
 * 
 * Ecludian geometery says that {@code this.left.right == this}, similarly for each other
 * direction, but this makes no specific checks to that effect.
 * 
 * @author Raymond Dodge
 * @version ?? ??? 2011
 * @version 29 Sept 2011 - modified with super Space; also, no longer abstract
 * @version 15 Dec 2011 - moved from {@code net.verizon.rayrobdod.boardGame} to {@code com.rayrobdod.boardGame}
 * @version 2013 Aug 06 - Apparently 'Future' in scala means 'there's a thing that I will want in the future', not 
 			'there's a thing that will become availiable in the futrue'. Either way, scala.parellel.Future no longer exists,
 			as of Scala 2.11. Using `scala.Function0` instead.
 * @param typeOfSpace the class that defines how this space interacts with Tokens.
 * @param *Future a future that returns the spaces that border this.
 * @see [[com.rayrobdod.boardGame.RectangularField]]
 */
class RectangularSpace(
		typeOfSpace:SpaceClass,
		leftFuture:Future[Option[Space]],
		upFuture:Future[Option[Space]],
		rightFuture:Future[Option[Space]],
		downFuture:Future[Option[Space]]) extends Space(typeOfSpace)
{
	lazy val left:Option[Space] = leftFuture()
	lazy val up:Option[Space] = upFuture()
	lazy val right:Option[Space] = rightFuture()
	lazy val down:Option[Space] = downFuture()
	
	override def adjacentSpaces:Set[Space] = {
		val optionSpaces = Set(left,up,right,down)
		val someSpaces = optionSpaces - None
		someSpaces.map{_.get}
	}
}
