package com.rayrobdod.boardGame

import scala.collection.mutable.Buffer
import com.rayrobdod.boardGame.{Space => BoardGameSpace}

/**
 * An object that can move around a game board.
 * 
 * @author Raymond Dodge
 * @version 06 May 2011
 * @version 15 May 2011 - gave a reactions value instead of forcing extending functions
 * @version 03 Oct 2011 - removed the constructor and type, and allowed currentSpace to be null.
 * @version 15 Dec 2011 - moved from {@code net.verizon.rayrobdod.boardGame} to {@code com.rayrobdod.boardGame}
 * @version 2013 Aug 07 - no longer implements actors, or uses reactions or events
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
