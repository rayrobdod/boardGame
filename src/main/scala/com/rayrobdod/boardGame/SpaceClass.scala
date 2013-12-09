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
 * @see [[com.rayrobdod.boardGame.NoPassOverAction]], [[com.rayrobdod.boardGame.UniformMovementCost]]
 		[[com.rayrobdod.boardGame.UniformMovementCost]] - mixins that have the simplest implementation possible for
 		one of these classes each
 * @see [[com.rayrobdod.boardGame.SpaceClassConstructor]] - factories and deconstructors for these
 * @see [[com.rayrobdod.boardGame.Space]] - This defines the way Spaces interact with Tokens
 */
abstract class SpaceClass
{
	/**
	 * An action that occurs when a piece moves over this tile
	 * This function is called for its side effects
	 */
	def passOverAction:Function1[Token, Any]
	
	/**
	 * An action that occurs when a piece lands on this tile
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
 */
trait NoLandOnAction extends SpaceClass
{
	/** returns a function that does nothing */
	override def landOnAction = Function.const(None)
}
