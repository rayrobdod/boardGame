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

import scala.collection.mutable.Buffer
import com.rayrobdod.boardGame.{Space => BoardGameSpace}

/**
 * An object that can move around a game board.
 * 
 * @author Raymond Dodge
 * @version 2.0.0
 */
abstract class Token
{
	import Token._
	
	private var _currentSpace:Space = null
	/** returns the Token's current space. */
	def currentSpace:Space = _currentSpace
	protected def currentSpace_=(movedTo:Space, landed:Boolean):Unit = {
		movedTo.typeOfSpace.passOverAction(Token.this)
		if (landed) {movedTo.typeOfSpace.landOnAction(Token.this)}
		_currentSpace = movedTo
		
		moveReactions.foreach{a => a(movedTo, landed)}
	}
	
	private val moveReactions:Buffer[MoveReactionType] = Buffer.empty
	def moveReactions_+=(f:MoveReactionType) = moveReactions += f 
	def moveReactions_-=(f:MoveReactionType) = moveReactions -= f 
	
	private val selectedReactions:Buffer[SelectedReactionType] = Buffer.empty
	def selectedReactions_+=(f:SelectedReactionType) = selectedReactions += f 
	def selectedReactions_-=(f:SelectedReactionType) = selectedReactions -= f 
	
	def beSelected(b:Boolean):Unit = selectedReactions.foreach{a => a(b)}
}

object Token {
	type MoveReactionType = (Space, Boolean) => Unit
	type SelectedReactionType = (Boolean) => Unit
}
