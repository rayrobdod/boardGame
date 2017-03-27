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

import java.io.Reader
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import scala.collection.immutable.Seq
import scala.util.{Either, Left, Right}
import com.rayrobdod.json.parser.{Parser, JsonParser}
import com.rayrobdod.json.builder.{Builder, SeqBuilder}
import com.rayrobdod.json.union.{StringOrInt, JsonValue}
import VisualizationRuleBasedTilesheetBuilder.Delayed

/**
 * @group VisualizationRuleTilesheet
 */
final class VisualizationRuleBasedTilesheetBuilder[SpaceClass, Index, Dimension, DimensionDelay, IconPart, Icon](
		  baseUrl:URL
		, classMap:SpaceClassMatcherFactory[SpaceClass]
		, compostLayers:Function1[Seq[Seq[IconPart]], Icon]
		, urlToFrameImages:Function2[URL, java.awt.Dimension, Seq[IconPart]]
		, stringToIndexConverter:Function1[String, Either[(String, Int), IndexConverter[Index]]]
		, coordFunVars:Map[Char, CoordinateFunction[Index, Int]]
		, dimensionBuilder:VisualizationRuleBasedTilesheetBuilder.FinalizableBuilder[String, JsonValue, DimensionDelay, Dimension]
) extends Builder[String, JsonValue, Delayed[SpaceClass, Index, Dimension, DimensionDelay, IconPart, Icon]] {
	override def init:Delayed[SpaceClass, Index, Dimension, DimensionDelay, IconPart, Icon] = {
		new Delayed[SpaceClass, Index, Dimension, DimensionDelay, IconPart, Icon](
			  classMap
			, compostLayers
			, urlToFrameImages
			, stringToIndexConverter
			, coordFunVars
			, dimensionBuilder.init
		)
	}
	override def apply[Input](
			a:Delayed[SpaceClass, Index, Dimension, DimensionDelay, IconPart, Icon],
			key:String,
			input:Input,
			parser:Parser[String, JsonValue, Input]
	):Either[(String, Int), Delayed[SpaceClass, Index, Dimension, DimensionDelay, IconPart, Icon]] = {
		parser.parsePrimitive(input).right.flatMap{value =>
			dimensionBuilder.apply(
				a.dimension, key, value, new com.rayrobdod.json.parser.IdentityParser[JsonValue]
			).right.map{x =>
				a.copy[SpaceClass, Index, Dimension, DimensionDelay, IconPart, Icon](dimension = x)
			}.right.flatMap{b:Delayed[SpaceClass, Index, Dimension, DimensionDelay, IconPart, Icon] =>
				key match {
					case "tiles" =>
						value.stringToEither{x => Right(b.copy(sheetUrl = new URL(baseUrl, x)))}
					case "tileWidth" =>
						value.integerToEither{x => Right(b.copy(tileWidth = x))}
					case "tileHeight" =>
						value.integerToEither{x => Right(b.copy(tileHeight = x))}
					case "rules" =>
						value.stringToEither{x => Right(b.copy(rules = new URL(baseUrl, x)))}
					case "name" =>
						value.stringToEither{x => Right(b.copy(name = x))}
					case _ =>
						Right(b)
				}
			}
		}
	}
}

/**
 * @group VisualizationRuleTilesheet
 */
object VisualizationRuleBasedTilesheetBuilder {
	
	final case class Delayed[SpaceClass, Index, Dimension, DimensionDelay, IconPart, Icon] (
		  classMap:SpaceClassMatcherFactory[SpaceClass]
		, compostLayers:Function1[Seq[Seq[IconPart]], Icon]
		, urlToFrameImages:Function2[URL, java.awt.Dimension, Seq[IconPart]]
		, stringToIndexConverter:Function1[String, Either[(String, Int), IndexConverter[Index]]]
		, coordFunVars:Map[Char, CoordinateFunction[Index, Int]]
		, dimension:DimensionDelay
		, sheetUrl:URL = new URL("http://localhost:80/")
		, tileWidth:Int = 1
		, tileHeight:Int = 1
		, rules:URL = new URL("http://localhost:80/")
		, name:String = "???"
	) {
		def apply(dimensionFinalize:Function1[DimensionDelay, Dimension]):VisualizationRuleBasedTilesheet[SpaceClass, Index, Dimension, IconPart, Icon] = {
			new VisualizationRuleBasedTilesheet[SpaceClass, Index, Dimension, IconPart, Icon](
				name, visualizationRules, compostLayers, dimensionFinalize(dimension)
			)
		}
		
		private def visualizationRules:Seq[ParamaterizedVisualizationRule[SpaceClass, Index, IconPart]] = {
			val b = new VisualizationRuleBuilder[SpaceClass, Index, IconPart](
				  urlToFrameImages(sheetUrl, new java.awt.Dimension(tileWidth, tileHeight))
				, classMap
				, stringToIndexConverter
				, coordFunVars
			).mapKey{StringOrInt.unwrapToString}
			var r:Reader = new java.io.StringReader("{}")
			try {
				r = new java.io.InputStreamReader(rules.openStream(), UTF_8)
				new JsonParser().parse(new SeqBuilder(b), r).fold(
					{x => x},
					{x => throw new java.text.ParseException("Parsed to primitive value", 0)},
					{(s,i) => throw new java.text.ParseException(s"$s ($rules:$i)", i)}
				)
			} finally {
				r.close()
			}
		}
	} 
	
