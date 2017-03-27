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
package com.rayrobdod.jsonTilesheetViewer

import com.rayrobdod.boardGame.SpaceClassMatcher
import com.rayrobdod.boardGame.ConstTrueSpaceClassMatcher

/**
 * A SpaceClassMatcherFactory for strings where
 * 
  - "*" matches everything
  - "!whatever" matches everything except "whatever"
  - "whatever" matches only "whatever"
 * 
 */
object StringSpaceClassMatcherFactory extends 
		com.rayrobdod.boardGame.view.SpaceClassMatcherFactory[SpaceClass] {
	
	override def apply(reference:String):SpaceClassMatcher[SpaceClass] = {
		if (reference == "*") {ConstTrueSpaceClassMatcher}
		else if (reference.head == '!') {new UnequalsMatcher(reference.tail)}
		else {new EqualsMatcher(reference)}
	}
	
	/** A SpaceClassMatcher which returns true if the reference equals the space class */
	case class EqualsMatcher(val reference:String) extends SpaceClassMatcher[SpaceClass] {
		def unapply(sc:SpaceClass):Boolean = (sc == reference)
	}
	/** A SpaceClassMatcher which returns true if the reference does not equal the space class */
	case class UnequalsMatcher(val reference:String) extends SpaceClassMatcher[SpaceClass] {
		def unapply(sc:SpaceClass):Boolean = (sc != reference)
	}
}

