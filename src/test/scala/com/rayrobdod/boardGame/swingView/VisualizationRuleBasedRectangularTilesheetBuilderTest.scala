/*
	Deduction Tactics
	Copyright (C) 2012-2015  Raymond Dodge

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
package com.rayrobdod.boardGame.swingView

import org.scalatest.{FunSuite, FunSpec}
import scala.collection.immutable.{Seq, Map}
import java.net.URL
import com.rayrobdod.json.parser.JsonParser;
import com.rayrobdod.boardGame.SpaceClassMatcher

class VisualizationRuleBasedRectangularTilesheetBuilderTest extends FunSpec {
	
	describe("VisualizationRuleBasedRectangularTilesheetBuilder + JsonParser") {
		it ("do a thing") {
			val expected = new VisualizationRuleBasedRectangularTilesheetBuilder.Delayed(
				classMap = MySpaceClassMatcherFactory,
				sheetUrl = new URL("http://localhost/tiles"),
				tileWidth = 32,
				tileHeight = 48,
				rules = new URL("http://localhost/rules"),
				name = "name"
			)
			val src = """{
				"tiles":"tiles",
				"tileWidth":32,
				"tileHeight":48,
				"rules":"rules",
				"name":"name"
			}"""
			val result = new JsonParser(new VisualizationRuleBasedRectangularTilesheetBuilder(new URL("http://localhost/"), MySpaceClassMatcherFactory)).parse(src)
			
			assertResult(expected){result}
		}
	}
	
	
	object MySpaceClassMatcherFactory extends SpaceClassMatcherFactory[String] {
		def apply(ref:String):SpaceClassMatcher[String] = {
			throw new UnsupportedOperationException("")
		}
	}
}
