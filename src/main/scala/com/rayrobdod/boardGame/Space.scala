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
 * A spot on a board game board
 * 
 * @author Raymond Dodge
 *
 * @constructor
 * @param typeOfSpace the class that defines how this space interacts with Tokens.
 * @see [[com.rayrobdod.boardGame.SpaceClass]] defines the way this interacts with tokens
 */
abstract class Space(val typeOfSpace:SpaceClass)
{
	/**
	 * A space that is treated as adjacent to this one; such as a tile that can be directly
	 * accessed from this tile without passing through other tiles
	 */
	def adjacentSpaces:Traversable[Space]
	
	/**
	 * Finds all the spaces within a certain movementCost of this one.
	 * 
	 * @param availableCost the amount of movementCost available
	 * @return a set of all spaces that can be reached from this by moving into an adjacentTile
			using movementCost or less
	 */
	def spacesWithin(availableCost:Int, token:Token, costType:TypeOfCost):Set[Space] =
	{
		if (availableCost < 0) Set.empty
		else if (availableCost == 0) Set(this)
		else
		{
			Set(this) ++ adjacentSpaces.flatMap((space:Space) => {
				space.spacesWithin(
					availableCost - space.typeOfSpace.cost(token, costType),
					token,
					costType
				)
			})
		}
	}
	
	/**
	 * Finds all the spaces that take exactly movementCost to get To.
	 * 
	 * @param availableCost the amount of movementCost available
	 * @return a set of all spaces that can be reached from this by moving into an adjacentTile
			using exactly movementCost
	 */
	def spacesAfter(availableCost:Int, token:Token, costType:TypeOfCost):Set[Space] =
	{
		if (availableCost < 0) Set.empty
		else if (availableCost == 0) Set(this)
		else
		{
			Set.empty ++ adjacentSpaces.flatMap((space:Space) => {
				space.spacesAfter(
					availableCost - space.typeOfSpace.cost(token, costType),
					token,
					costType
				)
			})
		}
	}
	
	/**
	 * Finds the distance between this Space and another Space.
	 * This is Dijkstra's algorithm, as the spaces aren't allowed to know where they are in relation
	 * they are to each other.
	 * 
	 * @param other the space to find the movementCost required to get to
	 * @return the movementCost required to get from this space to other
	 */
	def distanceTo(other:Space, token:Token, costType:TypeOfCost):Int =
	{
		val closed = MMap.empty[Space, Int]
		val open = MMap.empty[Space, Int]
		var checkingTile:(Space, Int) = ((this, 0))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (checkingTile._1 != other)
		{
			open -= checkingTile._1
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Space) => {
				val newDistance = checkingTile._2 + s.typeOfSpace.cost(token, costType)
				val oldDistance = open.getOrElse(s, Integer.MAX_VALUE)
				
				if (newDistance < oldDistance) open += ((s, newDistance))
			}}
			
			checkingTile = open.minBy{_._2}
		}
		return checkingTile._2
	}
	
	/**
	 * Finds the shortest path from this space to another space
	 * This is Dijkstra's algorithm, as the spaces aren't allowed to know where they are in relation
	 * they are to each other.
	 * 
	 * This short-circuts when it finds the desired space, and so is more efficient than pathToEverywhere
	 * which searches the whole field
	 * 
	 * @param other the space to find the movementCost required to get to
	 * @return the a list of spaces such that the first space is this, the last space is other, and
	 			the movementcost between the two is minimal
	 */
	def pathTo(other:Space, token:Token, costType:TypeOfCost):List[Space] =
	{
		val closed = MMap.empty[Space, (Int, Space)]
		val open = MMap.empty[Space, (Int, Space)]
		var checkingTile:(Space, (Int, Space)) = ((this, ((0, null )) ))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (checkingTile._1 != other)
		{
			open -= checkingTile._1
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Space) => {
				val newDistance = checkingTile._2._1 + s.typeOfSpace.cost(token, costType)
				val oldDistance = open.getOrElse(s, ((Integer.MAX_VALUE, None)) )._1
				
				if (newDistance < oldDistance) open += ((s, ((newDistance, checkingTile._1)) ))
			}}
			
			checkingTile = open.minBy{_._2._1}
		}
		open -= checkingTile._1
		closed += checkingTile
		
		var currentTile:Space = other
		var returnValue:List[Space] = other :: Nil
		while ((closed(currentTile)._2) != null)
		{
			currentTile = closed(currentTile)._2
			returnValue = currentTile :: returnValue
		}
		return returnValue
	}
	
	/**
	 * Returns the raw Dijkstra's algorithm data
	 * 
	 * @param token the token that is moving from this space to everywhere
	 * @return A map where the key is a space, and the value is the cost from here to the key, and how to get there.
	 */
	def pathToEverywhere(token:Token, costType:TypeOfCost):IMap[Space, (Int, Space)] =
	{
		val closed = MMap.empty[Space, (Int, Space)]
		val open = MMap.empty[Space, (Int, Space)]
		var checkingTile:(Space, (Int, Space)) = ((this, ((0, null )) ))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (! open.isEmpty || checkingTile._1 == this)
		{
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Space) => {
				val newDistance = checkingTile._2._1 + s.typeOfSpace.cost(token, costType)
				val oldDistance = open.getOrElse(s, ((Integer.MAX_VALUE, None)) )._1
				
				if (newDistance < oldDistance) open += ((s, ((newDistance, checkingTile._1)) ))
			}}
			
			checkingTile = open.minBy{_._2._1}
			open -= checkingTile._1
		}
		closed += checkingTile
		
		return IMap.empty ++ closed
	}
}
