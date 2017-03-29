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
package com.rayrobdod.boardGame
package view

import java.net.URL
import scala.collection.immutable.Seq
import com.rayrobdod.json.parser.Parser
import com.rayrobdod.json.builder.{Builder, SeqBuilder}
import com.rayrobdod.json.union.{StringOrInt, JsonValue, ParserRetVal}
import VisualizationRuleBasedTilesheetBuilder._

/**
 * @group VisualizationRuleTilesheet
 */
final class VisualizationRuleBasedTilesheetBuilder[SpaceClass, Index, Dimension, IconPart, Icon](
		  baseUrl:URL
		, classMap:SpaceClassMatcherFactory[SpaceClass]
		, compostLayers:Function1[Seq[Seq[IconPart]], Icon]
		, urlToFrameImages:Function2[URL, java.awt.Dimension, Seq[IconPart]]
		, stringToIndexConverter:Function1[String, Option[IndexConverter[Index]]]
		, coordFunVars:Map[Char, CoordinateFunction[Index, Int]]
		, dimensionBuilder:Builder[StringOrInt, JsonValue, VisualizationRuleBasedTilesheetFailure, Dimension]
) extends Builder[StringOrInt, JsonValue, VisualizationRuleBasedTilesheetFailure, VisualizationRuleBasedTilesheet[SpaceClass, Index, Dimension, IconPart, Icon]] {
	
	override type Middle = Delayed[SpaceClass, Index, Dimension]
	
	override def init:Delayed[SpaceClass, Index, Dimension] = {
		new Delayed[SpaceClass, Index, Dimension](
			  "???"
			, TilesheetData()
			, dimensionBuilder.finish(dimensionBuilder.init).fold({x => x}, {x:Nothing => x}, {x:Nothing => x}, {x => throw new IllegalArgumentException(x.toString)})
			, Seq.empty
		)
	}
	
	override def apply[Input, PF](
			folding:Middle,
			key:StringOrInt,
			input:Input,
			parser:Parser[StringOrInt, JsonValue, PF, Input]
	):ParserRetVal[Middle, Nothing, PF, VisualizationRuleBasedTilesheetFailure] = key match {
		case StringOrInt.Left("name") => {
			parser.parsePrimitive[VisualizationRuleBasedTilesheetFailure](input, ExpectedPrimitive)
				.primitive.flatMap{_.ifIsString(
					  {x => ParserRetVal.Complex(folding.copy(name = x))}
					, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"))}
				)}
		}
		case StringOrInt.Left("tileset") => {
			parser.parse(new TilesetDataBuilder(baseUrl), input)
				.complex.map{x => folding.copy(tilesheet = x)}
				.primitive.flatMap{x => ParserRetVal.BuilderFailure(ExpectedComplex)}
			
		}
		case StringOrInt.Left("dimensions") => {
			parser.parse(dimensionBuilder, input)
				.complex.map{x => folding.copy(dimension = x)}
				.primitive.flatMap{x => ParserRetVal.BuilderFailure(ExpectedComplex)}
		}
		case StringOrInt.Left("rules") => {
			parser.parse(new SeqBuilder(new VisualizationRuleBuilder(classMap, stringToIndexConverter, coordFunVars), ExpectedComplex), input)
				.complex.map{x => folding.copy(rules = x)}
				.primitive.flatMap{x => ParserRetVal.BuilderFailure(ExpectedComplex)}
		}
		case _ => {
			parser.parse(this, input); ParserRetVal.Complex(folding)
		}
	}
	
	override def finish(x:Middle):ParserRetVal[VisualizationRuleBasedTilesheet[SpaceClass, Index, Dimension, IconPart, Icon], Nothing, Nothing, VisualizationRuleBasedTilesheetFailure] = {
		val tiles:Seq[IconPart] = urlToFrameImages(x.tilesheet.url, new java.awt.Dimension(x.tilesheet.tileWidth, x.tilesheet.tileHeight))
		val vizRules = x.rules.map{_.mapIconPart(tiles)}
		
		ParserRetVal.Complex(new VisualizationRuleBasedTilesheet[SpaceClass, Index, Dimension, IconPart, Icon](
			  name = x.name
			, visualizationRules = vizRules
			, compostLayers = compostLayers
			, iconDimensions = x.dimension
		))
	}
}

