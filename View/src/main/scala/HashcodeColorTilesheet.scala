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

import scala.collection.immutable.BitSet
import scala.util.Random

/**
 * A tilesheet that makes solid color tile images, where the color of each
 * tile is dependent upon that tile's space's spaceClass's hashcode. 
 * 
 * @group TrivialTilesheet
 * 
 * @constructor
 * Creates a HashcodeColorTilesheet
 * @param transparentIcon a transparent icon
 * @param colorToIcon an icon that deisplays the specified color
 * @param iconDimensions the size of each tile
 * @tparam Index the index used to specify a space in a tiling
 * @tparam Dimension the dimensions of the thlesheet's tiles
 * @tparam Icon the icon produced by this tilesheet
 */
final class HashcodeColorTilesheet[Index, Dimension, Icon](
	  transparentIcon:Function0[Icon]
	, colorToIcon:Function2[java.awt.Color, Index, Icon]
	, override val iconDimensions:Dimension
) extends Tilesheet[Any, Index, Dimension, Icon] {
	
	def getIconFor(f:Tiling[_ <: Any, Index, _], xy:Index, rng:Random):(Icon, Icon) = {
		(( colorToIcon(getColorFor(f,xy), xy), transparentIcon() ))
	}
	
	private[this] def getColorFor(f:Tiling[_ <: Any, Index, _], xy:Index):java.awt.Color = {
		val hash = f.spaceClass(xy).map{_.hashCode}.getOrElse{0}
		// reorder bits to make most colors not really close to black
		val set1 = BitSet.fromBitMask(Array(hash))
		val color = Seq(
			set1(0), set1(3), set1(6), set1(9),  false,false,false,false,
			set1(1), set1(4), set1(7), set1(10), false,false,false,false,
			set1(2), set1(5), set1(8), set1(11), false,false,false,false
		).reverse.zipWithIndex.filter{_._1}.map{_._2}.foldLeft(BitSet.empty){_ + _}.toBitMask.head.intValue
		new java.awt.Color(color)
	}
}