package com.rayrobdod.boardGame

import scala.swing.Reactions
import scala.swing.Reactions.Reaction
import scala.swing.event.Event

/**
 * A reaction that temporarilly removes the specified reaction 
 * <small>A turn reaction would work best - one that takes a
 * StartOfTurn and includes a EndOfTurn invocation</small> for
 * a certain umber of turns.
 * 
 * This is a Turn Reaction. 
 * 
 * @author Raymond Dodge
 * @version 2013 Jun 11
 */
class SkipTurnReaction (
			val countTo:Int,
			val token:Token,
			val takingOverFor:Reaction
) extends Reaction {
	token.reactions -= takingOverFor
	
	private var currentCount:Int = 0;
	def isDone = currentCount >= countTo;
	
	def apply(e:Event) = e match {
		case StartOfTurn => {
			currentCount = currentCount + 1;
			
			if (isDone) {
				token.reactions -= this
				token.reactions += takingOverFor
			}
			
			token ! EndOfTurn
		}
		case _ => {}
	}
	
	def isDefinedAt(e:Event) = e match {
		case StartOfTurn => isDone
		case _ => false
	}
}
