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

/** 
 * A type of space on a game board.
 *
 * @author Raymond Dodge
 * @version 29 Sept 2011
 * @version 15 Dec 2011 - moved from {@code net.verizon.rayrobdod.boardGame} to {@code com.rayrobdod.boardGame}
 * @version 05 Apr 2012 - Adding #cost(Token, TypeOfCost). Removing #movementCost(Token)
 * @see [[com.rayrobdod.boardGame.NoPassOverAction]], [[com.rayrobdod.boardGame.UniformMovementCost]]
 		[[com.rayrobdod.boardGame.UniformMovementCost]] - mixins that have the simplest impelemtnaiton possible for
 		one of these classes each
 * @see [[com.rayrobdod.boardGame.SpaceClassConstructor]] - factories and deconstructors for these
 * @see [[com.rayrobdod.boardGame.Space]] - This defines the way Spaces interact with Tokens
 */
abstract class SpaceClass
{
	/**
	 * An action that occurs when a piece moves over this tile
	 * This function is called for its side effects
	 * 
	 * In general, this should use the fact that the Token is an Actor, and pass messages rather than call methods.
	 */
	def passOverAction:Function1[Token, Any]
	
	/**
	 * An action that occurs when a peice lands on this tile
	 * 
	 * In general, this should use the fact that the Token is an Actor, and pass messages rather than call methods.
	 */
	def landOnAction:Function1[Token, Any]
	
	/** 
	 * Some type of cost associated with this space.
	 */
	def cost(token:Token, costType:TypeOfCost):Int
}

/**
 * A trait that indicates that all spaces of a type all have the same effort to pass through.
 * 
 * @author Raymond Dodge
 * @version 06 May 2011
 * @version 30 July 2011 - #movementCost() => #movementCost(Token)
 * @version 29 Sept 2011 - now overrides SpaceClass instead of Space
 * @version 15 Dec 2011 - moved from {@code net.verizon.rayrobdod.boardGame} to {@code com.rayrobdod.boardGame}
 */
trait UniformMovementCost extends SpaceClass
{
	/** an arbitrary value; 1 */
	private val arbitraryValue = 1
	
	/**
	 * returns a constant arbitrary value since all tiles have the same movement cost.
	 * @return 1. So that movement cost equals spaces moved.
	 */
	override def cost(token:Token, costType:TypeOfCost) = arbitraryValue
}

/**
* A trait that indicates that a Space has no {@link #passOverAction}
 *  
 * @author Raymond Dodge
 * @version 06 May 2011
 * @version 29 Sept 2011 - now overrides SpaceClass instead of Space
 * @version 15 Dec 2011 - moved from {@code net.verizon.rayrobdod.boardGame} to {@code com.rayrobdod.boardGame}
 */
trait NoPassOverAction extends SpaceClass
{
	/** returns a function that does nothing */
	override def passOverAction = Function.const(None)
}

/**
* A trait that indicates that a Space has no {@link #landOnAction}
 *  
 * @author Raymond Dodge
 * @version 06 May 2011
 * @version 29 Sept 2011 - now overrides SpaceClass instead of Space
 * @version 15 Dec 2011 - moved from {@code net.verizon.rayrobdod.boardGame} to {@code com.rayrobdod.boardGame}
 */
trait NoLandOnAction extends SpaceClass
{
	/** returns a function that does nothing */
	override def landOnAction = Function.const(None)
}
