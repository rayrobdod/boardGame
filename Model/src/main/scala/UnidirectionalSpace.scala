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

import scala.collection.immutable.Seq


/**
 * A [[com.rayrobdod.boardGame.SpaceLike]] in which a player can continue in only one direction.
 * 
 * @group SpaceLike
 * 
 * @constructor
 * @tparam SpaceClass the domain object representing the properties of this space
 * @tparam Repr the type of space representing every other space reachable from this space
 */
trait UnidirectionalSpaceLike[SpaceClass, Repr <: SpaceLike[SpaceClass, Repr]] extends SpaceLike[SpaceClass, Repr] {
	/** The space that can be moved to after this one */
	def next:Option[Repr]
	
	/** The space that can be moved to after this one, as a list */
	final override def adjacentSpaces:Seq[Repr] = next.toList
}

/**
 * A [[com.rayrobdod.boardGame.SpaceLike]] in which a player can continue in only one direction
 * and in which all adjacent spaces are also UnidirectoinalSpaces
 * 
 * @group Unidirectional
 * 
 * @constructor
 * @tparam SpaceClass the domain object representing the properties of this space
 */
trait UnidirectionalSpace[SpaceClass] extends UnidirectionalSpaceLike[SpaceClass, UnidirectionalSpace[SpaceClass]] {
	
	/**
	 * Returns the space a player will reach when using a certain cost.
	 * @param availableCost the available for movement
	 * @param costFunction A function that defines the 'cost' of moving from the first space to the second space
	 * @return an Option containing a space if there are nextSpaces until the cost is used up.
	 */
	def spaceAfter(
		  availableCost:Int
		, costFunction:CostFunction[UnidirectionalSpace[SpaceClass]]
	):Option[UnidirectionalSpace[SpaceClass]] = {
		if (availableCost < 0) {None}
		else if (availableCost == 0) {Option(this)}
		else {
			next.map{x:UnidirectionalSpace[SpaceClass] =>
				val actualCost = costFunction(this, x)
				
				if (actualCost > availableCost) {None}
				else if (actualCost == availableCost) {Some(x)}
				else {x.spaceAfter(availableCost - actualCost, costFunction)}
			}.flatten
		}
	}
}

/**
 * A UnidirectionalSpace in which all abstract members are provided in the constructor
 * 
 * @group Unidirectional
 * 
 * @constructor
 * @tparam SpaceClass the domain object representing the properties of this space
 * @param typeOfSpace the class that defines how this space interacts with Tokens.
 * @param nextSpace The space a player will continue to after this one 
 */
final class UnidirectionalSpaceDirect[SpaceClass](
		  override val typeOfSpace:SpaceClass
		, override val next:Option[UnidirectionalSpace[SpaceClass]]
) extends UnidirectionalSpace[SpaceClass]
