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

import scala.collection.immutable.LinearSeq

/**
 * Noticed that the UnaryMovement Spaces look like linked list nodes
 * So, I made a linked list around the UnaryMovement Spaces.
 * 
 * I haven't grown out of my habit of making useless collections yet…
 * 
 * @group Unidirectional
 * 
 */
final class UnidirectionalSpaceSeq[SpaceClass](override val headOption:Option[UnidirectionalSpace[SpaceClass]])
			extends LinearSeq[UnidirectionalSpace[SpaceClass]] 
{
	def this(head:UnidirectionalSpace[SpaceClass]) = this(Option(head))
	
	override def head:UnidirectionalSpace[SpaceClass] = headOption.get
	override def isEmpty:Boolean = (headOption == None) // assume no nulls, right?
	override def tail:UnidirectionalSpaceSeq[SpaceClass] = {
		// TRYTHIS test to see if it is worth making this a lazy val: Infinite Seq is probably possible (e.g. Monopoly)
		if (this.isEmpty) {
			throw new UnsupportedOperationException("Cannot get tail of empty list")
		} else {
			val next:Option[UnidirectionalSpace[SpaceClass]] = headOption.flatMap(_.nextSpace)
			new UnidirectionalSpaceSeq[SpaceClass](next)
		}
	}
	
	override def apply(i:Int):UnidirectionalSpace[SpaceClass] = {
		if (this.isEmpty) {throw new IndexOutOfBoundsException("Too high an index called")}
		else if (i < 0) {throw new IndexOutOfBoundsException("index less than zero called: " + i)}
		else if (i == 0) {head}
		else {tail.apply(i - 1)}
	}
	
	override def length:Int = {
		if (this.isEmpty) {0} else {tail.length + 1}
	}
}
