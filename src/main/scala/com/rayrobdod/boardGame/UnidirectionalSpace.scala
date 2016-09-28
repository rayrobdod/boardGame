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

import scala.collection.immutable.{Set, List, Map}


/**
 * A [[com.rayrobdod.boardGame.Space]] in which a player can continue in only one direction.
 * 
 * @author Raymond Dodge
 * @version 3.0.0 rename from UnaryMovement
 * 
 * @constructor
 * @tparam SpaceClass the domain object representing the properties of this space
 * @param typeOfSpace the class that defines how this space interacts with Tokens.
 * @param nextSpace The space a player will continue to after this one 
 */
final class UnidirectionalSpace[SpaceClass](override val typeOfSpace:SpaceClass, val nextSpace:Option[UnidirectionalSpace[SpaceClass]]) extends Space[SpaceClass]
{
	/**
	 * Returns a singleton set containing {@link #nextSpace} iff nextSpace is not None; else returns an empty set.
	 */
	override def adjacentSpaces:Set[UnidirectionalSpace[SpaceClass]] = nextSpace.toList.toSet
	
	/**
	 * Returns the space a player will reach when using a certain cost.
	 * @param availableCost the available for movement
	 * @param costFunction A function that defines the 'cost' of moving from the first space to the second space
	 * @return an Option containing a space if there are nextSpaces until the cost is used up.
	 * @throws ClassCastException if one of the next spaces is not an instance of UnaryMovement, which presumably means
				there are multiple available adjacentSpaces.
	 */
	def spaceAfter(availableCost:Int, costFunction:Space.CostFunction[SpaceClass]):Option[UnidirectionalSpace[_]] =
	{
		if (availableCost < 0) {None}
		else if (availableCost == 0) {Option(this)}
		else {
			nextSpace.map{x:UnidirectionalSpace[SpaceClass] =>
				val actualCost = costFunction(this, x)
				
				if (actualCost > availableCost) {None}
				else if (actualCost == availableCost) {Some(x)}
				else {x.spaceAfter(availableCost - actualCost, costFunction)}
			}.flatten.headOption
			// `headOption` because scala_2.9.
		}
	}
}
