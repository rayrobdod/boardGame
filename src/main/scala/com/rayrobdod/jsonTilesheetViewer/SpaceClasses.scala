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
package com.rayrobdod.jsonTilesheetViewer

import com.rayrobdod.boardGame.swingView.SpaceClassMatcherFactory
import com.rayrobdod.boardGame.SpaceClassMatcher
import com.rayrobdod.boardGame.ConstTrueSpaceClassMatcher

/**
 * # '*' matches everything
 * # "!whatever" matches everything except "whatever"
 * # "whatever" matches only "whatever"
 */
class StringSpaceClassMatcher extends SpaceClassMatcherFactory[SpaceClass] {
	
	def apply(reference:String):SpaceClassMatcher[SpaceClass] = {
		if (reference == "*") {ConstTrueSpaceClassMatcher}
		else if (reference.head == '!') {new unequalsMatcher(reference.tail)}
		else {new equalsMatcher(reference)}
	}
	
	private class equalsMatcher(reference:String) extends SpaceClassMatcher[SpaceClass] {
		def unapply(sc:SpaceClass):Boolean = (sc == reference)
	}
	private class unequalsMatcher(reference:String) extends SpaceClassMatcher[SpaceClass] {
		def unapply(sc:SpaceClass):Boolean = (sc != reference)
	}
}

