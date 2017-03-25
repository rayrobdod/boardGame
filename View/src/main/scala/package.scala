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

import scala.annotation.tailrec

/**
 * 
 * @groupprio Tilesheet 300
 * @groupname TrivialTilesheet Tilesheets - Trivial
 * @groupprio TrivialTilesheet 301
 * @groupname VisualizationRuleTilesheet Tilesheets - Visualization Rule Based
 * @groupprio VisualizationRuleTilesheet 302
 * @groupprio SpaceClassMatcherFactory 500
 * @groupprio RendererTemplate 101
 * @groupprio IconLocation 500
 * @groupprio Dimension 500
 * @groupprio CoordinateFunction 500
 */
package object view {
	type IndexConverter[Index] = Function1[Index, Index]
	
	/**
	 * Least Common Multiple
	 * @group utility
	 */
	private[view] def lcm(x:Int, y:Int):Int = x / gcd(x,y) * y
	
	/**
	 * Greatest Common Denominator
	 * @group utility
	 */
	@tailrec private[view] def gcd(x:Int, y:Int):Int = {
		if (y == 1) {1} else
		if (x == 1) {1} else
		if (x == y) {x} else
		if (x > y) {gcd(x - y, y)} else
		{gcd(x, y - x)}
	}
	
	
	/**
	 * Functions that specify the screen positions of tiles in a Rectangular Tiling
	 * @group IconLocation
	 */
	implicit object RectangularIconLocation extends IconLocation[RectangularIndex, RectangularDimension] {
		
		override def bounds(idx:RectangularIndex, dim:RectangularDimension):java.awt.Rectangle = {
			new java.awt.Rectangle(
				idx._1 * dim.width,
				idx._2 * dim.height,
				dim.width,
				dim.height
			)
		}
		
		override def hit(point:(Int, Int), dim:RectangularDimension):RectangularIndex = ((
			point._1 / dim.width,
			point._2 / dim.height
		))
	}
	
	/**
	 * Functions that specify the screen positions of tiles in a Horizontal Hexagonal Tiling
	 * @group IconLocation
	 */
	implicit object HorizontalHexagonalIconLocation extends IconLocation[HorizontalHexagonalIndex, HorizontalHexagonalDimension] {
		
		override def bounds(idx:HorizontalHexagonalIndex, dim:HorizontalHexagonalDimension) = {
			val (ew, nwse) = idx
			new java.awt.Rectangle(
				dim.width * ew + (dim.width / 2) * nwse,
				dim.verticalOffset * nwse,
				dim.width,
				dim.height
			)
		}
		
		override def hit(coords:(Int, Int), dim:HorizontalHexagonalDimension) = {
			// Using the 'geometric rectangle-and-two-triangles' approach
			// high-level is described at: http://gdreflections.com/2011/02/hexagonal-grid-math.html
			
			
			// find the box which contains the top 2/3 of the hexagon
			val col = coords._2 / dim.verticalOffset
			val row = (coords._1 - (if (col % 2 == 0) {0} else {dim.width / 2})) / dim.width - col / 2
			
			// translate the coordinate to be relative to (row,col)'s bounding box
			val bounds = this.bounds( ((row, col)), dim)
			val localCoords = ((coords._1 - bounds.x, coords._2 - bounds.y))
			
			if (localCoords._1 < dim.width / 2) {
				// west half of bounding box
				
				val (x1, y1, x2, y2) = (0, dim.hinset, dim.width / 2.0, 0)
				def line(x:Double):Double = ((y2 - y1) / (x2 - x1)) * x + y1 - ((y2 - y1) / (x2 - x1)) * x1
				
				// if coord is north of that line, then the point is in the hex northwest of the bounding box's hex, else it is in the bounding box's hex 
				if (line(localCoords._1) >= localCoords._2) {
					// hex northwest of bounding box
					(row, col - 1)
				} else {
					(row, col)
				}
				
			} else {
				// east half of bounding box
				
				val (x1, y1, x2, y2) = (dim.width, dim.hinset, dim.width / 2.0, 0)
				def line(x:Double):Double = ((y2 - y1) / (x2 - x1)) * x + y1 - ((y2 - y1) / (x2 - x1)) * x1
				
				// if coord is north of that line, then the point is in the hex northeast of the bounding box's hex, else it is in the bounding box's hex
				if (line(localCoords._1) >= localCoords._2) {
					// hex northeast of bounding box
					(row + 1, col - 1)
				} else {
					(row, col)
				}
			}
		}
	}
	
}

package view {
	/**
	 * A function that takes a string and converts it into a SpaceClassMatcher
	 * @group SpaceClassMatcherFactory
	 */
	trait SpaceClassMatcherFactory[-SpaceClass] {
		def apply(reference:String):SpaceClassMatcher[SpaceClass]
	}
	
	/**
	 * A tilesheet which will always return the Icon specified in the constructor
	 * @group TrivialTilesheet
	 */
	final class NilTilesheet[Index, Dimension, Icon](
			  private[this] val tile:Function0[Icon]
			, override val iconDimensions:Dimension
	) extends Tilesheet[Any, Index, Dimension, Icon] {
		override def getIconFor(f:Tiling[_ <: Any, Index, _], xy:Index, rng:scala.util.Random):(Icon, Icon) = ((tile(), tile()))
	}
	
	
	/**
	 * The dimensions describing a rectangular tile
	 * @group Dimension
	 */
	final case class RectangularDimension(width:Int, height:Int)
	
	/**
	 * The dimensions describing a horizontal hexagonal tile
	 * @group Dimension
	 */
	final case class HorizontalHexagonalDimension(width:Int, height:Int, hinset:Int) {
		val verticalOffset = height - hinset
	}
	
	/**
	 * The dimensions describing elongated triangular tiles
	 * @group Dimension
	 */
	final case class ElongatedTriangularDimension(width:Int, squareHeight:Int, triangleHeight:Int)
	
	/**
	 * Functions that specify the screen positions of tiles in a tiling
	 * @group IconLocation
	 */
	trait IconLocation[Index, Dimension] {
		/** A bounding box for the tile at `idx`  */
		def bounds(idx:Index, dim:Dimension):java.awt.Rectangle
		/** The space which contains the `point` */
		def hit(point:(Int, Int), dim:Dimension):Index
	}
	
}
