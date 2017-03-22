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
	
	/** Returns a transparent icon of the specified size */
	def blankIcon(size:Dimension):Icon
	/** Returns a solid-colored icon of the specified size */
	def rgbToIcon(rgb:Color, size:Dimension):Icon
	/** Returns an icon that displays the specified string */
	def stringIcon(text:String, rgb:Color, size:Dimension):Icon
	/**  */
	def compostLayers(layersWithLCMFrames:Seq[Seq[IconPart]]):Icon
	/** Reads an image from the specified URL and splits it into dimension-sized images */
	def sheeturl2images(sheetUrl:URL, tileDimension:Dimension):Seq[IconPart]
	
	
	final def NilTilesheet:NilTilesheet[Icon] = new NilTilesheet[Icon](() => blankIcon(new Dimension(16, 16)))
	final def HashcodeColorTilesheet(dim:Dimension):HashcodeColorTilesheet[RectangularIndex, Icon] = {
		new HashcodeColorTilesheet[RectangularIndex, Icon](
			{() => blankIcon(dim)},
			{x:Color => rgbToIcon(x, dim)}
		)
	}
	
	final def VisualizationRuleBasedRectangularTilesheetBuilder[SpaceClass](
		baseUrl:URL,
		classMap:SpaceClassMatcherFactory[SpaceClass]
	):VisualizationRuleBasedRectangularTilesheetBuilder[SpaceClass, IconPart, Icon] = {
		new VisualizationRuleBasedRectangularTilesheetBuilder[SpaceClass, IconPart, Icon](
			baseUrl,
			classMap,
			this.compostLayers _,
			this.sheeturl2images _
		)
	}
}
