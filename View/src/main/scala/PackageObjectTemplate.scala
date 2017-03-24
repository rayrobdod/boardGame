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

import scala.collection.immutable.Seq
import scala.util.Random
import java.awt.{Dimension => AwtDimension}
import java.awt.Color
import java.net.URL

/**
 * 
 */
abstract class PackageObjectTemplate[IconPart, Icon] {
	
	type RenderableComponentType
	
	/** Returns a transparent icon of the specified size */
	def blankIcon():Icon
	/** Returns a solid-colored rectangular icon of the specified size */
	def rgbToRectangularIcon(rgb:Color, size:RectangularDimension):Icon
	/** Returns a solid-colored hexagonal icon of the specified size */
	def rgbToHorizontalHexagonalIcon(rgb:Color, size:HorizontalHexagonalDimension):Icon
	/** Returns an icon that displays the specified string */
	def stringIcon(text:String, rgb:Color, size:RectangularDimension):Icon
	/**  */
	def compostLayers(layersWithLCMFrames:Seq[Seq[IconPart]]):Icon
	/** Reads an image from the specified URL and splits it into dimension-sized images */
	def sheeturl2images(sheetUrl:URL, tileDimension:AwtDimension):Seq[IconPart]
	
	
	final def NilTilesheet[Dimension](dim:Dimension)(implicit ev:ProbablePropertiesBasedOnDimension[Dimension]):NilTilesheet[ev.Index, Dimension, Icon] = {
		new NilTilesheet[ev.Index, Dimension, Icon](
			() => blankIcon,
			dim
		)
	}
	final def HashcodeColorTilesheet[Dimension](dim:Dimension)(implicit ev:ProbablePropertiesBasedOnDimension[Dimension]):HashcodeColorTilesheet[ev.Index, Dimension, Icon] = {
		new HashcodeColorTilesheet[ev.Index, Dimension, Icon](
			  {() => blankIcon}
			, {x:Color => ev.rgbToIcon(x, dim)}
			, dim
		)
	}
	final def IndexesTilesheet[Dimension](dim:Dimension)(implicit ev:ProbablePropertiesBasedOnDimension[Dimension]):IndexesTilesheet[ev.Index, Dimension, Icon] = {
		new IndexesTilesheet(
			{x:ev.Index => ev.rgbToIcon( ev.indexiesTilesheetColor(x), dim )},
			{s:String => stringIcon(s, Color.black, RectangularDimension(64, 24))},
			dim
		)
	}
	final def RandomColorTilesheet[Dimension](dim:Dimension)(implicit ev:ProbablePropertiesBasedOnDimension[Dimension]):RandomColorTilesheet[ev.Index, Dimension, Icon] = {
		new RandomColorTilesheet(
			ev.rgbToIcon _,
			{(s:String, rgb:Color, blarg:Dimension) => stringIcon(s, rgb, RectangularDimension(64, 24))},
			dim
		)
	}
		
	
	trait ProbablePropertiesBasedOnDimension[Dimension] {
		type Index
		def rgbToIcon(c:Color, d:Dimension):Icon
		def indexiesTilesheetColor(idx:Index):Color
		def iconLocation:IconLocation[Index, Dimension]
	}
	implicit object RectangularProperties extends ProbablePropertiesBasedOnDimension[RectangularDimension] {
		override type Index = RectangularIndex
		override def rgbToIcon(c:Color, d:RectangularDimension):Icon = rgbToRectangularIcon(c,d)
		override def indexiesTilesheetColor(idx:Index):Color = { if ((idx._1 + idx._2) % 2 == 0) {Color.cyan} else {Color.magenta} }
		override def iconLocation:RectangularIconLocation.type = RectangularIconLocation
	}
	implicit object HorizontalHexagonalProperties extends ProbablePropertiesBasedOnDimension[HorizontalHexagonalDimension] {
		override type Index = HorizontalHexagonalIndex
		override def rgbToIcon(c:Color, d:HorizontalHexagonalDimension):Icon = rgbToHorizontalHexagonalIcon(c,d)
		override def indexiesTilesheetColor(idx:Index):Color = { math.abs((idx._1 + idx._1 + idx._2) % 3) match {
			case 0 => Color.cyan
			case 1 => Color.magenta
			case 2 => new Color(0.5f, 1.0f, 0.5f)
		}}
		override def iconLocation:HorizontalHexagonalIconLocation.type = HorizontalHexagonalIconLocation
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
