/*
	Deduction Tactics
	Copyright (C) 2012-2017  Raymond Dodge

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.rayrobdod.boardGame

import scala.collection.immutable.{Seq, Map}

/**
 * A set of spaces in which their connections are determined by the indexies
 * of spaceclasses in this field
 * 
 * @group Generic
 * @constructor
 * @tparam SpaceClass the space model
 * @tparam Index the key used to specify a space from this field
 * @tparam SpaceType the spaces contained in this tiling
 * @param classes the mapping from indexies to space classes
 * @param generator a function which will generate the field's spaces
 */
final class Field[SpaceClass, Index, SpaceType <: SpaceLike[SpaceClass, SpaceType]](
	private val classes:Map[Index, SpaceClass]
)(implicit
	private val generator:Field.SpaceGenerator[SpaceClass, Index, SpaceType]
) extends Tiling[SpaceClass, Index, SpaceType] {
	
	override def spaceClass(idx:Index):Option[SpaceClass] = classes.get(idx)
	override def mapIndex[A](f:Index => A):Seq[A] = classes.keySet.to[Seq].map(f)
	override def foreachIndex(f:Index => Unit):Unit = classes.keySet.foreach(f)
	
	override def space(idx:Index):Option[SpaceType] = this.spaceClass(idx).map{sc => generator.apply(sc, idx, this)}
	
	override def hashCode:Int = this.classes.hashCode
	override def equals(other:Any):Boolean = other match {
		case other2:Field[_, _, _] => {
			other2.classes == this.classes &&
			other2.generator == this.generator
		}
		case _ => false
	}
}

/**
 * A set of built-in [[SpaceGenerator]]s which may be pulled in by Field implicitly
 * @group Generic
 */
object Field {
	
	/**
	 * A function that generates field spaces of a particular shape
	 */
	trait SpaceGenerator[SpaceClass, Index, SpaceType <: SpaceLike[SpaceClass, SpaceType]] {
		def apply(sc:SpaceClass, index:Index, field:Field[SpaceClass, Index, SpaceType]):SpaceType
	}
	
	
	
