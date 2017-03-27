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

import scala.collection.immutable.Seq
import scala.util.Random
import java.awt.{Dimension => AwtDimension}
import java.awt.Color
import java.net.URL

/**
 * Contains methods that create things specific to a rendering technology
 * 
 * @tparam IconPart the layers that compose Icons in this rendering technology
 * @tparam Icon the Icon type of the rendering technology
 * 
 * @group RendererTemplate
 * 
 * @contentDiagram
 * @groupprio Icons 100
 * @groupprio Tilesheet 200
 * @groupprio DimensionProperties 210
 * @groupprio Renderable 110
 */
abstract class PackageObjectTemplate[IconPart, Icon] {
	
	/**
	 * Returns a transparent icon
	 * @group Icons
	 */
	def blankIcon:Icon
	/**
	 * Returns a solid-colored rectangular icon of the specified size
	 * @group Icons
	 */
	def rgbToRectangularIcon(rgb:Color, size:RectangularDimension):Icon
	/**
	 * Returns a solid-colored hexagonal icon of the specified size
	 * @group Icons
	 */
	final def rgbToHorizontalHexagonalIcon(rgb:Color, size:HorizontalHexagonalDimension):Icon = {
		this.rgbToPolygonIcon(rgb, size.toPolygon)
	}
	/**
	 * Returns a solid-colored icon of the specified
	 * @group Icons
	 */
	def rgbToPolygonIcon(rgb:Color, size:java.awt.Polygon):Icon
	/**
	 * Returns an icon that displays the specified string
	 * @group Icons
	 */
	def stringIcon(text:String, rgb:Color, size:RectangularDimension):Icon
	/**
	 * 
	 * @group Icons
	 */
	def compostLayers(layersWithLCMFrames:Seq[Seq[IconPart]]):Icon
	/**
	 * Reads an image from the specified URL and splits it into dimension-sized images
	 * @group Icons
	 */
	def sheeturl2images(sheetUrl:URL, tileDimension:AwtDimension):Seq[IconPart]
	
	
	/**
	 * Returns a [[view.NilTilesheet]] that uses this template's blankIcon
	 * as it's icon
	 * @group Tilesheet
	 */
	final def NilTilesheet[Dimension](
			dim:Dimension)(implicit ev:ProbablePropertiesBasedOnDimension[Dimension]
	):NilTilesheet[ev.Index, Dimension, Icon] = {
		new NilTilesheet[ev.Index, Dimension, Icon](
			() => blankIcon,
			dim
		)
	}
	/**
	 * @group Tilesheet
	 */
	final def HashcodeColorTilesheet[Dimension](
			dim:Dimension)(implicit ev:ProbablePropertiesBasedOnDimension[Dimension]
	):HashcodeColorTilesheet[ev.Index, Dimension, Icon] = {
		new HashcodeColorTilesheet[ev.Index, Dimension, Icon](
			  {() => blankIcon}
			, {(rgb:Color, idx:ev.Index) => ev.rgbToIcon(rgb, dim, idx)}
			, dim
		)
	}
	/**
	 * @group Tilesheet
	 */
	final def IndexesTilesheet[Dimension](
			dim:Dimension)(implicit ev:ProbablePropertiesBasedOnDimension[Dimension]
	):IndexesTilesheet[ev.Index, Dimension, Icon] = {
		new IndexesTilesheet(
			{x:ev.Index => ev.rgbToIcon( ev.indexiesTilesheetColor(x), dim, x )},
			{s:String => stringIcon(s, Color.black, RectangularDimension(64, 24))},
			dim
		)
	}
	/**
	 * @group Tilesheet
	 */
	final def RandomColorTilesheet[Dimension](
			dim:Dimension)(implicit ev:ProbablePropertiesBasedOnDimension[Dimension]
	):RandomColorTilesheet[ev.Index, Dimension, Icon] = {
		new RandomColorTilesheet(
			ev.rgbToIcon _,
			{(s:String, rgb:Color, blarg:Dimension) => stringIcon(s, rgb, RectangularDimension(64, 24))},
			dim
		)
	}
		
	
	/**
	 * @group DimensionProperties
	 */
	trait ProbablePropertiesBasedOnDimension[Dimension] {
		type Index
		def rgbToIcon(c:Color, d:Dimension, i:Index):Icon
		def indexiesTilesheetColor(idx:Index):Color
		def iconLocation:IconLocation[Index, Dimension]
	}
	/**
	 * @group DimensionProperties
	 */
	implicit final object RectangularProperties extends ProbablePropertiesBasedOnDimension[RectangularDimension] {
		override type Index = RectangularIndex
		override def rgbToIcon(c:Color, d:RectangularDimension, i:Index):Icon = rgbToRectangularIcon(c,d)
		override def indexiesTilesheetColor(idx:Index):Color = { if ((idx._1 + idx._2) % 2 == 0) {Color.cyan} else {Color.magenta} }
		override def iconLocation:RectangularIconLocation.type = RectangularIconLocation
	}
	/**
	 * @group DimensionProperties
	 */
	implicit final object HorizontalHexagonalProperties extends ProbablePropertiesBasedOnDimension[HorizontalHexagonalDimension] {
		override type Index = HorizontalHexagonalIndex
		override def rgbToIcon(c:Color, d:HorizontalHexagonalDimension, i:Index):Icon = rgbToHorizontalHexagonalIcon(c,d)
		override def indexiesTilesheetColor(idx:Index):Color = { math.abs((idx._1 + idx._1 + idx._2) % 3) match {
			case 0 => Color.cyan
			case 1 => Color.magenta
			case 2 => new Color(0.5f, 1.0f, 0.5f)
		}}
		override def iconLocation:HorizontalHexagonalIconLocation.type = HorizontalHexagonalIconLocation
	}
	/**
	 * @group DimensionProperties
	 */
	implicit final object ElongatedTriangularProperties extends ProbablePropertiesBasedOnDimension[ElongatedTriangularDimension] {
		override type Index = ElongatedTriangularIndex
		import ElongatedTriangularType.{NorthTri, Square, SouthTri}
		
		override def rgbToIcon(c:Color, d:ElongatedTriangularDimension, i:Index):Icon = i.typ match {
			case Square => rgbToRectangularIcon(c, new RectangularDimension(d.width, d.squareHeight))
			case NorthTri => rgbToPolygonIcon(c, d.northTriToPolygon)
			case SouthTri => rgbToPolygonIcon(c, d.southTriToPolygon)
		}
		
		override def indexiesTilesheetColor(idx:Index):Color = {
			val hue = idx match {
				case ElongatedTriangularIndex(x, _, Square) if x % 2 == 0 => 0.125f
				case ElongatedTriangularIndex(x, _, Square) if x % 2 == 1 => 0.625f
				case ElongatedTriangularIndex(_, _, NorthTri) => 0.375f
				case ElongatedTriangularIndex(_, _, SouthTri) => 0.875f
			}
			
			Color.getHSBColor(hue, 0.8f, 0.95f)
		}
		
		override def iconLocation:ElongatedTriangularIconLocation.type = ElongatedTriangularIconLocation
	}
	
	
	
