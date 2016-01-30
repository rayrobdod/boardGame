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
package com.rayrobdod.boardGame.javafxView

import scala.util.Random
import scala.collection.immutable.BitSet
import javafx.scene.Node
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Color
import com.rayrobdod.boardGame.RectangularField

/**
 * A tilesheet that uses the tile's hashcode to determine what color to use
 * 
 * @version next
 * 
 * @constructor Creates a HashcodeColorTilesheet
 * @param dim the size of each tile in the checkerboard
 */
case class HashcodeColorTilesheet(
		val dim:Dimension = new Dimension(16,16)
) extends RectangularTilesheet[Any] {
	override def name:String = "HashcodeColor";
	override def toString:String = name + ", " + dim;
	
	def transparentIcon = new Rectangle(dim.width, dim.height, Color.TRANSPARENT)
	
	def getImageFor(f:RectangularField[_ <: Any], x:Int, y:Int, rng:Random):(Node, Node) = {
		val hash = f.apply((x,y)).typeOfSpace.hashCode
		// reorder bits to make most colors not really close to black
		val set1 = BitSet.fromBitMask(Array(hash))
		val color = Seq(
			set1(0), set1(3), set1(6), set1(9),  false,false,false,false,
			set1(1), set1(4), set1(7), set1(10), false,false,false,false,
			set1(2), set1(5), set1(8), set1(11), false,false,false,false
		).reverse.zipWithIndex.filter{_._1}.map{_._2}.foldLeft(BitSet.empty){_ + _}.toBitMask.head.intValue
		
		val colorIcon = new Rectangle(dim.width, dim.height, Color.rgb((color >> 16) % 256, (color >> 8) % 256, (color >> 0) % 256))
		(( colorIcon, transparentIcon ))
	}
}
