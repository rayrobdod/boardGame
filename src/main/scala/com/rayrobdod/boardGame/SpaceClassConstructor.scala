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
