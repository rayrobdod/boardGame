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

import scala.collection.immutable.{Seq, Set, Map}

/**
 * A RectangularField is a set of [[com.rayrobdod.boardGame.RectangularSpace]]s,
 * such that each space is connected to adjacent spaces Euclidean-geometry wise.
 * 
 * 
 * @version 3.0.0
 * @see [[com.rayrobdod.boardGame.RectangularSpace]]
 */
object RectangularField
{
	/**
	 * @version next
	 * @since 3.0.0
	 */
	private final class RectangularFieldSpace[SpaceClass](
			private val fieldClasses:Map[RectangularFieldIndex, SpaceClass],
			private val myIndex:(Int, Int)
	) extends StrictRectangularSpace[SpaceClass] {
		private val (i,j) = myIndex
		private def getSpaceAt(i:RectangularFieldIndex):Option[StrictRectangularSpace[SpaceClass]] = {
			if (fieldClasses.contains(i)) {
				Some(new RectangularFieldSpace(fieldClasses, i))
			} else {None}
		}
		
		// RectangularSpace Implementation
		
		override val typeOfSpace:SpaceClass = fieldClasses(myIndex)
		override def left:Option[StrictRectangularSpace[SpaceClass]]  = this.getSpaceAt(i - 1, j)
		override def up:Option[StrictRectangularSpace[SpaceClass]]    = this.getSpaceAt(i, j - 1)
		override def right:Option[StrictRectangularSpace[SpaceClass]] = this.getSpaceAt(i + 1, j)
		override def down:Option[StrictRectangularSpace[SpaceClass]]  = this.getSpaceAt(i, j + 1)
		
		// Object Overrides
		
		// Until there's something that can apply to all RectangularSpaces, we're doing somehting specialized for this
		override def equals(other:Any):Boolean = other match {
			case other2:RectangularFieldSpace[_] => {
				(other2.fieldClasses == this.fieldClasses &&
						other2.myIndex == this.myIndex)
			}
			case _ => false
		}
		override def hashCode:Int = myIndex.hashCode
	}
	
	
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
		classes.map{x => ((x._1, new RectangularFieldSpace(classes, x._1) ))}
	}
}
