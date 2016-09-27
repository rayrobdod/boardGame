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

import scala.collection.immutable.{Set, List, Map => IMap}
import scala.collection.mutable.{Map => MMap}
import Space.CostFunction

/**
 * A spot on a board game board
 * @version 3.0.0
 * @tparam SpaceClass the type of domain object representing the properties of this space 
 */
trait Space[SpaceClass] {
	/**
	 * the domain object  representing the properties of this space 
	 */
	def typeOfSpace:SpaceClass
	
	/**
	 * A space that is treated as adjacent to this one; such as a tile that can be directly
	 * accessed from this tile without passing through other tiles
	 */
	def adjacentSpaces:Traversable[_ <: Space[SpaceClass]]
	
	
	
	/************** the implemented stuff ****************/
	
	
	/**
	 * Returns a set of all spaces that are `avaliableCost` or less movement away according to the `costFunction`
	 * 
	 * @param availableCost the amount of "movement" available to spend
	 * @param costFunction A function that defines the 'cost' of moving from the first space to the second space
	 * @return a set of all spaces that are `avaliableCost` or less spaces away according to the `costFunction`
	 */
	def spacesWithin(availableCost:Int, costFunction:CostFunction[SpaceClass]):Set[Space[SpaceClass]] = {
		if (availableCost < 0) {Set.empty}
		else if (availableCost == 0) {Set(this)}
		else {
			Set(this) ++ adjacentSpaces.flatMap((space:Space[SpaceClass]) => {
				space.spacesWithin(
					availableCost - costFunction(this, space),
					costFunction
				)
			})
		}
	}
	
	/**
	 * Returns a set of all spaces that are exactly `avaliableCost` movement away according to the `costFunction`
	 * 
	 * @param availableCost the amount of "movement" available to spend
	 * @param costFunction A function that defines the 'cost' of moving from the first space to the second space
	 * @return Returns a set of all spaces that are exactly `avaliableCost` "movement" away according to the `costFunction`
	 */
	def spacesAfter(availableCost:Int, costFunction:CostFunction[SpaceClass]):Set[Space[SpaceClass]] = {
		if (availableCost < 0) {Set.empty}
		else if (availableCost == 0) {Set(this)}
		else {
			Set.empty ++ adjacentSpaces.flatMap((space:Space[SpaceClass]) => {
				space.spacesAfter(
					availableCost - costFunction(this, space),
					costFunction
				)
			})
		}
	}
	
	/**
	 * Finds the smallest movement cost between this Space and another Space.
	 * 
	 * This is Dijkstra's algorithm, as the spaces aren't allowed to know where they are in relation
	 * they are to each other.
	 * 
	 * @param other the space to find the movementCost required to get to
	 * @param costFunction A function that defines the 'cost' of moving from the first space to the second space
	 * @return the movementCost required to get from this space to other
	 */
	def distanceTo(other:Space[SpaceClass], costFunction:CostFunction[SpaceClass]):Int = {
		val closed = MMap.empty[Space[SpaceClass], Int]
		val open = MMap.empty[Space[SpaceClass], Int]
		var checkingTile:(Space[SpaceClass], Int) = ((this, 0))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (checkingTile._1 != other) {
			open -= checkingTile._1
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Space[SpaceClass]) => {
				val newDistance = checkingTile._2 + costFunction(checkingTile._1, s)
				val oldDistance = open.getOrElse(s, Integer.MAX_VALUE)
				
				if (newDistance < oldDistance) open += ((s, newDistance))
			}}
			
			checkingTile = open.minBy{_._2}
		}
		checkingTile._2
	}
	
	/**
	 * Returns the shortest path from `this` space to another space.
	 * 
	 * This is Dijkstra's algorithm, as the spaces aren't allowed to know where they are in relation
	 * they are to each other.
	 * 
	 * This short-circuits when it finds the desired space, and so is more efficient than pathToEverywhere
	 * which searches the whole field
	 * 
	 * @param other the space to find the movement cost required to get to
	 * @param costFunction A function that defines the 'cost' of moving from the first space to the second space
	 * @return a list of spaces such that the first space is this, the last space is other, and
	 			the movement cost between the two is minimal
	 */
	def pathTo(other:Space[SpaceClass], costFunction:CostFunction[SpaceClass]):List[Space[SpaceClass]] = {
		val closed = MMap.empty[Space[SpaceClass], (Int, Space[SpaceClass])]
		val open = MMap.empty[Space[SpaceClass], (Int, Space[SpaceClass])]
		var checkingTile:(Space[SpaceClass], (Int, Space[SpaceClass])) = ((this, ((0, null )) ))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (checkingTile._1 != other) {
			open -= checkingTile._1
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Space[SpaceClass]) => {
				val newDistance = checkingTile._2._1 + costFunction(checkingTile._1, s)
				val oldDistance = open.getOrElse(s, ((Integer.MAX_VALUE, None)) )._1
				
				if (newDistance < oldDistance) open += ((s, ((newDistance, checkingTile._1)) ))
			}}
			
			checkingTile = open.minBy{_._2._1}
		}
		open -= checkingTile._1
		closed += checkingTile
		
		var currentTile:Space[SpaceClass] = other
		var returnValue:List[Space[SpaceClass]] = other :: Nil
		while ((closed(currentTile)._2) != null) {
			currentTile = closed(currentTile)._2
			returnValue = currentTile :: returnValue
		}
		returnValue
	}
	
	/**
	 * Returns the raw Dijkstra's algorithm data
	 * 
	 * @param costFunction the function defining the cost to move from one space to another
	 * @return A map where the key is a space, and the value is the cost from here to the key, and how to get there.
	 */
	def rawDijkstraData(costFunction:CostFunction[SpaceClass]):IMap[Space[SpaceClass], (Int, Space[SpaceClass])] = {
		
		val closed = MMap.empty[Space[SpaceClass], (Int, Space[SpaceClass])]
		val open = MMap.empty[Space[SpaceClass], (Int, Space[SpaceClass])]
		var checkingTile:(Space[SpaceClass], (Int, Space[SpaceClass])) = ((this, ((0, null )) ))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (! open.isEmpty || checkingTile._1 == this) {
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Space[SpaceClass]) => {
				val newDistance = checkingTile._2._1 + costFunction(checkingTile._1, s)
				val oldDistance = open.getOrElse(s, ((Integer.MAX_VALUE, None)) )._1
				
				if (newDistance < oldDistance) open += ((s, ((newDistance, checkingTile._1)) ))
			}}
			
			checkingTile = open.minBy{_._2._1}
			open -= checkingTile._1
		}
		closed += checkingTile
		
		IMap.empty ++ closed
	}
}

object Space {
	
	/** A function that defines the 'cost' of moving from one space to the second space, under the assumption that two spaces are adjacent */
	type CostFunction[SpaceClass] = Function2[Space[_ <: SpaceClass], Space[_ <: SpaceClass], Int]
	
	/** A CostFunction with a constant (1) cost for every move. */
	val constantCostFunction:CostFunction[Any] = {(from:Space[_], to:Space[_]) => 1}
	
}
