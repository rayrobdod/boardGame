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

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks
import com.rayrobdod.boardGame.ConstTrueSpaceClassMatcher

class StringSpaceClassMatcherFactoryTest extends FunSpec {
	import StringSpaceClassMatcherFactory.EqualsMatcher
	import StringSpaceClassMatcherFactory.UnequalsMatcher
	
	
	describe ("StringSpaceClassMatcherFactory") {
		it ("returns ConstTrueSpaceClassMatcher on input '*'"){
			assertResult(ConstTrueSpaceClassMatcher){
				StringSpaceClassMatcherFactory("*")
			}
		}
		it ("returns EqualsMatcher('a') on input 'a'"){
			assertResult(EqualsMatcher("a")){
				StringSpaceClassMatcherFactory("a")
			}
		}
		it ("returns UnequalsMatcher('a') on input '!a'"){
			assertResult(UnequalsMatcher("a")){
				StringSpaceClassMatcherFactory("!a")
			}
		}
	}
	describe ("StringSpaceClassMatcherFactory.EqualsMatcher") {
		it ("EqualsMatcher(\"a\") matches \"a\""){
			assert(EqualsMatcher("a").unapply("a"))
		}
		it ("EqualsMatcher(\"a\") doesn't match \"c\""){
			assert(! EqualsMatcher("a").unapply("c"))
		}
	}
	describe ("StringSpaceClassMatcherFactory.UnequalsMatcher") {
		it ("UnequalsMatcher(\"a\") doesn't match \"a\""){
			assert(! UnequalsMatcher("a").unapply("a"))
		}
		it ("UnequalsMatcher(\"a\") matches \"c\""){
			assert(UnequalsMatcher("a").unapply("c"))
		}
	}
}
