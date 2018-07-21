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

import scala.collection.immutable.Seq
import com.rayrobdod.json.parser.JsonParser
import com.rayrobdod.json.union.StringOrInt
import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.view._

/**
 * Things that this demo needs to be parameterized by Dimension type,
 * but which make no sense being a part of the library
 */
trait NameToTilesheetDemensionType[IconPart, Icon] {
	val template:PackageObjectTemplate[IconPart, Icon]
	type Dimension
	type SpaceType <: SpaceLike[SpaceClass, SpaceType]
	val templateProps:template.ProbablePropertiesBasedOnDimension[Dimension]
	def textIncludingDimension:Dimension
	def hashTilesheetDimension:Dimension
	def nilTilesheetDimension:Dimension
	
	def checkerboardTilesheet(
		x:CheckerboardURIMatcher.CheckerboardTilesheetDelay
	):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon]
	
	def parseVisualizationRuleTilesheet(
		parser:JsonParser,
		reader:java.io.Reader,
		baseUrl:java.net.URL
	):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon]
	
	def initialRotationField(initialClass:SpaceClass):Tiling[SpaceClass, templateProps.Index, SpaceType]
	def arbitraryField(clazzes:Seq[Seq[SpaceClass]]):Tiling[SpaceClass, templateProps.Index, SpaceType]
	def arbitraryField(clazzes:Map[templateProps.Index, SpaceClass]):Tiling[SpaceClass, templateProps.Index, SpaceType]
}

final class RectangularNameToTilesheetDemensionType[IconPart, Icon](
	override val template:PackageObjectTemplate[IconPart, Icon]
) extends NameToTilesheetDemensionType[IconPart, Icon] {
	override type Dimension = RectangularDimension
	override type SpaceType = RectangularSpace[SpaceClass]
	override val templateProps:template.RectangularProperties.type = template.RectangularProperties
	override def textIncludingDimension:RectangularDimension = RectangularDimension(64, 24)
	override def hashTilesheetDimension:RectangularDimension = RectangularDimension(24, 24)
	override def nilTilesheetDimension:RectangularDimension = RectangularDimension(16, 16)
	
	override def checkerboardTilesheet(
		x:CheckerboardURIMatcher.CheckerboardTilesheetDelay
	):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
		x.apply({() => template.blankIcon}, template.rgbToRectangularIcon)
	}
	override def parseVisualizationRuleTilesheet(
		parser:JsonParser, reader:java.io.Reader, baseUrl:java.net.URL
	):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
		val b = template.VisualizationRuleBasedRectangularTilesheetBuilder(baseUrl, StringSpaceClassMatcherFactory).mapKey[StringOrInt](_.fold({x => x}, {_.toString}))
		parser.parse(b, reader).fold(
			  {x => x}
			, {x => throw new java.text.ParseException("Parsed to primitive", 0)}
			, {x => throw new java.text.ParseException("" + x, 0)}
			, {(x, e) => throw new java.text.ParseException("" + x + e, 0)}
		)
	}
	
	override def initialRotationField(initialClass:SpaceClass):Tiling[SpaceClass, template.RectangularProperties.Index, SpaceType] = {
		RectangularField(Seq.fill(12, 14){initialClass})
	}
	override def arbitraryField(clazzes:Seq[Seq[SpaceClass]]):Tiling[SpaceClass, templateProps.Index, SpaceType] = {
		RectangularField(clazzes)
	}
	override def arbitraryField(clazzTable:Map[templateProps.Index, SpaceClass]):Tiling[SpaceClass, templateProps.Index, SpaceType] = {
		RectangularField(clazzTable)
	}
}

