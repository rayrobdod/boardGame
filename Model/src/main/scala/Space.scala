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

import scala.collection.immutable.{Seq, List, Map => IMap}
import scala.collection.mutable.{Map => MMap}

/**
 * A spot on a board game board
 * @group Generic
 * @tparam SpaceClass the type of domain object representing the properties of this space 
 * @tparam Repr the type of space representing every other space reachable from this space
 */
trait SpaceLike[SpaceClass, Repr <: SpaceLike[SpaceClass, Repr]] {
	/**
	 * the domain object  representing the properties of this space 
	 */
	def typeOfSpace:SpaceClass
	
	/**
	 * A space that is treated as adjacent to this one; such as a tile that can be directly
	 * accessed from this tile without passing through other tiles
	 */
	def adjacentSpaces:Seq[Repr]
	
	
	
	/************** the implemented stuff ****************/
	
	
	/**
	 * Returns a set of all spaces that are `avaliableCost` or less movement away according to the `costFunction`
	 * 
	 * @param availableCost the amount of "movement" available to spend
	 * @param costFunction A function that defines the 'cost' of moving from the first space to the second space
	 * @return a set of all spaces that are `avaliableCost` or less spaces away according to the `costFunction`
	 */
	def spacesWithin(availableCost:Int, costFunction:CostFunction[Repr])(implicit ev:this.type <:< Repr):Seq[Repr] = {
		if (availableCost < 0) {Seq.empty}
		else if (availableCost == 0) {Seq(this)}
		else {
			val a:Seq[Repr] = Seq(this)
			val b:Seq[Repr] = adjacentSpaces.flatMap{(space:Repr) => 
				space.spacesWithin(
					availableCost - costFunction(this, space),
					costFunction
				)
			}
			(a ++ b).distinct
		}
	}
	
	/**
	 * Returns a set of all spaces that are exactly `avaliableCost` movement away according to the `costFunction`
	 * 
	 * @param availableCost the amount of "movement" available to spend
	 * @param costFunction A function that defines the 'cost' of moving from the first space to the second space
	 * @return Returns a set of all spaces that are exactly `avaliableCost` "movement" away according to the `costFunction`
	 */
	def spacesAfter(availableCost:Int, costFunction:CostFunction[Repr])(implicit ev:this.type <:< Repr):Seq[Repr] = {
		if (availableCost < 0) {Seq.empty}
		else if (availableCost == 0) {Seq(this)}
		else {
			adjacentSpaces.flatMap((space:Repr) => {
				space.spacesAfter(
					availableCost - costFunction(this, space),
					costFunction
				)
			}).to[Seq].distinct
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
	def distanceTo(other:Repr, costFunction:CostFunction[Repr])(implicit ev:this.type <:< Repr):Int = {
		val closed = MMap.empty[Repr, Int]
		val open = MMap.empty[Repr, Int]
		var checkingTile:(Repr, Int) = ((this, 0))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (checkingTile._1 != other) {
			open -= checkingTile._1
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Repr) => {
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
	def pathTo(other:Repr, costFunction:CostFunction[Repr])(implicit ev:this.type <:< Repr):List[Repr] = {
		val closed = MMap.empty[Repr, (Int, Repr)]
		val open = MMap.empty[Repr, (Int, Repr)]
		var checkingTile:(Repr, (Int, Repr)) = ((this, ((0, ev(null))) ))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (checkingTile._1 != other) {
			open -= checkingTile._1
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Repr) => {
				val newDistance = checkingTile._2._1 + costFunction(checkingTile._1, s)
				val oldDistance = open.getOrElse(s, ((Integer.MAX_VALUE, None)) )._1
				
				if (newDistance < oldDistance) open += ((s, ((newDistance, checkingTile._1)) ))
			}}
			
			checkingTile = open.minBy{_._2._1}
		}
		open -= checkingTile._1
		closed += checkingTile
		
		var currentTile:Repr = other
		var returnValue:List[Repr] = other :: Nil
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
	def rawDijkstraData(costFunction:CostFunction[Repr])(implicit ev:this.type <:< Repr):IMap[Repr, (Int, Repr)] = {
		
		val closed = MMap.empty[Repr, (Int, Repr)]
		val open = MMap.empty[Repr, (Int, Repr)]
		var checkingTile:(Repr, (Int, Repr)) = ((this, ((0, ev(null))) ))
		
		// (Space, Int) is Space and a distance to the tile from this
		
		while (! open.isEmpty || checkingTile._1 == this) {
			closed += checkingTile
			
			val newTilesToCheck = checkingTile._1.adjacentSpaces
			val unclosedNewTilesToCheck = newTilesToCheck.filter{! closed.contains(_)}
			
			unclosedNewTilesToCheck.foreach{(s:Repr) => {
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
