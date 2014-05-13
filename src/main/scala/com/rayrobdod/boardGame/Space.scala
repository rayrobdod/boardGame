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
import Space.CostFunction

/**
 * A spot on a board game board
 * @version 3.0.0
 * @tparam A the type of spaceclass used by this class
 */
trait Space[A] {
	/**
	 * an object that defines how this space interacts with tokens
	 */
	def typeOfSpace:A
	
	/**
	 * A space that is treated as adjacent to this one; such as a tile that can be directly
	 * accessed from this tile without passing through other tiles
	 */
	def adjacentSpaces:Traversable[_ <: Space[A]]
	
	
	
	/************** the implemented stuff ****************/
	
	
	/**
	 * Finds all the spaces within a certain movementCost of this one.
	 * 
	 * @param availableCost the amount of movementCost available
	 * @return a set of all spaces that can be reached from this by moving into an adjacentTile
			using movementCost or less
	 */
	def spacesWithin(availableCost:Int, costFunction:CostFunction[A]):Set[Space[A]] =
	{
		if (availableCost < 0) Set.empty
		else if (availableCost == 0) Set(this)
		else
		{
			Set(this) ++ adjacentSpaces.flatMap((space:Space[A]) => {
				space.spacesWithin(
					availableCost - costFunction(this, space),
					costFunction
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
	def spacesAfter(availableCost:Int, costFunction:CostFunction[A]):Set[Space[A]] =
	{
		if (availableCost < 0) Set.empty
		else if (availableCost == 0) Set(this)
		else
		{
			Set.empty ++ adjacentSpaces.flatMap((space:Space[A]) => {
				space.spacesAfter(
					availableCost - costFunction(this, space),
					costFunction
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
	def distanceTo(other:Space[A], costFunction:CostFunction[A]):Int =
	{
		val closed = MMap.empty[Space[A], Int]
		val open = MMap.empty[Space[A], Int]
		var checkingTile:(Space[A], Int) = ((this, 0))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (checkingTile._1 != other)
		{
			open -= checkingTile._1
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Space[A]) => {
				val newDistance = checkingTile._2 + costFunction(checkingTile._1, s)
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
	def pathTo(other:Space[A], costFunction:CostFunction[A]):List[Space[A]] =
	{
		val closed = MMap.empty[Space[A], (Int, Space[A])]
		val open = MMap.empty[Space[A], (Int, Space[A])]
		var checkingTile:(Space[A], (Int, Space[A])) = ((this, ((0, null )) ))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (checkingTile._1 != other)
		{
			open -= checkingTile._1
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Space[A]) => {
				val newDistance = checkingTile._2._1 + costFunction(checkingTile._1, s)
				val oldDistance = open.getOrElse(s, ((Integer.MAX_VALUE, None)) )._1
				
				if (newDistance < oldDistance) open += ((s, ((newDistance, checkingTile._1)) ))
			}}
			
			checkingTile = open.minBy{_._2._1}
		}
		open -= checkingTile._1
		closed += checkingTile
		
		var currentTile:Space[A] = other
		var returnValue:List[Space[A]] = other :: Nil
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
	 * @param costFunction the function defining the cost to move from one space to another
	 * @return A map where the key is a space, and the value is the cost from here to the key, and how to get there.
	 */
	def rawDijkstraData(costFunction:CostFunction[A]):IMap[Space[A], (Int, Space[A])] = {
		
		val closed = MMap.empty[Space[A], (Int, Space[A])]
		val open = MMap.empty[Space[A], (Int, Space[A])]
		var checkingTile:(Space[A], (Int, Space[A])) = ((this, ((0, null )) ))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (! open.isEmpty || checkingTile._1 == this)
		{
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Space[A]) => {
				val newDistance = checkingTile._2._1 + costFunction(checkingTile._1, s)
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

object Space {
	
	/** A function that defines the 'cost' of moving from the first space to the second space */
	type CostFunction[A] = Function2[Space[A], Space[A], Int]
	
	/** A CostFunction with a constant (1) cost for every move. */
	val constantCostFunction:CostFunction[_] = {(from:Space[_], to:Space[_]) => 1}
	
}