final class HorizHexNameToTilesheetDemensionType[IconPart, Icon](
	override val template:PackageObjectTemplate[IconPart, Icon]
) extends NameToTilesheetDemensionType[IconPart, Icon] {
	override type Dimension = HorizontalHexagonalDimension
	override type SpaceType = HorizontalHexagonalSpace[SpaceClass]
	override val templateProps:template.HorizontalHexagonalProperties.type = template.HorizontalHexagonalProperties
	override def textIncludingDimension:HorizontalHexagonalDimension = HorizontalHexagonalDimension(64, 24, 5)
	override def hashTilesheetDimension:HorizontalHexagonalDimension = HorizontalHexagonalDimension(24, 24, 8)
	override def nilTilesheetDimension:HorizontalHexagonalDimension = HorizontalHexagonalDimension(16, 16, 5)
	
	override def checkerboardTilesheet(
		x:CheckerboardURIMatcher.CheckerboardTilesheetDelay
	):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
		throw new IllegalStateException("Checkerboard doesn't support Hex tilings")
	}
	override def parseVisualizationRuleTilesheet(
		parser:JsonParser, reader:java.io.Reader, baseUrl:java.net.URL
	):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
		val b = template.VisualizationRuleBasedHorizontalHexagonalTilesheetBuilder(baseUrl, StringSpaceClassMatcherFactory).mapKey[StringOrInt](_.fold({x => x}, {_.toString}))
		parser.parse(b, reader).fold(
			  {x => x}
			, {x => throw new java.text.ParseException("Parsed to primitive", 0)}
			, {x => throw new java.text.ParseException("" + x, 0)}
			, {(x, e) => throw new java.text.ParseException("" + x + e, 0)}
		)
	}
	
	override def initialRotationField(
		initialClass:SpaceClass
	):Tiling[SpaceClass, template.HorizontalHexagonalProperties.Index, SpaceType] = {
		HorizontalHexagonalField( (
			for(
				j <- 0 to 6;
				i <- (0 - (j / 2)) to (6 - (j / 2))
			) yield {
				(i, j) -> initialClass
			}
		).toMap )
	}
	override def arbitraryField(
		clazzTable:Seq[Seq[SpaceClass]]
	):Tiling[SpaceClass, template.HorizontalHexagonalProperties.Index, SpaceType] = {
		HorizontalHexagonalField( (
			for(
				(clazzRow, j) <- clazzTable.zipWithIndex;
				(clazz, i) <- clazzRow.zipWithIndex
			) yield {
				(i, j) -> clazz
			}
		).toMap )
	}
	override def arbitraryField(
		clazzTable:Map[template.HorizontalHexagonalProperties.Index, SpaceClass]
	):Tiling[SpaceClass, template.HorizontalHexagonalProperties.Index, SpaceType] = {
		HorizontalHexagonalField(clazzTable)
	}
}

final class ElongTriNameToTilesheetDemensionType[IconPart, Icon](
	override val template:PackageObjectTemplate[IconPart, Icon]
) extends NameToTilesheetDemensionType[IconPart, Icon] {
	override type Dimension = ElongatedTriangularDimension
	override type SpaceType = ElongatedTriangularSpace[SpaceClass]
	override val templateProps:template.ElongatedTriangularProperties.type = template.ElongatedTriangularProperties
	override def textIncludingDimension:ElongatedTriangularDimension = ElongatedTriangularDimension(64, 32, 32)
	override def hashTilesheetDimension:ElongatedTriangularDimension = ElongatedTriangularDimension(32, 32, 22)
	override def nilTilesheetDimension:ElongatedTriangularDimension = ElongatedTriangularDimension(16, 10, 8)
	
	override def checkerboardTilesheet(
		x:CheckerboardURIMatcher.CheckerboardTilesheetDelay
	):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
		throw new IllegalStateException("Checkerboard doesn't support Hex tilings")
	}
	
	override def parseVisualizationRuleTilesheet(
		parser:JsonParser, reader:java.io.Reader, baseUrl:java.net.URL
	):Tilesheet[SpaceClass, templateProps.Index, Dimension, Icon] = {
		val b = template.VisualizationRuleBasedElongatedTriangularTilesheetBuilder(baseUrl, StringSpaceClassMatcherFactory).mapKey[StringOrInt](_.fold({x => x}, {_.toString}))
		parser.parse(b, reader).fold(
			  {x => x}
			, {x => throw new java.text.ParseException("Parsed to primitive", 0)}
			, {x => throw new java.text.ParseException("" + x, 0)}
			, {(x, e) => throw new java.text.ParseException("" + x + e, 0)}
		)
	}
	
	override def initialRotationField(
		initialClass:SpaceClass
	):Tiling[SpaceClass, template.ElongatedTriangularProperties.Index, SpaceType] = {
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
	
	override def arbitraryField(
		clazzTable:Seq[Seq[SpaceClass]]
	):Tiling[SpaceClass, template.ElongatedTriangularProperties.Index, SpaceType] = {
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
	
	override def arbitraryField(
		clazzTable:Map[template.ElongatedTriangularProperties.Index, SpaceClass]
	):Tiling[SpaceClass, template.ElongatedTriangularProperties.Index, SpaceType] = {
		ElongatedTriangularField(clazzTable)
	}
}