	trait FinalizableBuilder[Key, Value, Folding, Result] extends Builder[Key, Value, Folding] {
		def finalize(x:Folding):Result
	}
	
	final class RectangularDimensionBuilder extends FinalizableBuilder[String, JsonValue, RectangularDimension, RectangularDimension] {
		override def init = RectangularDimension(1,1)
		override def apply[Input](
				folding:RectangularDimension,
				key:String,
				input:Input,
				parser:Parser[String, JsonValue, Input]
		):Either[(String, Int), RectangularDimension] = key match {
			case "tileWidth" => parser.parsePrimitive(input).right.flatMap{_.integerToEither{x => Right(folding.copy(width = x))}}
			case "tileHeight" => parser.parsePrimitive(input).right.flatMap{_.integerToEither{x => Right(folding.copy(height = x))}}
			case _ => {parser.parse(RectangularDimensionBuilder.this, input); Right(folding)}
		}
		override def finalize(x:RectangularDimension):RectangularDimension = x
	}
	
	final class HorizontalHexagonalDimensionBuilder extends FinalizableBuilder[String, JsonValue, HorizontalHexagonalDimensionDelay, HorizontalHexagonalDimension] {
		override def init = HorizontalHexagonalDimensionDelay(1, 1, 1)
		override def apply[Input](
				folding:HorizontalHexagonalDimensionDelay,
				key:String,
				input:Input,
				parser:Parser[String, JsonValue, Input]
		):Either[(String, Int), HorizontalHexagonalDimensionDelay] = key match {
			case "tileWidth" => parser.parsePrimitive(input).right.flatMap{_.integerToEither{x => Right(folding.copy(width = x))}}
			case "tileHeight" => parser.parsePrimitive(input).right.flatMap{_.integerToEither{x => Right(folding.copy(height = x))}}
			case "tileVerticalOffset" => parser.parsePrimitive(input).right.flatMap{_.integerToEither{x => Right(folding.copy(verticalOffset = x))}}
			case _ => {parser.parse(HorizontalHexagonalDimensionBuilder.this, input); Right(folding)}
		}
		override def finalize(x:HorizontalHexagonalDimensionDelay):HorizontalHexagonalDimension = x.build
	}
	
	final class ElongatedTriangularDimensionBuilder extends FinalizableBuilder[String, JsonValue, ElongatedTriangularDimension, ElongatedTriangularDimension] {
		override def init = ElongatedTriangularDimension(1,1,1)
		override def apply[Input](
				folding:ElongatedTriangularDimension,
				key:String,
				input:Input,
				parser:Parser[String, JsonValue, Input]
		):Either[(String, Int), ElongatedTriangularDimension] = key match {
			// TODO: make keys more ElongatedTriangular-like and less Rectangular-like
			case "tileWidth" => parser.parsePrimitive(input).right.flatMap{_.integerToEither{x => Right(folding.copy(width = x))}}
			case "tileHeight" => parser.parsePrimitive(input).right.flatMap{_.integerToEither{x => Right(folding.copy(squareHeight = x, triangleHeight = x))}}
			case _ => {parser.parse(ElongatedTriangularDimensionBuilder.this, input); Right(folding)}
		}
		override def finalize(x:ElongatedTriangularDimension):ElongatedTriangularDimension = x
	}
	
	final case class HorizontalHexagonalDimensionDelay(width:Int, height:Int, verticalOffset:Int) {
		val hinset = height - verticalOffset
		
		def build:HorizontalHexagonalDimension = HorizontalHexagonalDimension(width, height, hinset)
	}
}
