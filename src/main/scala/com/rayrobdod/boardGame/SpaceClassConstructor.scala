package com.rayrobdod.boardGame

/**
 * A constructor-deconstructor for Space Classes.
 * 
 * The object constructed should be matched by the deconstructor, but there is no need for objects constructed
 * by this to be the only types matched by the deconstructor.
 * 
 * @author Raymond Dodge
 * @version 29 Sept 2011
 * @version 15 Dec 2011 - moved from {@code net.verizon.rayrobdod.boardGame} to {@code com.rayrobdod.boardGame}
 * @version 2013 Mar 04 - implementing hashCode
 * @see [[com.rayrobdod.boardGame.SpaceClass]] - the thing this constructs and deconstructs 
 */
trait SpaceClassConstructor
{
	/** determines if a space is of this type */
	def unapply(a:SpaceClass):Boolean
	/** creates a space of this type */
	def apply():SpaceClass
	
	/**
	 * This seems like a bad idea, but I can't quite place why...
	 * @return this.apply.hashCode, if it is stable
	 */ 
	override def hashCode = { 
		if (this.apply.hashCode == this.apply.hashCode)
			this.apply.hashCode
		else
			super.hashCode
	}
		
}
