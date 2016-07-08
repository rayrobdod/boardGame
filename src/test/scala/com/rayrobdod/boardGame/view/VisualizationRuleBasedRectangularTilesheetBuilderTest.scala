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
package com.rayrobdod.boardGame.view

import org.scalatest.{FunSuite, FunSpec}
import scala.collection.immutable.{Seq, Map}
import java.awt.Image
import java.awt.image.BufferedImage
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import com.rayrobdod.json.parser.JsonParser;
import com.rayrobdod.json.union.StringOrInt
import com.rayrobdod.json.union.ParserRetVal.Complex
import com.rayrobdod.boardGame.SpaceClassMatcher
import com.rayrobdod.boardGame.swingView

class VisualizationRuleBasedRectangularTilesheetBuilderTest extends FunSpec {
	
	describe("VisualizationRuleBasedRectangularTilesheetBuilder + JsonParser") {
		it ("do a thing") {
			val compostLayersFun = {x:Seq[Seq[Float]] => "apple"}
			val urlToFrameImagesFun = {(u:URL, d:java.awt.Dimension) => Seq(0.5f)}
			
			val expected = Complex(new VisualizationRuleBasedRectangularTilesheetBuilder.Delayed(
				classMap = StubSpaceClassMatcherFactory,
				compostLayers = compostLayersFun,
				urlToFrameImages = urlToFrameImagesFun,
				sheetUrl = new URL("http://localhost/tiles"),
				tileWidth = 32,
				tileHeight = 48,
				rules = new URL("http://localhost/rules"),
				name = "name"
			))
			val src = """{
				"tiles":"tiles",
				"tileWidth":32,
				"tileHeight":48,
				"rules":"rules",
				"name":"name"
			}"""
			val builder = new VisualizationRuleBasedRectangularTilesheetBuilder(new URL("http://localhost/"), StubSpaceClassMatcherFactory, compostLayersFun, urlToFrameImagesFun).mapKey[StringOrInt](StringOrInt.unwrapToString)
			val result = new JsonParser().parse(builder, src)
			
			assertResult(expected){result}
		}
	}
	describe("VisualizationRuleBasedRectangularTilesheetBuilder.Delayed") {
		it ("can apply() using a two-image, two-rule pair of files") {
			val source = new VisualizationRuleBasedRectangularTilesheetBuilder.Delayed(
				classMap = StubSpaceClassMatcherFactory,
				compostLayers = Swing.compostLayers,
				urlToFrameImages = Swing.sheeturl2images,
				sheetUrl = this.getClass.getResource("/com/rayrobdod/boardGame/swingView/whiteBlackTiles.png"),
				tileWidth = 32,
				tileHeight = 32,
				rules = new URL("data", "text/json", -1, """[{"tiles":0, "indexies":"x == 0"},{"tiles":1}]""", new DataHandler),
				name = "name"
			)
			val result = source.apply()
			
			assertResult("name"){result.name}
			val resRules = result.visualizationRules.map{_.asInstanceOf[ParamaterizedRectangularVisualizationRule[String, _]]}
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

class DataHandler extends java.net.URLStreamHandler {
	override def openConnection(u:URL):java.net.URLConnection = {
		new DataConnection(u)
	}
}

class DataConnection(u:URL) extends java.net.URLConnection(u) {
	override def connect():Unit = {}
	override def getInputStream():java.io.InputStream = {
		new java.io.ByteArrayInputStream(u.getFile.getBytes(UTF_8))
	}
}
