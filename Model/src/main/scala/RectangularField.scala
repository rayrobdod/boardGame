/*
	Deduction Tactics
	Copyright (C) 2012-2015  Raymond Dodge

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

/* A rectangular tesselation */
/**
 * A RectangularField is a set of [[com.rayrobdod.boardGame.StrictRectangularSpace]]s,
 * such that each space is connected to adjacent spaces Euclidean-geometry wise
 * and that the spaces are arranged in a n×m grid.
 * 
 * An examle of what this kind of board is appropriate for is the average turn-based strategy
 * 
 * 
 * @since 4.0
 * @see [[com.rayrobdod.boardGame.StrictRectangularSpace]]
 */
final class RectangularField[SpaceClass](
	private val classes:Map[(Int, Int), SpaceClass]
) extends RectangularTilable[SpaceClass] {
	
	override def getSpaceAt(x:Int, y:Int):Option[StrictRectangularSpace[SpaceClass]] = classes.get( ((x,y)) ).map{sc => new Space(x, y)}
	
	override def mapIndex[A](f:((Int,Int)) => A):Seq[A] = classes.keySet.to[Seq].map(f)
	override def foreachIndex(f:((Int,Int)) => Unit):Unit = classes.keySet.foreach(f)
	
	private final class Space(private val x:Int, private val y:Int) extends StrictRectangularSpace[SpaceClass] {
		private val field = RectangularField.this
		override def typeOfSpace:SpaceClass = RectangularField.this.classes.apply( ((x,y)) )
		
		override def west:Option[StrictRectangularSpace[SpaceClass]]  = RectangularField.this.getSpaceAt(x - 1, y)
		override def north:Option[StrictRectangularSpace[SpaceClass]] = RectangularField.this.getSpaceAt(x, y - 1)
		override def east:Option[StrictRectangularSpace[SpaceClass]]  = RectangularField.this.getSpaceAt(x + 1, y)
		override def south:Option[StrictRectangularSpace[SpaceClass]] = RectangularField.this.getSpaceAt(x, y + 1)
		
		override def toString:String = s"RectangularField.Space(typ = $typeOfSpace, x = $x, y = $y, field = ${RectangularField.this})"
		override def hashCode:Int = x * 31 + y
		override def equals(other:Any):Boolean = {
			if (other.isInstanceOf[Space]) {
				val other2 = other.asInstanceOf[Space]
				other2.field == this.field &&
					other2.x == this.x &&
					other2.y == this.y
			} else {
				false
			}
		}
	}
	
	override def hashCode:Int = this.classes.hashCode
	override def equals(other:Any):Boolean = other match {
		case other2:RectangularField[_] => {
			other2.classes == this.classes
		}
		case _ => false
	}
}

object RectangularField {
	
	/**
	 * A factory method for Rectangular Fields
	 * 
	 * It will convert the locations in the array to indexes by using the
	 * inner array as the first dimension and the outer array as the
	 * first dimension.
	 * 
	 * It sounds weird, but it matches the most common ways of making a
	 * Seq[Seq[_]]. Both OpenCSV and direct inline have this endianess.
	 * 
	 * An example is, given the below sequence, `g` would be be considered
	 * at index (0, 2) by this function. Even though normally, that element
	 * would be accessed by (2)(0).
	 * {{{
	 *     Seq( Seq( a, b, c ),
	 *          Seq( d, e, f ),
	 *          Seq( g, h, i )
	 *     )
	 * }}}
	 * @param classes the Space Classes making up the field
	 * @version 3.0.0
	 */
	def apply[SpaceClass](classes:Seq[Seq[SpaceClass]]):RectangularField[SpaceClass] = this.apply(
		
		classes.zipWithIndex.map({(classSeq:Seq[SpaceClass], j:Int) =>
			classSeq.zipWithIndex.map({(clazz:SpaceClass, i:Int) => 
				(i, j) -> clazz
			}.tupled)
		}.tupled).flatten.toMap
	)
	
	/**
	 * A factory method for Rectangular Fields
	 * @since 3.0.0
	 */
	def apply[SpaceClass](classes:Map[RectangularFieldIndex, SpaceClass]):RectangularField[SpaceClass] = {
		new RectangularField(classes)
	}
}
