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
 * A HorizontalHexagonalSpace is a set of [[com.rayrobdod.boardGame.StrictHorizontalHexagonalSpace]]s,
 * such that each space is connected to adjacent spaces Euclidean-geometry wise.
 * 
 * The first coordinate is the east/west coordinate, and the second coordinate is the NW/SE coordinate.
 * 
 * @since 4.0
 * @see [[com.rayrobdod.boardGame.StrictHorizontalHexagonalSpace]]
 */
final class HorizontalHexagonalField[SpaceClass](
		private val classes:Map[(Int, Int), SpaceClass]
) extends HorizontalHexagonalTilable[SpaceClass] {
	
	override def getSpaceAt(ew:Int, nwse:Int):Option[StrictHorizontalHexagonalSpace[SpaceClass]] = {
		classes.get( ((ew, nwse)) ).map{sc => new HorizontalHexagonalField.Space(this, ew, nwse)}
	}
	
	override def mapIndex[A](f:((Int,Int)) => A):Seq[A] = classes.keySet.to[Seq].map(f)
	override def foreachIndex(f:((Int, Int)) => Unit):Unit = classes.keySet.foreach(f)
	
	override def hashCode:Int = this.classes.hashCode
	override def equals(other:Any):Boolean = other match {
		case other2:HorizontalHexagonalField[_] => {
			other2.classes == this.classes
		}
		case _ => false
	}
}

private object HorizontalHexagonalField {
	
	private final class Space[SpaceClass](
		private val field:HorizontalHexagonalField[SpaceClass],
		private val ew:Int,
		private val nwse:Int
	) extends StrictHorizontalHexagonalSpace[SpaceClass] {
		override def typeOfSpace:SpaceClass = field.classes.apply( ((ew, nwse)) )
		
		override def northwest:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.getSpaceAt(ew, nwse - 1)
		override def southeast:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.getSpaceAt(ew, nwse + 1)
		override def west:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.getSpaceAt(ew - 1, nwse)
		override def east:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.getSpaceAt(ew + 1, nwse)
		override def northeast:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.getSpaceAt(ew + 1, nwse - 1)
		override def southwest:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = field.getSpaceAt(ew - 1, nwse + 1)
		
		override def toString:String = s"RectangularField.Space(typ = $typeOfSpace, ew = $ew, nwse = $nwse, field = $field)"
		override def hashCode:Int = ew * 31 + nwse
		override def equals(other:Any):Boolean = {
			if (other.isInstanceOf[Space[_]]) {
				val other2 = other.asInstanceOf[Space[_]]
				other2.field == this.field &&
					other2.ew == this.ew &&
					other2.nwse == this.nwse
			} else {
				false
			}
		}
	}
	
}
