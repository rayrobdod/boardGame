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
package com.rayrobdod

import scala.language.higherKinds

import java.nio.charset.StandardCharsets.UTF_8
import scala.collection.immutable.Seq
import scala.util.Random
import com.rayrobdod.json.parser.JsonParser
import com.rayrobdod.json.union.StringOrInt
import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.view._

/**
 * 
 */
package object jsonTilesheetViewer {
	type SpaceClass = String
	
	/** @since next */
	val TAG_MAP_ROTATE:String = "tag:rayrobdod.name,2013-08:map-rotate"
	
	/** @since next */
	val TAG_SHEET_NIL:String = "tag:rayrobdod.name,2013-08:tilesheet-nil"
	/** @since next */
	val TAG_SHEET_INDEX:String = "tag:rayrobdod.name,2013-08:tilesheet-indexies"
	/** @since next */
	val TAG_SHEET_RAND:String = "tag:rayrobdod.name,2013-08:tilesheet-randcolor"
	/** @since next */
	val TAG_SHEET_HASH:String = "tag:rayrobdod.name,2015-06-12:tilesheet-hashcolor"
	/** @since next */
	val TAG_SHEET_CHECKER:String = "tag:rayrobdod.name,2013-08:tilesheet-checker"
	
	
	
	private def urlOrFileStringToUrl(s:String):java.net.URL = {
		try {
			new java.net.URL(s)
		} catch {
			case e:java.net.MalformedURLException =>
						new java.io.File(s).toURI.toURL
		}
	}
	
	def allClassesInTilesheet(f:com.rayrobdod.boardGame.view.Tilesheet[SpaceClass, _, _, _]):Seq[SpaceClass] = {
		import com.rayrobdod.boardGame.SpaceClassMatcher
		import com.rayrobdod.boardGame.view.ParamaterizedVisualizationRule
		import com.rayrobdod.boardGame.view.VisualizationRuleBasedTilesheet
		import com.rayrobdod.boardGame.view.HashcodeColorTilesheet
		import StringSpaceClassMatcherFactory.EqualsMatcher
		
		val a = f match {
			case x:VisualizationRuleBasedTilesheet[SpaceClass, _, _, _, _] => {
				val a:Seq[ParamaterizedVisualizationRule[SpaceClass, _, _]] = x.visualizationRules.map{_.asInstanceOf[ParamaterizedVisualizationRule[SpaceClass, _, _]]}
				val b:Seq[Map[_, SpaceClassMatcher[SpaceClass]]] = a.map{_.surroundingTiles}
				val c:Seq[Seq[SpaceClassMatcher[SpaceClass]]] = b.map{(a) => (Seq.empty ++ a.toSeq).map{_._2}}
				val d:Seq[SpaceClassMatcher[SpaceClass]] = c.flatten
				
				val e:Seq[Option[SpaceClass]] = d.map{_ match {
					case EqualsMatcher(ref) => Option(ref)
					case _ => None
				}}
				val f:Seq[SpaceClass] = e.flatten.distinct
				
				f
			}
			// designed to be one of each color // green, blue, red, white
			//case x:HashcodeColorTilesheet[SpaceClass] => Seq("AWv", "Ahf", "\u43c8\u0473\u044b", "")
			case x:HashcodeColorTilesheet[_, _, _] => Seq("a", "b", "c", "d")
			case _ => Seq("")
		}
		
		a
	}
	
	
	def nameToTilesheet[IconPart, Icon](
		  url:String
		, dimProps:NameToTilesheetDemensionType[IconPart, Icon]
	):Tilesheet[SpaceClass, dimProps.templateProps.Index, dimProps.Dimension, Icon] = {
		url match {
			case TAG_SHEET_NIL => dimProps.template.NilTilesheet(dimProps.nilTilesheetDimension)(dimProps.templateProps)
			case TAG_SHEET_INDEX => dimProps.template.IndexesTilesheet(dimProps.textIncludingDimension)(dimProps.templateProps)
			case TAG_SHEET_RAND => dimProps.template.RandomColorTilesheet(dimProps.textIncludingDimension)(dimProps.templateProps)
			case TAG_SHEET_HASH => dimProps.template.HashcodeColorTilesheet(dimProps.hashTilesheetDimension)(dimProps.templateProps)
			case CheckerboardURIMatcher(x) => dimProps.checkerboardTilesheet(x)
			case x:String => {
				val url = urlOrFileStringToUrl(x)
				var r:java.io.Reader = new java.io.StringReader("{}");
				try {
					r = new java.io.InputStreamReader(url.openStream(), UTF_8);
					val parser = new JsonParser()
					dimProps.parseVisualizationRuleTilesheet(parser, r, url)
				} finally {
					r.close();
				}
			}
		}
	}
	
	/**
	 * Things that this demo needs to be parameterized by Dimension type,
	 * but which make no sense being a part of the library
	 */
	trait NameToTilesheetDemensionType[IconPart, Icon] {
		val template:PackageObjectTemplate[IconPart, Icon]
		type Dimension
		type SpaceType[SpaceClass] <: Space[SpaceClass, SpaceType[SpaceClass]]
		val templateProps:template.ProbablePropertiesBasedOnDimension[Dimension]
		def textIncludingDimension:Dimension
		def hashTilesheetDimension:Dimension
		def nilTilesheetDimension:Dimension
		
		def checkerboardTilesheet(x:CheckerboardURIMatcher.CheckerboardTilesheetDelay):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon]
		def parseVisualizationRuleTilesheet(parser:JsonParser, reader:java.io.Reader, baseUrl:java.net.URL):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon]
		
		def initialRotationField[SpaceClass](initialClass:SpaceClass):Tiling[SpaceClass, templateProps.Index, SpaceType[SpaceClass]]
		def arbitraryField[SpaceClass](clazzes:Seq[Seq[SpaceClass]]):Tiling[SpaceClass, templateProps.Index, SpaceType[SpaceClass]]
		def arbitraryField[SpaceClass](clazzes:Map[templateProps.Index, SpaceClass]):Tiling[SpaceClass, templateProps.Index, SpaceType[SpaceClass]]
	}
	final class RectangularNameToTilesheetDemensionType[IconPart, Icon](override val template:PackageObjectTemplate[IconPart, Icon]) extends NameToTilesheetDemensionType[IconPart, Icon] {
		override type Dimension = RectangularDimension
		override type SpaceType[SpaceClass] = StrictRectangularSpace[SpaceClass]
		override val templateProps:template.RectangularProperties.type = template.RectangularProperties
		override def textIncludingDimension:RectangularDimension = RectangularDimension(64, 24)
		override def hashTilesheetDimension:RectangularDimension = RectangularDimension(24, 24)
		override def nilTilesheetDimension:RectangularDimension = RectangularDimension(16, 16)
		
		override def checkerboardTilesheet(x:CheckerboardURIMatcher.CheckerboardTilesheetDelay):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
			x.apply({() => template.blankIcon}, template.rgbToRectangularIcon)
		}
		override def parseVisualizationRuleTilesheet(parser:JsonParser, reader:java.io.Reader, baseUrl:java.net.URL):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
			val b = template.VisualizationRuleBasedRectangularTilesheetBuilder(baseUrl, StringSpaceClassMatcherFactory).mapKey(StringOrInt.unwrapToString)
			parser.parse(b, reader).fold(
				  {x => x}
				, {x => throw new java.text.ParseException("Parsed to primitive", 0)}
				, {(s,i) => throw new java.text.ParseException(s + " : " + i, i)}
			).apply(
				{x => x}
			)
		}
		
		override def initialRotationField[SpaceClass](initialClass:SpaceClass):Tiling[SpaceClass, template.RectangularProperties.Index, SpaceType[SpaceClass]] = {
			RectangularField(Seq.fill(14, 12){initialClass})
		}
		override def arbitraryField[SpaceClass](clazzes:Seq[Seq[SpaceClass]]):Tiling[SpaceClass, templateProps.Index, SpaceType[SpaceClass]] = {
			RectangularField(clazzes)
		}
		override def arbitraryField[SpaceClass](clazzTable:Map[templateProps.Index, SpaceClass]):Tiling[SpaceClass, templateProps.Index, SpaceType[SpaceClass]] = {
			RectangularField(clazzTable)
		}
	}
	
	final class HorizHexNameToTilesheetDemensionType[IconPart, Icon](override val template:PackageObjectTemplate[IconPart, Icon]) extends NameToTilesheetDemensionType[IconPart, Icon] {
		override type Dimension = HorizontalHexagonalDimension
		override type SpaceType[SpaceClass] = StrictHorizontalHexagonalSpace[SpaceClass]
		override val templateProps:template.HorizontalHexagonalProperties.type = template.HorizontalHexagonalProperties
		override def textIncludingDimension:HorizontalHexagonalDimension = HorizontalHexagonalDimension(64, 24, 5)
		override def hashTilesheetDimension:HorizontalHexagonalDimension = HorizontalHexagonalDimension(24, 24, 8)
		override def nilTilesheetDimension:HorizontalHexagonalDimension = HorizontalHexagonalDimension(16, 16, 5)
		
		override def checkerboardTilesheet(x:CheckerboardURIMatcher.CheckerboardTilesheetDelay):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
			throw new IllegalStateException("Checkerboard doesn't support Hex tilings")
		}
		override def parseVisualizationRuleTilesheet(parser:JsonParser, reader:java.io.Reader, baseUrl:java.net.URL):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
			val b = template.VisualizationRuleBasedHorizontalHexagonalTilesheetBuilder(baseUrl, StringSpaceClassMatcherFactory).mapKey(StringOrInt.unwrapToString)
			parser.parse(b, reader).fold(
				  {x => x}
				, {x => throw new java.text.ParseException("Parsed to primitive", 0)}
				, {(s,i) => throw new java.text.ParseException(s + " : " + i, i)}
			).apply(
				{x => x.build}
			)
		}
		
		override def initialRotationField[SpaceClass](initialClass:SpaceClass):Tiling[SpaceClass, template.HorizontalHexagonalProperties.Index, SpaceType[SpaceClass]] = {
			HorizontalHexagonalField( (
				for(
					j <- 0 to 6;
					i <- (0 - (j / 2)) to (6 - (j / 2))
				) yield {
					(i, j) -> initialClass
				}
			).toMap )
		}
		override def arbitraryField[SpaceClass](clazzTable:Seq[Seq[SpaceClass]]):Tiling[SpaceClass, template.HorizontalHexagonalProperties.Index, SpaceType[SpaceClass]] = {
			HorizontalHexagonalField( (
				for(
					(clazzRow, j) <- clazzTable.zipWithIndex;
					(clazz, i) <- clazzRow.zipWithIndex
				) yield {
					(i, j) -> clazz
				}
			).toMap )
		}
		override def arbitraryField[SpaceClass](clazzTable:Map[template.HorizontalHexagonalProperties.Index, SpaceClass]):Tiling[SpaceClass, template.HorizontalHexagonalProperties.Index, SpaceType[SpaceClass]] = {
			HorizontalHexagonalField(clazzTable)
		}
	}
	
	final class ElongTriNameToTilesheetDemensionType[IconPart, Icon](override val template:PackageObjectTemplate[IconPart, Icon]) extends NameToTilesheetDemensionType[IconPart, Icon] {
		override type Dimension = ElongatedTriangularDimension
		override type SpaceType[SpaceClass] = StrictElongatedTriangularSpace[SpaceClass]
		override val templateProps:template.ElongatedTriangularProperties.type = template.ElongatedTriangularProperties
		override def textIncludingDimension:ElongatedTriangularDimension = ElongatedTriangularDimension(64, 32, 32)
		override def hashTilesheetDimension:ElongatedTriangularDimension = ElongatedTriangularDimension(32, 32, 22)
		override def nilTilesheetDimension:ElongatedTriangularDimension = ElongatedTriangularDimension(16, 10, 8)
		
		override def checkerboardTilesheet(x:CheckerboardURIMatcher.CheckerboardTilesheetDelay):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
			throw new IllegalStateException("Checkerboard doesn't support Hex tilings")
		}
		override def parseVisualizationRuleTilesheet(parser:JsonParser, reader:java.io.Reader, baseUrl:java.net.URL):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
			val b = template.VisualizationRuleBasedElongatedTriangularTilesheetBuilder(baseUrl, StringSpaceClassMatcherFactory).mapKey(StringOrInt.unwrapToString)
			parser.parse(b, reader).fold(
				  {x => x}
				, {x => throw new java.text.ParseException("Parsed to primitive", 0)}
				, {(s,i) => throw new java.text.ParseException(s + " : " + i, i)}
			).apply(
				{x => x}
			)
		}
		
		override def initialRotationField[SpaceClass](initialClass:SpaceClass):Tiling[SpaceClass, template.ElongatedTriangularProperties.Index, SpaceType[SpaceClass]] = {
			ElongatedTriangularField( (
				for(
					j <- 0 to 6;
					i <- 0 to 9;
					t <- ElongatedTriangularType.values
				) yield {
					ElongatedTriangularIndex(i, j, t) -> initialClass
				}
			).toMap )
		}
		override def arbitraryField[SpaceClass](clazzTable:Seq[Seq[SpaceClass]]):Tiling[SpaceClass, template.ElongatedTriangularProperties.Index, SpaceType[SpaceClass]] = {
			ElongatedTriangularField( (
				for(
					(clazzRow, yt) <- clazzTable.zipWithIndex;
					(clazz, x) <- clazzRow.zipWithIndex
				) yield {
					val (y, t) = (yt / 3, ElongatedTriangularType.values(yt % 3) )
					
					ElongatedTriangularIndex(x, y, t) -> clazz
				}
			).toMap )
		}
		override def arbitraryField[SpaceClass](clazzTable:Map[template.ElongatedTriangularProperties.Index, SpaceClass]):Tiling[SpaceClass, template.ElongatedTriangularProperties.Index, SpaceType[SpaceClass]] = {
			ElongatedTriangularField(clazzTable)
		}
	}
	
	def nameToRandom(s:String):Random = s match{
		case "" => Random
		case "a" => new Random(new java.util.Random(){override def next(bits:Int):Int = 1})
		case "b" => new Random(new java.util.Random(){override def next(bits:Int):Int = 0})
		case s => try {
			new Random(s.toLong)
		} catch {
			case e:NumberFormatException => {
				throw new IllegalStateException(
						"Seed must be '', 'a', 'b' or an integer",
						e
				)
			}
		}
	}
	
	def nameToField(
		  url:String
		, props:NameToTilesheetDemensionType[_, _]
	):Tiling[SpaceClass, props.templateProps.Index, props.SpaceType[SpaceClass]] = {
		import java.io.InputStreamReader
		import com.opencsv.CSVReader
		
		val layoutReader = new InputStreamReader(urlOrFileStringToUrl(url).openStream(), UTF_8)
		val layoutTable:Seq[Seq[String]] = {
			import scala.collection.JavaConversions.collectionAsScalaIterable;
			
			val reader = new CSVReader(layoutReader);
			val letterTable3 = reader.readAll();
			val letterTable = Seq.empty ++ letterTable3.map{Seq.empty ++ _}
			
			letterTable
		}
		
		props.arbitraryField( layoutTable )
	}
}