	implicit def rectangularSpaceGenerator[SpaceClass]:SpaceGenerator[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]] = new RectangularSpaceGenerator[SpaceClass]
	
	private final class RectangularSpaceGenerator[SpaceClass] extends SpaceGenerator[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]] {
		override def apply(sc:SpaceClass, index:RectangularIndex, field:Field[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]]):StrictRectangularSpace[SpaceClass] = {
			new MyRectangularSpace[SpaceClass](sc, index, field)
		}
		override def hashCode:Int = 23
		override def equals(other:Any):Boolean = other match {
			case other2:RectangularSpaceGenerator[_] => true
			case _ => false
		}
	}
	private final class MyRectangularSpace[SpaceClass](
			sc:SpaceClass,
			index:RectangularIndex,
			private val field:Field[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]]
	) extends StrictRectangularSpace[SpaceClass] {
		private val (x, y) = index
		
		override def typeOfSpace:SpaceClass = sc
		
		override def west:Option[StrictRectangularSpace[SpaceClass]]  = field.space((x - 1, y))
		override def north:Option[StrictRectangularSpace[SpaceClass]] = field.space((x, y - 1))
		override def east:Option[StrictRectangularSpace[SpaceClass]]  = field.space((x + 1, y))
		override def south:Option[StrictRectangularSpace[SpaceClass]] = field.space((x, y + 1))
		
		override def toString:String = s"RectangularField.Space(typ = $typeOfSpace, x = $x, y = $y, field = $field)"
		override def hashCode:Int = x * 31 + y
		override def equals(other:Any):Boolean = other match {
			case other2:MyRectangularSpace[_] =>
				other2.field == this.field &&
				other2.x == this.x &&
				other2.y == this.y
			case _ => false
		}
	}
	
	
	implicit def horizontalHexagonalSpaceGenerator[SpaceClass]:SpaceGenerator[SpaceClass, HorizontalHexagonalIndex, StrictHorizontalHexagonalSpace[SpaceClass]] = new HorizontalHexagonalSpaceGenerator[SpaceClass]
	
	private final class HorizontalHexagonalSpaceGenerator[SpaceClass] extends SpaceGenerator[SpaceClass, HorizontalHexagonalIndex, StrictHorizontalHexagonalSpace[SpaceClass]] {
		override def apply(
			  sc:SpaceClass
			, index:HorizontalHexagonalIndex
			, field:Field[SpaceClass, HorizontalHexagonalIndex, StrictHorizontalHexagonalSpace[SpaceClass]]
		):StrictHorizontalHexagonalSpace[SpaceClass] = {
			new MyHorizontalHexagonalSpace[SpaceClass](sc, index, field)
		}
		override def hashCode:Int = 24
		override def equals(other:Any):Boolean = other match {
			case other2:HorizontalHexagonalSpaceGenerator[_] => true
			case _ => false
		}
	}
	
	private final class MyHorizontalHexagonalSpace[SpaceClass](
			sc:SpaceClass,
			index:HorizontalHexagonalIndex,
			private val field:Field[SpaceClass, HorizontalHexagonalIndex, StrictHorizontalHexagonalSpace[SpaceClass]]
	) extends StrictHorizontalHexagonalSpace[SpaceClass] {
		private val (ew, nwse) = index
		
		override def typeOfSpace:SpaceClass = sc
		
		override def northwest:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.space((ew, nwse - 1))
		override def southeast:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.space((ew, nwse + 1))
		override def west:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.space((ew - 1, nwse))
		override def east:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.space((ew + 1, nwse))
		override def northeast:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.space((ew + 1, nwse - 1))
		override def southwest:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.space((ew - 1, nwse + 1))
		
		override def toString:String = s"HorizontalHexagonalField.Space(typ = $typeOfSpace, ew = $ew, nwse = $nwse, field = $field)"
		override def hashCode:Int = ew * 31 + nwse
		override def equals(other:Any):Boolean = other match {
			case other2:MyHorizontalHexagonalSpace[_] =>
				other2.field == this.field &&
				other2.ew == this.ew &&
				other2.nwse == this.nwse
			case _ => false
		}
	}
	
	
	implicit def elongatedTriangularSpaceGenerator[SpaceClass]:SpaceGenerator[SpaceClass, ElongatedTriangularIndex, StrictElongatedTriangularSpace[SpaceClass]] = new ElongatedTriangularSpaceGenerator[SpaceClass]
	
	private final class ElongatedTriangularSpaceGenerator[SpaceClass] extends SpaceGenerator[SpaceClass, ElongatedTriangularIndex, StrictElongatedTriangularSpace[SpaceClass]] {
		override def apply(
			  sc:SpaceClass
			, index:ElongatedTriangularIndex
			, field:Field[SpaceClass, ElongatedTriangularIndex, StrictElongatedTriangularSpace[SpaceClass]]
		):StrictElongatedTriangularSpace[SpaceClass] = {
			index.typ match {
				case ElongatedTriangularType.Square => new MySquareElongatedTriangularSpace(sc, field, index.x, index.y)
				case ElongatedTriangularType.NorthTri => new MyTriangle1ElongatedTriangularSpace(sc, field, index.x, index.y)
				case ElongatedTriangularType.SouthTri => new MyTriangle2ElongatedTriangularSpace(sc, field, index.x, index.y)
			}
		}
		override def hashCode:Int = 25
		override def equals(other:Any):Boolean = other match {
			case other2:ElongatedTriangularSpaceGenerator[_] => true
			case _ => false
		}
	}
	
	private implicit class ElongatedTriangularFieldOps[SpaceClass](backing:Field[SpaceClass, ElongatedTriangularIndex, StrictElongatedTriangularSpace[SpaceClass]]) {
		def square(x:Int, y:Int):Option[StrictElongatedTriangularSpace.Square[SpaceClass]] = backing.spaceClass(ElongatedTriangularIndex(x, y, ElongatedTriangularType.Square)).map{sc => new MySquareElongatedTriangularSpace(sc, backing, x, y)}
		def northTri(x:Int, y:Int):Option[StrictElongatedTriangularSpace.Triangle1[SpaceClass]] = backing.spaceClass(ElongatedTriangularIndex(x, y, ElongatedTriangularType.NorthTri)).map{sc => new MyTriangle1ElongatedTriangularSpace(sc, backing, x, y)}
		def southTri(x:Int, y:Int):Option[StrictElongatedTriangularSpace.Triangle2[SpaceClass]] = backing.spaceClass(ElongatedTriangularIndex(x, y, ElongatedTriangularType.SouthTri)).map{sc => new MyTriangle2ElongatedTriangularSpace(sc, backing, x, y)}
	}
	
	private final class MySquareElongatedTriangularSpace[SpaceClass](
			override val typeOfSpace:SpaceClass,
			private val field:Field[SpaceClass, ElongatedTriangularIndex, StrictElongatedTriangularSpace[SpaceClass]],
			private val x:Int,
			private val y:Int
	) extends StrictElongatedTriangularSpace.Square[SpaceClass] {
		
		override def north:Option[StrictElongatedTriangularSpace.Triangle1[SpaceClass]] = field.northTri(x, y)
		override def south:Option[StrictElongatedTriangularSpace.Triangle2[SpaceClass]] = field.southTri(x, y)
		override def east:Option[StrictElongatedTriangularSpace.Square[SpaceClass]] = field.square(x + 1, y)
		override def west:Option[StrictElongatedTriangularSpace.Square[SpaceClass]] = field.square(x - 1, y)
		
		override def toString:String = s"ElongatedTriangularField.Square(typ = $typeOfSpace, x = $x, y = $y, field = ...)"
		override def hashCode:Int = x * 93 + y * 3
		override def equals(other:Any):Boolean = other match {
			case other2:MySquareElongatedTriangularSpace[_] =>
				other2.field == this.field &&
					other2.x == this.x &&
					other2.y == this.y
			case _ => false
		}
	}
	
	private final class MyTriangle1ElongatedTriangularSpace[SpaceClass](
		override val typeOfSpace:SpaceClass,
		private val field:Field[SpaceClass, ElongatedTriangularIndex, StrictElongatedTriangularSpace[SpaceClass]],
		private val x:Int,
		private val y:Int
	) extends StrictElongatedTriangularSpace.Triangle1[SpaceClass] {
		def south:Option[StrictElongatedTriangularSpace.Square[SpaceClass]] = field.square(x, y)
		def northEast:Option[StrictElongatedTriangularSpace.Triangle2[SpaceClass]] = field.southTri(x + (if (y % 2 == 0) {-1} else {0}), y - 1)
		def northWest:Option[StrictElongatedTriangularSpace.Triangle2[SpaceClass]] = field.southTri(x + (if (y % 2 == 0) {0} else {1}), y - 1)
		
		override def toString:String = s"ElongatedTriangularField.Triangle1(typ = $typeOfSpace, x = $x, y = $y, field = ...)"
		override def hashCode:Int = x * 93 + y * 3 + 1
		override def equals(other:Any):Boolean = other match {
			case other2:MyTriangle1ElongatedTriangularSpace[_] =>
				other2.field == this.field &&
					other2.x == this.x &&
					other2.y == this.y
			case _ => false
		}
	}
	
	private final class MyTriangle2ElongatedTriangularSpace[SpaceClass](
		override val typeOfSpace:SpaceClass,
		private val field:Field[SpaceClass, ElongatedTriangularIndex, StrictElongatedTriangularSpace[SpaceClass]],
		private val x:Int,
		private val y:Int
	) extends StrictElongatedTriangularSpace.Triangle2[SpaceClass] {
		def north:Option[StrictElongatedTriangularSpace.Square[SpaceClass]] = field.square(x, y)
		def southWest:Option[StrictElongatedTriangularSpace.Triangle1[SpaceClass]] = field.northTri(x + (if (y % 2 == 0) {0} else {1}), y + 1)
		def southEast:Option[StrictElongatedTriangularSpace.Triangle1[SpaceClass]] = field.northTri(x + (if (y % 2 == 0) {-1} else {0}), y + 1)
		
		override def toString:String = s"ElongatedTriangularField.Triangle2(typ = $typeOfSpace, x = $x, y = $y, field = ...)"
		override def hashCode:Int = x * 93 + y * 3 + 2
		override def equals(other:Any):Boolean = other match {
			case other2:MyTriangle2ElongatedTriangularSpace[_] =>
				other2.field == this.field &&
					other2.x == this.x &&
					other2.y == this.y
			case _ => false
		}
	}
}
