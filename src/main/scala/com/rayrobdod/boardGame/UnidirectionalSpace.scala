/*
	Deduction Tactics
	Copyright (C) 2012-2013  Raymond Dodge

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

import scala.collection.immutable.{Set, List, Map => IMap}
import scala.collection.mutable.{Map => MMap}


/**
 * A [[com.rayrobdod.boardGame.Space]] in which a player can continue in only one direction.
 * 
 * @author Raymond Dodge
 * @version 3.0.0 rename from UnaryMovement
 * 
 * @constructor
 * @param typeOfSpace the class that defines how this space interacts with Tokens.
 * @param nextSpace The space a player will continue to after this one 
 */
final class UnidirectionalSpace[A](val typeOfSpace:A, val nextSpace:Option[UnidirectionalSpace[A]]) extends Space[A]
{
	/**
	 * Returns a singleton set containing {@link #nextSpace} iff nextSpace is not None; else returns an empty set.
	 */
	override def adjacentSpaces:Set[UnidirectionalSpace[A]] = nextSpace.toList.toSet
	
	/**
	 * Returns the space a player will reach when using a certain cost.
	 * @param availiableCost the available for movement
	 * @return an Option containing a space if there are nextSpaces until the cost is used up.
	 * @throws ClassCastException if one of the next spaces is not an instance of UnaryMovement, which presumably means
				there are multiple available adjacentSpaces.
	 */
	def spaceAfter(availiableCost:Int, costFunction:Space.CostFunction[A]):Option[UnidirectionalSpace[_]] =
	{
		if (availiableCost == 0) Option(this)
		else
		{
			nextSpace match
			{
				case None => None
				case Some(x:UnidirectionalSpace[_]) =>
				{
					val thisCost = costFunction(this, x)
					
					if (availiableCost >= thisCost) x.spaceAfter(availiableCost - thisCost, costFunction)
					else None
				}
				case Some(_) => throw new ClassCastException("Encountered something that is not a UnarySpace; ")
			}
		}
	}
}
