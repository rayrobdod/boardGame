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
package com.rayrobdod.boardGame
package view

import scala.annotation.tailrec
import scala.collection.immutable.Seq
import scala.util.Random
import java.awt.Dimension
import java.awt.Color
import java.net.URL

/**
 * 
 */
abstract class PackageObjectTemplate[IconPart, Icon] {
	
	type RenderableComponentType
	
	/** Returns a transparent icon of the specified size */
	def blankIcon():Icon
	/** Returns a solid-colored icon of the specified size */
	def rgbToRectangularIcon(rgb:Color, size:RectangularDimension):Icon
	/** Returns an icon that displays the specified string */
	def stringIcon(text:String, rgb:Color, size:RectangularDimension):Icon
	/**  */
	def compostLayers(layersWithLCMFrames:Seq[Seq[IconPart]]):Icon
	/** Reads an image from the specified URL and splits it into dimension-sized images */
	def sheeturl2images(sheetUrl:URL, tileDimension:Dimension):Seq[IconPart]
	
	
	final def RectangularNilTilesheet:NilTilesheet[RectangularIndex, RectangularDimension, Icon] = {
		new NilTilesheet[RectangularIndex, RectangularDimension, Icon](
			() => blankIcon,
			new RectangularDimension(16, 16)
		)
	}
	final def RectangularHashcodeColorTilesheet(dim:Dimension):HashcodeColorTilesheet[RectangularIndex, RectangularDimension, Icon] = {
		new HashcodeColorTilesheet[RectangularIndex, RectangularDimension, Icon](
			  {() => blankIcon}
			, {x:Color => rgbToRectangularIcon(x, RectangularDimension(dim.width, dim.height))}
			, new RectangularDimension(dim.width, dim.height)
		)
	}
	
	final def VisualizationRuleBasedRectangularTilesheetBuilder[SpaceClass](
		baseUrl:URL,
		classMap:SpaceClassMatcherFactory[SpaceClass]
	):VisualizationRuleBasedTilesheetBuilder[SpaceClass, RectangularIndex, RectangularDimension, RectangularDimension, IconPart, Icon] = {
		new VisualizationRuleBasedTilesheetBuilder[SpaceClass, RectangularIndex, RectangularDimension, RectangularDimension, IconPart, Icon](
			  baseUrl
			, classMap
			, this.compostLayers _
			, this.sheeturl2images _
			, VisualizationRuleBuilder.stringToRectangularIndexTranslation
			, CoordinateFunctionSpecifierParser.rectangularVars
			, new VisualizationRuleBasedTilesheetBuilder.RectangularDimensionBuilder
		)
	}
	
	final def VisualizationRuleBasedHorizontalHexagonalTilesheetBuilder[SpaceClass](
		baseUrl:URL,
		classMap:SpaceClassMatcherFactory[SpaceClass]
	):VisualizationRuleBasedTilesheetBuilder[SpaceClass, HorizontalHexagonalIndex, HorizontalHexagonalDimension, VisualizationRuleBasedTilesheetBuilder.HorizontalHexagonalDimensionDelay, IconPart, Icon] = {
		new VisualizationRuleBasedTilesheetBuilder[SpaceClass, HorizontalHexagonalIndex, HorizontalHexagonalDimension, VisualizationRuleBasedTilesheetBuilder.HorizontalHexagonalDimensionDelay, IconPart, Icon](
			  baseUrl
			, classMap
			, this.compostLayers _
			, this.sheeturl2images _
			, VisualizationRuleBuilder.stringToRectangularIndexTranslation
			, CoordinateFunctionSpecifierParser.hexagonalVars
			, new VisualizationRuleBasedTilesheetBuilder.HorizontalHexagonalDimensionBuilder
		)
	}
	
	
	
	final def renderable[SpaceClass, Index, Dimension](
			  field:Tiling[SpaceClass, Index, _]
			, tilesheet:Tilesheet[SpaceClass, Index, Dimension, Icon]
			, rng:Random = Random
	)(implicit
			iconLocation:IconLocation[Index, Dimension]
	):Tuple2[
		  Renderable[Index, RenderableComponentType]
		, Renderable[Index, RenderableComponentType]
	] = {
		
		val a:Map[Index, (Icon, Icon)] = field.mapIndex{x => ((x, tilesheet.getIconFor(field, x, rng) )) }.toMap
		val top = a.mapValues{_._1}
		val bot = a.mapValues{_._2}
		
		(( this.renderable(top, tilesheet.iconDimensions), this.renderable(bot, tilesheet.iconDimensions) ))
	}
	
	def renderable[Index, Dimension](
			  tiles:Map[Index, Icon]
			, dimension:Dimension
	)(implicit
			iconLocation:IconLocation[Index, Dimension]
	):Renderable[Index, RenderableComponentType]
}
