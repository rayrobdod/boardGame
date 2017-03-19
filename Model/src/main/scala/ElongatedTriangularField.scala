package com.rayrobdod.boardGame

import scala.collection.immutable.Seq
import ElongatedTriangularField._
import StrictElongatedTriangularSpace._

/** An elongated triangular tesselation */
final class ElongatedTriangularField[SpaceClass](
	private val offset:ElongatedTriangularField.HalfRowOffset,
	private val classes:Map[(Int, Int), ElongatedTriangularField.SpaceClassTriple[SpaceClass]]
) {
	def getSquareAt(x:Int, y:Int):Option[Square[SpaceClass]] = classes.get( ((x,y)) ).map{sc => new MySquare(this, x, y)}
	private def getSquareAt(xy:(Int, Int)):Option[Square[SpaceClass]] = this.getSquareAt(xy._1, xy._2)
}

object ElongatedTriangularField {
	
	private final class MySquare[SpaceClass](
		private val field:ElongatedTriangularField[SpaceClass],
		private val x:Int,
		private val y:Int
	) extends StrictElongatedTriangularSpace.Square[SpaceClass] {
		
		override def typeOfSpace:SpaceClass = field.classes( ((x,y)) ).square
		override def north:Option[Triangle1[SpaceClass]] = Option(new MyTriangle1(field,x,y))
		override def south:Option[Triangle2[SpaceClass]] = Option(new MyTriangle2(field,x,y))
		override def east:Option[Square[SpaceClass]] = field.getSquareAt(x - 1, y)
		override def west:Option[Square[SpaceClass]] = field.getSquareAt(x + 1, y)
		
		override def toString:String = s"ElongatedTriangularField.Square(typ = $typeOfSpace, x = $x, y = $y, field = ...)"
		override def hashCode:Int = x * 93 + y * 3
		override def equals(other:Any):Boolean = other match {
			case other2:MySquare[_] =>
				other2.field == this.field &&
					other2.x == this.x &&
					other2.y == this.y
			case _ => false
		}
	}
	
	private final class MyTriangle1[SpaceClass](
		private val field:ElongatedTriangularField[SpaceClass],
		private val x:Int,
		private val y:Int
	) extends StrictElongatedTriangularSpace.Triangle1[SpaceClass] {
		override def typeOfSpace:SpaceClass = field.classes( ((x,y)) ).northTri
		def south:Option[Square[SpaceClass]] = Option(new MySquare(field,x,y))
		def northEast:Option[Triangle2[SpaceClass]] = field.getSquareAt(field.offset.northEast(x, y)).flatMap{_.south}
		def northWest:Option[Triangle2[SpaceClass]] = field.getSquareAt(field.offset.northWest(x, y)).flatMap{_.south}
		
		override def toString:String = s"ElongatedTriangularField.Triangle1(typ = $typeOfSpace, x = $x, y = $y, field = ...)"
		override def hashCode:Int = x * 93 + y * 3 + 1
		override def equals(other:Any):Boolean = other match {
			case other2:MyTriangle1[_] =>
				other2.field == this.field &&
					other2.x == this.x &&
					other2.y == this.y
			case _ => false
		}
	}
	private final class MyTriangle2[SpaceClass](
		private val field:ElongatedTriangularField[SpaceClass],
		private val x:Int,
		private val y:Int
	) extends StrictElongatedTriangularSpace.Triangle2[SpaceClass] {
		override def typeOfSpace:SpaceClass = field.classes( ((x,y)) ).southTri
		def north:Option[Square[SpaceClass]] = Option(new MySquare(field,x,y))
		def southEast:Option[Triangle1[SpaceClass]] = field.getSquareAt(field.offset.southEast(x, y)).flatMap{_.north}
		def southWest:Option[Triangle1[SpaceClass]] = field.getSquareAt(field.offset.southWest(x, y)).flatMap{_.north}
		
		override def toString:String = s"ElongatedTriangularField.Triangle2(typ = $typeOfSpace, x = $x, y = $y, field = ...)"
		override def hashCode:Int = x * 93 + y * 3 + 2
		override def equals(other:Any):Boolean = other match {
			case other2:MyTriangle2[_] =>
				other2.field == this.field &&
					other2.x == this.x &&
					other2.y == this.y
			case _ => false
		}
	}
	
	
	
	
	
	final case class SpaceClassTriple[SpaceClass](
		val square:SpaceClass,
		val northTri:SpaceClass,
		val southTri:SpaceClass
	)
	
	sealed trait HalfRowOffset {
		def northEastXOffset(myY:Int):Int
		def northEast(myX:Int, myY:Int):(Int, Int) = ((myX + northEastXOffset(myY), myY - 1))
		def northWest(myX:Int, myY:Int):(Int, Int) = ((myX + (1 + northEastXOffset(myY)), myY - 1))
		def southEast(myX:Int, myY:Int):(Int, Int) = ((myX + northEastXOffset(myY), myY + 1))
		def southWest(myX:Int, myY:Int):(Int, Int) = ((myX + (1 + northEastXOffset(myY)), myY + 1))
	}
	object HalfRowOffset {
		object EvenIsEastOfOdd extends HalfRowOffset {
			override def northEastXOffset(myY:Int):Int = if (myY % 2 == 0) {-1} else {0}
		}
		object EvenIsWestOfOdd extends HalfRowOffset {
			override def northEastXOffset(myY:Int):Int = if (myY % 2 == 0) {0} else {-1}
		}
	}
}