/**
 * @group VisualizationRuleTilesheet
 */
object VisualizationRuleBasedTilesheetBuilder {
	
	final case class Delayed[SpaceClass, Index, Dimension](
		  name:String
		, tilesheet:TilesheetData
		, dimension:Dimension
		, rules:Seq[ParamaterizedVisualizationRule[SpaceClass, Index, Int]]
	)
	
	final case class TilesheetData(url:URL = new URL("http://localhost:80/"), tileWidth:Int = 1, tileHeight:Int = 1)
	
	final class TilesetDataBuilder(baseUrl:URL) extends Builder[StringOrInt, JsonValue, VisualizationRuleBasedTilesheetFailure, TilesheetData] {
		override type Middle = TilesheetData
		override def init = TilesheetData()
		override def apply[Input, PF](
				folding:Middle, key:StringOrInt, input:Input, parser:Parser[StringOrInt, JsonValue, PF, Input]
		):ParserRetVal[Middle, Nothing, PF, VisualizationRuleBasedTilesheetFailure] = key match {
			case StringOrInt.Left("image") => parser.parsePrimitive(input, ExpectedPrimitive).primitive.flatMap{_.ifIsString(
					{str =>
						try {
							ParserRetVal.Complex(folding.copy(url = new URL(baseUrl, str)))
						} catch {
							case ex:java.net.MalformedURLException => ParserRetVal.BuilderFailure(MalformedUrl)
						}
					},
					{x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "String"))}
			)}
			case StringOrInt.Left("tileWidth") => {
				parser.parsePrimitive[VisualizationRuleBasedTilesheetFailure](input, ExpectedPrimitive)
					.primitive.flatMap{_.ifIsInteger(
						  {x => ParserRetVal.Complex(folding.copy(tileWidth = x))}
						, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"))}
					)}
			}
			case StringOrInt.Left("tileHeight") => {
				parser.parsePrimitive[VisualizationRuleBasedTilesheetFailure](input, ExpectedPrimitive)
					.primitive.flatMap{_.ifIsInteger(
						  {x => ParserRetVal.Complex(folding.copy(tileHeight = x))}
						, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"))}
					)}
			}
			case _ => {
				parser.parse(this, input); ParserRetVal.Complex(folding)
			}
		}
		override def finish(x:Middle) = ParserRetVal.Complex(x)
	}
	
	final class RectangularDimensionBuilder extends Builder[StringOrInt, JsonValue, VisualizationRuleBasedTilesheetFailure, RectangularDimension] {
		override type Middle = RectangularDimension
		override def init = RectangularDimension(1,1)
		override def apply[Input, PF](
				folding:RectangularDimension,
				key:StringOrInt,
				input:Input,
				parser:Parser[StringOrInt, JsonValue, PF, Input]
		):ParserRetVal[Middle, Nothing, PF, VisualizationRuleBasedTilesheetFailure]  = key match {
			case StringOrInt.Left("width") => {
				parser.parsePrimitive[VisualizationRuleBasedTilesheetFailure](input, ExpectedPrimitive)
					.primitive.flatMap{_.ifIsInteger(
						  {x => ParserRetVal.Complex(folding.copy(width = x))}
						, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"))}
					)}
			}
			case StringOrInt.Left("height") => {
				parser.parsePrimitive[VisualizationRuleBasedTilesheetFailure](input, ExpectedPrimitive)
					.primitive.flatMap{_.ifIsInteger(
						  {x => ParserRetVal.Complex(folding.copy(height = x))}
						, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"))}
					)}
			}
			case _ => {
				parser.parse(RectangularDimensionBuilder.this, input); ParserRetVal.Complex(folding)
			}
		}
		override def finish(x:Middle) = ParserRetVal.Complex(x)
	}
	
	final class HorizontalHexagonalDimensionBuilder extends Builder[StringOrInt, JsonValue, VisualizationRuleBasedTilesheetFailure, HorizontalHexagonalDimension] {
		override type Middle = HorizontalHexagonalDimensionDelay
		override def init = HorizontalHexagonalDimensionDelay(1, 1, 1)
		override def apply[Input, PF](
				folding:HorizontalHexagonalDimensionDelay,
				key:StringOrInt,
				input:Input,
				parser:Parser[StringOrInt, JsonValue, PF, Input]
		):ParserRetVal[Middle, Nothing, PF, VisualizationRuleBasedTilesheetFailure] = key match {
			case StringOrInt.Left("width") => {
				parser.parsePrimitive[VisualizationRuleBasedTilesheetFailure](input, ExpectedPrimitive)
					.primitive.flatMap{_.ifIsInteger(
						  {x => ParserRetVal.Complex(folding.copy(width = x))}
						, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"))}
					)}
			}
			case StringOrInt.Left("height") => {
				parser.parsePrimitive[VisualizationRuleBasedTilesheetFailure](input, ExpectedPrimitive)
					.primitive.flatMap{_.ifIsInteger(
						  {x => ParserRetVal.Complex(folding.copy(height = x))}
						, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"))}
					)}
			}
			case StringOrInt.Left("verticalOffset") => {
				parser.parsePrimitive[VisualizationRuleBasedTilesheetFailure](input, ExpectedPrimitive)
					.primitive.flatMap{_.ifIsInteger(
						  {x => ParserRetVal.Complex(folding.copy(verticalOffset = x))}
						, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"))}
					)}
			}
			case _ => {
				parser.parse(HorizontalHexagonalDimensionBuilder.this, input); ParserRetVal.Complex(folding)
			}
		}
		override def finish(x:Middle) = ParserRetVal.Complex(x.build)
	}
	
	final class ElongatedTriangularDimensionBuilder extends Builder[StringOrInt, JsonValue, VisualizationRuleBasedTilesheetFailure, ElongatedTriangularDimension] {
		override type Middle = ElongatedTriangularDimension
		override def init = ElongatedTriangularDimension(1,1,1)
		override def apply[Input, PF](
				folding:ElongatedTriangularDimension,
				key:StringOrInt,
				input:Input,
				parser:Parser[StringOrInt, JsonValue, PF, Input]
		):ParserRetVal[Middle, Nothing, PF, VisualizationRuleBasedTilesheetFailure] = key match {
			case StringOrInt.Left("width") => {
				parser.parsePrimitive[VisualizationRuleBasedTilesheetFailure](input, ExpectedPrimitive)
					.primitive.flatMap{_.ifIsInteger(
						  {x => ParserRetVal.Complex(folding.copy(width = x))}
						, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"))}
					)}
			}
			case StringOrInt.Left("triangleHeight") => {
				parser.parsePrimitive[VisualizationRuleBasedTilesheetFailure](input, ExpectedPrimitive)
					.primitive.flatMap{_.ifIsInteger(
						  {x => ParserRetVal.Complex(folding.copy(triangleHeight = x))}
						, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"))}
					)}
			}
			case StringOrInt.Left("squareHeight") => {
				parser.parsePrimitive[VisualizationRuleBasedTilesheetFailure](input, ExpectedPrimitive)
					.primitive.flatMap{_.ifIsInteger(
						  {x => ParserRetVal.Complex(folding.copy(squareHeight = x))}
						, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"))}
					)}
			}
			case _ => {
				parser.parse(ElongatedTriangularDimensionBuilder.this, input); ParserRetVal.Complex(folding)
			}
		}
		override def finish(x:Middle) = ParserRetVal.Complex(x)
	}
	
	final case class HorizontalHexagonalDimensionDelay(width:Int, height:Int, verticalOffset:Int) {
		val hinset = height - verticalOffset
		
		def build:HorizontalHexagonalDimension = HorizontalHexagonalDimension(width, height, hinset)
	}
}