	/**
	 * @group Tilesheet
	 */
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
	
	/**
	 * @group Tilesheet
	 */
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
	
	/**
	 * @group Tilesheet
	 */
	final def VisualizationRuleBasedElongatedTriangularTilesheetBuilder[SpaceClass](
		baseUrl:URL,
		classMap:SpaceClassMatcherFactory[SpaceClass]
	):VisualizationRuleBasedTilesheetBuilder[SpaceClass, ElongatedTriangularIndex, ElongatedTriangularDimension, ElongatedTriangularDimension, IconPart, Icon] = {
		new VisualizationRuleBasedTilesheetBuilder[SpaceClass, ElongatedTriangularIndex, ElongatedTriangularDimension, ElongatedTriangularDimension, IconPart, Icon](
			  baseUrl
			, classMap
			, this.compostLayers _
			, this.sheeturl2images _
			, VisualizationRuleBuilder.stringToElongatedTriangularIndexTranslation
			, CoordinateFunctionSpecifierParser.elongatedTriangularVars
			, new VisualizationRuleBasedTilesheetBuilder.ElongatedTriangularDimensionBuilder
		)
	}
	
	/**
	 * @group Renderable
	 */
	type RenderableComponentType
	
	/**
	 * @group Renderable
	 */
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
	
	/**
	 * @group Renderable
	 */
	def renderable[Index, Dimension](
			  tiles:Map[Index, Icon]
			, dimension:Dimension
	)(implicit
			iconLocation:IconLocation[Index, Dimension]
	):Renderable[Index, RenderableComponentType]
}
