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

import org.scalatest.FunSpec
import java.awt.image.BufferedImage
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import scala.collection.immutable.Seq
import com.rayrobdod.json.parser.JsonParser;
import com.rayrobdod.boardGame.SpaceClassMatcher
import com.rayrobdod.boardGame.RectangularIndex
import com.rayrobdod.boardGame.RectangularField

class VisualizationRuleBasedTilesheetBuilderTest extends FunSpec {
	val rdb = new VisualizationRuleBasedTilesheetBuilder.RectangularDimensionBuilder()
	
	describe("VisualizationRuleBasedTilesheetBuilder + JsonParser") {
		it ("do a thing") {
			val builder = new VisualizationRuleBasedTilesheetBuilder[Int, RectangularIndex, RectangularDimension, (String, Int, Int, Int), Seq[Seq[(String, Int, Int, Int)]]](
				  baseUrl = new URL("http://localhost/")
				, classMap = MySpaceClassMatcherFactory
				, compostLayers = {x => x}
				, urlToFrameImages = {(u:URL, d:java.awt.Dimension) => (0 to 1).map{x => ((u.toString, d.width, d.height, x))}}
				, stringToIndexConverter = VisualizationRuleBuilder.stringToRectangularIndexTranslation _
				, coordFunVars = CoordinateFunctionSpecifierParser.rectangularVars
				, dimensionBuilder = rdb
			)
			
			
			val src = """{
				"name" : "name",
				"tileset" : {
					"image" : "tiles",
					"tileWidth" : 32,
					"tileHeight" : 48
				},
				"dimensions" : {
					"width" : 64,
					"height" : 24
				},
				"rules": [{
					"tiles": 0
				}]
			}"""
			val result = new JsonParser().parse(builder, src).fold({x => x}, {x => fail()}, {x => fail()}, {x => fail()})
			
			val expectedRules = Vector(ParamaterizedVisualizationRule(Map(-127 -> List(("http://localhost/tiles", 32, 48, 0)))))
			val expectedIcon00 = ((Vector(List(("http://localhost/tiles", 32, 48, 0))),Vector()))
			assertResult("name"){result.name}
			assertResult(RectangularDimension(64, 24)){result.iconDimensions}
			// assertResult(expectedRules){result.visualizationRules}
			assertResult(expectedIcon00){result.getIconFor(RectangularField(Map((0, 0) -> 0)), (0,0), scala.util.Random)}
		}
	}
	describe("VisualizationRuleBasedTilesheetBuilder.Delayed") {
		it ("can apply() using a two-image, two-rule pair of files") {
			val idxEquationParser = new CoordinateFunctionSpecifierParser(CoordinateFunctionSpecifierParser.rectangularVars)
			
			val source = new VisualizationRuleBasedTilesheetBuilder.Delayed[Int, RectangularIndex, RectangularDimension](
				  name = "name"
				, tilesheet = VisualizationRuleBasedTilesheetBuilder.TilesheetData(
					  url = this.getClass.getResource("/com/rayrobdod/boardGame/swingView/whiteBlackTiles.png")
					, tileWidth = 32
					, tileHeight = 32
				)
				, dimension = RectangularDimension(32, 32)
				, rules = Seq(
					  ParamaterizedVisualizationRule(
						  iconParts = Map(-1 -> Seq(0))
						, indexEquation = idxEquationParser.parse("x == 0").right.get
					)
					, ParamaterizedVisualizationRule(
						  iconParts = Map(-1 -> Seq(1))
					)
				)
			)
			val result = Swing.VisualizationRuleBasedRectangularTilesheetBuilder(new URL("http://localhost:80"), MySpaceClassMatcherFactory)
					.finish(source)
			
			val resRules = result.fold({x => x}, {x => fail()}, {x => fail()}, {x => fail()})
					.visualizationRules.map{_.asInstanceOf[ParamaterizedVisualizationRule[Int, RectangularIndex, RectangularDimension]]}
			assertResult("(x == 0)"){resRules(0).indexEquation.toString}
			assertResult("true"){resRules(1).indexEquation.toString}
			assertResult(java.awt.Color.white.getRGB){resRules(0).iconParts(-1)(0).asInstanceOf[BufferedImage].getRGB(5,5)}
			assertResult(java.awt.Color.black.getRGB){resRules(1).iconParts(-1)(0).asInstanceOf[BufferedImage].getRGB(5,5)}
		}
	}
	
	
	
	object MySpaceClassMatcherFactory extends SpaceClassMatcherFactory[Int] {
		def apply(ref:String):SpaceClassMatcher[Int] = {
			new MySpaceClassMatcher(ref)
		}
	}
	case class MySpaceClassMatcher(ref:String) extends SpaceClassMatcher[Int] {
		def unapply(sc:Int):Boolean = {ref == sc.toString}
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
