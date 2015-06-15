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
import java.awt.Image
import java.awt.image.BufferedImage
import java.net.URL
import com.rayrobdod.json.parser.JsonParser;
import com.rayrobdod.boardGame.SpaceClassMatcher

class VisualizationRuleBasedRectangularTilesheetBuilderTest extends FunSpec {
	
	describe("VisualizationRuleBasedRectangularTilesheetBuilder + JsonParser") {
		it ("do a thing") {
			val expected = new VisualizationRuleBasedRectangularTilesheetBuilder.Delayed(
				classMap = StubSpaceClassMatcherFactory,
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
			val result = new JsonParser(new VisualizationRuleBasedRectangularTilesheetBuilder(new URL("http://localhost/"), StubSpaceClassMatcherFactory)).parse(src)
			
			assertResult(expected){result}
		}
	}
	describe("VisualizationRuleBasedRectangularTilesheetBuilder.Delayed") {
		it ("do a thing") {
			val source = new VisualizationRuleBasedRectangularTilesheetBuilder.Delayed(
				classMap = StubSpaceClassMatcherFactory,
				sheetUrl = new URL("data", "image/png;base64", -1, """iVBORw0KGgoAAAANSUhEUgAAAEAAAAAgAQAAAACK5kMpAAAAHklEQVR4Xu3GsREAAAyCQC7772y0dQYpuEcOd1ljeJXGBDtwuYtjAAAAAElFTkSuQmCC""", new DataHandler),
				tileWidth = 32,
				tileHeight = 32,
				rules = new URL("data", "text/json", -1, """[{"tiles":0, "indexies":"x == 0"},{"tiles":1}]""", new DataHandler),
				name = "name"
			)
			val result = source.apply()
			
			assertResult("name"){result.name}
			val resRules = result.visualizationRules.map{_.asInstanceOf[ParamaterizedRectangularVisualizationRule[String]]}
			assertResult("x == 0"){resRules(0).indexEquation}
			assertResult("true"){resRules(1).indexEquation}
			assertResult(java.awt.Color.white.getRGB){resRules(0).iconParts(-127)(0).asInstanceOf[BufferedImage].getRGB(5,5)}
			assertResult(java.awt.Color.black.getRGB){resRules(1).iconParts(-127)(0).asInstanceOf[BufferedImage].getRGB(5,5)}
		}
	}
	
	
	object StubSpaceClassMatcherFactory extends SpaceClassMatcherFactory[String] {
		def apply(ref:String):SpaceClassMatcher[String] = {
			throw new UnsupportedOperationException("")
		}
	}
}
