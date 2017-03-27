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
 * A set of spaces in which 
 * 
 * @group Generic
 * @constructor
 * @tparam SpaceClass the space model
 * @tparam Index the key used to specify a space from this field
 * @tparam SpaceType the spaces contained in this tiling
 * @param classes the mapping from indexies to space classes
 * @param warps the replacement targets after entering the space with the specified index
 * @param generator a function which will generate the field's spaces
 */
final class Room[SpaceClass, Index, SpaceType <: Space[SpaceClass, SpaceType]](
	classes:Map[Index, SpaceClass],
	private[Room] val warps:Map[Index, Function0[SpaceType]]
)(implicit
	generator:Room.SpaceGenerator[SpaceClass, Index, SpaceType]
) extends Tiling[SpaceClass, Index, SpaceType] {
	
	override def spaceClass(idx:Index):Option[SpaceClass] = classes.get(idx)
	override def mapIndex[A](f:Index => A):Seq[A] = classes.keySet.to[Seq].map(f)
	override def foreachIndex(f:Index => Unit):Unit = classes.keySet.foreach(f)
	
	override def space(idx:Index):Option[SpaceType] = this.spaceClass(idx).map{sc => generator.apply(sc, idx, this)}
}

/**
 * @group Generic
 */
object Room {
	
	trait SpaceGenerator[SpaceClass, Index, SpaceType <: Space[SpaceClass, SpaceType]] {
		def apply(sc:SpaceClass, index:Index, field:Room[SpaceClass, Index, SpaceType]):SpaceType
	}
	
	
	
	implicit def rectangularSpaceGenerator[SpaceClass]:SpaceGenerator[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]] = new RectangularSpaceGenerator[SpaceClass]
	
	private final class RectangularSpaceGenerator[SpaceClass] extends SpaceGenerator[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]] {
		override def apply(
				  sc:SpaceClass
				, index:RectangularIndex
				, field:Room[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]]
		):StrictRectangularSpace[SpaceClass] = {
			new MyRectangularSpace[SpaceClass](sc, index, field)
		}
		override def hashCode:Int = 26
		override def equals(other:Any):Boolean = other match {
			case other2:RectangularSpaceGenerator[_] => true
			case _ => false
		}
	}
	private final class MyRectangularSpace[SpaceClass](
			sc:SpaceClass,
			index:RectangularIndex,
			private val field:Room[SpaceClass, RectangularIndex, StrictRectangularSpace[SpaceClass]]
	) extends StrictRectangularSpace[SpaceClass] {
		private val (x, y) = index
		
		override def typeOfSpace:SpaceClass = sc
		
		override def west:Option[StrictRectangularSpace[SpaceClass]]  = {
			field.warps.get( ((x - 1, y)) ).map{_.apply}.orElse(field.space((x - 1, y)))
		}
		override def north:Option[StrictRectangularSpace[SpaceClass]] = {
			field.warps.get( ((x, y - 1)) ).map{_.apply}.orElse(field.space((x, y - 1)))
		}
		override def east:Option[StrictRectangularSpace[SpaceClass]]  = {
			field.warps.get( ((x + 1, y)) ).map{_.apply}.orElse(field.space((x + 1, y)))
		}
		override def south:Option[StrictRectangularSpace[SpaceClass]] = {
			field.warps.get( ((x, y + 1)) ).map{_.apply}.orElse(field.space((x, y + 1)))
		}
		
		override def toString:String = s"RectangularRoom.Space(typ = $typeOfSpace, x = $x, y = $y, field = $field)"
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
				, field:Room[SpaceClass, HorizontalHexagonalIndex, StrictHorizontalHexagonalSpace[SpaceClass]]
		):StrictHorizontalHexagonalSpace[SpaceClass] = {
			new MyHorizontalHexagonalSpace[SpaceClass](sc, index, field)
		}
		override def hashCode:Int = 27
		override def equals(other:Any):Boolean = other match {
			case other2:HorizontalHexagonalSpaceGenerator[_] => true
			case _ => false
		}
	}
	private final class MyHorizontalHexagonalSpace[SpaceClass](
			sc:SpaceClass,
			index:HorizontalHexagonalIndex,
			private val field:Room[SpaceClass, HorizontalHexagonalIndex, StrictHorizontalHexagonalSpace[SpaceClass]]
	) extends StrictHorizontalHexagonalSpace[SpaceClass] {
		private val (ew, nwse) = index
		
		override def typeOfSpace:SpaceClass = sc
		
		override def northwest:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = {
			val newidx = ((ew, nwse - 1)); field.warps.get(newidx).map{_.apply}.orElse(field.space(newidx))
		}
		override def southeast:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = {
			val newidx = ((ew, nwse + 1)); field.warps.get(newidx).map{_.apply}.orElse(field.space(newidx))
		}
		override def west:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = {
			val newidx = ((ew - 1, nwse)); field.warps.get(newidx).map{_.apply}.orElse(field.space(newidx))
		}
		override def east:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = {
			val newidx = ((ew + 1, nwse)); field.warps.get(newidx).map{_.apply}.orElse(field.space(newidx))
		}
		override def northeast:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = {
			val newidx = ((ew + 1, nwse - 1)); field.warps.get(newidx).map{_.apply}.orElse(field.space(newidx))
		}
		override def southwest:Option[StrictHorizontalHexagonalSpace[SpaceClass]] = {
			val newidx = ((ew - 1, nwse + 1)); field.warps.get(newidx).map{_.apply}.orElse(field.space(newidx))
		}
		
		override def toString:String = s"HorizontalHexagonalRoom.Space(typ = $typeOfSpace, ew = $ew, nwse = $nwse, field = $field)"
		override def hashCode:Int = ew * 31 + nwse
		override def equals(other:Any):Boolean = other match {
			case other2:MyHorizontalHexagonalSpace[_] =>
				other2.field == this.field &&
				other2.ew == this.ew &&
				other2.nwse == this.nwse
			case _ => false
		}
	}
	
}
