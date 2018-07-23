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

import scala.annotation.tailrec
import java.awt.{Rectangle, Polygon}
import scala.collection.immutable.Seq

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
	type AnimationFrames[Icon] = Seq[Icon]
	
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
		
		override def bounds(idx:RectangularIndex, dim:RectangularDimension):Rectangle = {
			new Rectangle(
				idx._1 * dim.width,
				idx._2 * dim.height,
				dim.width,
				dim.height
			)
		}
		
		override def hit(point:(Int, Int), dim:RectangularDimension):RectangularIndex = ((
			divideRoundDown(point._1, dim.width),
			divideRoundDown(point._2, dim.height)
		))
	}
	
	/**
	 * Functions that specify the screen positions of tiles in a Horizontal Hexagonal Tiling
	 * @group IconLocation
	 */
	implicit object HorizontalHexagonalIconLocation extends IconLocation[HorizontalHexagonalIndex, HorizontalHexagonalDimension] {
		
		override def bounds(idx:HorizontalHexagonalIndex, dim:HorizontalHexagonalDimension):Rectangle = {
			val (ew, nwse) = idx
			new Rectangle(
				dim.width * ew + (dim.width / 2) * nwse,
				dim.verticalOffset * nwse,
				dim.width,
				dim.height
			)
		}
		
		override def hit(coords:(Int, Int), dim:HorizontalHexagonalDimension):HorizontalHexagonalIndex = {
			// Using the 'geometric rectangle-and-two-triangles' approach
			// high-level is described at: http://gdreflections.com/2011/02/hexagonal-grid-math.html
			
			
			// find the box which contains the top 2/3 of the hexagon
			val col = divideRoundDown(coords._2, dim.verticalOffset)
			val row = {
				val adjustedX = (coords._1 - (if (col % 2 == 0) {0} else {dim.width / 2})) 
				divideRoundDown(adjustedX, dim.width) - divideRoundDown(col, 2)
			}
			
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
	
	
	/**
	 * Functions that specify the screen positions of tiles in a Horizontal Elongated Triangular Tiling
	 * @group IconLocation
	 */
	implicit object ElongatedTriangularIconLocation extends IconLocation[ElongatedTriangularIndex, ElongatedTriangularDimension] {
		
		override def bounds(idx:ElongatedTriangularIndex, dim:ElongatedTriangularDimension):Rectangle = {
			val ElongatedTriangularIndex(idxX, idxY, typ) = idx
			val ElongatedTriangularDimension(width, squHeight, triHeight) = dim
			
			val isOddRow = (math.abs(idxY) % 2 == 1)
			
			val x = width * idxX + (if (isOddRow) {width / 2} else {0})
			val y = idxY * (squHeight + triHeight) + (typ match {
				case ElongatedTriangularType.NorthTri => 0
				case ElongatedTriangularType.Square => triHeight
				case ElongatedTriangularType.SouthTri => triHeight + squHeight
			})
			val height = (typ match {
				case ElongatedTriangularType.NorthTri => triHeight
				case ElongatedTriangularType.Square => squHeight
				case ElongatedTriangularType.SouthTri => triHeight
			})
			
			new Rectangle(x, y, width, height)
		}
		
		override def hit(coords:(Int, Int), dim:ElongatedTriangularDimension):ElongatedTriangularIndex = {
			// Using the 'geometric rectangle-and-two-triangles' approach
			// high-level is described at: http://gdreflections.com/2011/02/hexagonal-grid-math.html
			
			
			// find the box which contains square and top-tri
			val ElongatedTriangularDimension(width, squHeight, triHeight) = dim
			
			val col = divideRoundDown(coords._2, (squHeight + triHeight))
			val row = {
				val adjustedX = (coords._1 - (if (col % 2 == 0) {0} else {width / 2}))
				divideRoundDown(adjustedX, width)
			}
			
			// translate the coordinate to be relative to (row,col)'s bounding box
			val localCoords = {
				val bounds = this.bounds( ElongatedTriangularIndex(row, col, ElongatedTriangularType.NorthTri), dim )
				((coords._1 - bounds.x, coords._2 - bounds.y))
			}
			
			if (localCoords._2 > triHeight) {
				// inside the square
				ElongatedTriangularIndex(row, col, ElongatedTriangularType.Square)
				
			} else if (localCoords._1 < dim.width / 2) {
				// west half of triangle's bounding box
				
				val (x1, y1, x2, y2) = (0, triHeight, dim.width / 2.0, 0)
				def line(x:Double):Double = ((y2 - y1) / (x2 - x1)) * x + y1 - ((y2 - y1) / (x2 - x1)) * x1
				
				// if coord is north of that line, then the point is in the hex northwest of the bounding box's hex, else it is in the bounding box's hex 
				if (line(localCoords._1) >= localCoords._2) {
					// hex northwest of bounding box
					val drow = (if (col % 2 == 0) {-1} else {0})
					ElongatedTriangularIndex(row + drow, col - 1, ElongatedTriangularType.SouthTri)
				} else {
					ElongatedTriangularIndex(row, col, ElongatedTriangularType.NorthTri)
				}
				
			} else {
				// east half of triangle's bounding box
				
				val (x1, y1, x2, y2) = (dim.width, triHeight, dim.width / 2.0, 0)
				def line(x:Double):Double = ((y2 - y1) / (x2 - x1)) * x + y1 - ((y2 - y1) / (x2 - x1)) * x1
				
				// if coord is north of that line, then the point is in the hex northwest of the bounding box's hex, else it is in the bounding box's hex 
				if (line(localCoords._1) >= localCoords._2) {
					// hex northwest of bounding box
					val drow = (if (col % 2 == 0) {0} else {1})
					ElongatedTriangularIndex(row + drow, col - 1, ElongatedTriangularType.SouthTri)
				} else {
					ElongatedTriangularIndex(row, col, ElongatedTriangularType.NorthTri)
				}
			}
		}
	}
	
	/**
	 * Integer division rounds towards zero.
	 * 
	 * The hitbox methods want division to round down.
	 */
	private[this] def divideRoundDown(num:Int, denom:Int):Int = {
		// assume denom is a physical dimension and that physical dimensions are greater than zero
		assert(denom > 0)
		if (num >= 0) {
			num / denom
		} else {
			-1 + (num + 1) / denom
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
		override def getIconFor(
				  f:Tiling[_ <: Any, Index, _]
				, xy:Index
				, rng:scala.util.Random
		):TileLocationIcons[Icon] = TileLocationIcons(
				  Seq(tile())
				, Seq(tile())
		)
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
		
		val toPolygon:Polygon = new Polygon(
			  Array[Int](this.width / 2, this.width, this.width, this.width / 2, 0, 0)
			, Array[Int](0, this.hinset, this.height - this.hinset, this.height, this.height - this.hinset, this.hinset)
			, 6
		)
	}
	
	/**
	 * The dimensions describing elongated triangular tiles
	 * @group Dimension
	 */
	final case class ElongatedTriangularDimension(width:Int, squareHeight:Int, triangleHeight:Int) {
		
		def northTriToPolygon:Polygon = new Polygon(
			Array[Int](0, width / 2, width),
			Array[Int](triangleHeight, 0, triangleHeight),
			3
		)
		def southTriToPolygon:Polygon = new Polygon(
			Array[Int](0, width / 2, width),
			Array[Int](0, triangleHeight, 0),
			3
		)
	}
	
	/**
	 * Functions that specify the screen positions of tiles in a tiling
	 * @group IconLocation
	 */
	trait IconLocation[Index, Dimension] {
		/** A bounding box for the tile at `idx`  */
		def bounds(idx:Index, dim:Dimension):Rectangle
		/** The space which contains the `point` */
		def hit(point:(Int, Int), dim:Dimension):Index
	}
	
	final case class TileLocationIcons[Icon](
			  aboveFrames:AnimationFrames[Icon]
			, belowFrames:AnimationFrames[Icon]
	) {
		assert(aboveFrames.length > 0)
		assert(belowFrames.length > 0)
	}
	
	object TileLocationIcons {
		def apply[Icon](above:Icon, below:Icon):TileLocationIcons[Icon] = this.apply(Seq(above), Seq(below))
	}
	
	
	sealed trait VisualizationRuleBasedTilesheetFailure
	object MalformedUrl extends VisualizationRuleBasedTilesheetFailure
	object FileNotFound extends VisualizationRuleBasedTilesheetFailure
	
	sealed trait VisualizationRuleBuilderFailure extends VisualizationRuleBasedTilesheetFailure
	object ExpectedComplex extends VisualizationRuleBuilderFailure 
	object ExpectedPrimitive extends VisualizationRuleBuilderFailure 
	final case class UnsuccessfulTypeCoercion(value:com.rayrobdod.json.union.JsonValue, toType:String) extends VisualizationRuleBuilderFailure
	final case class IconPartMapKeyNotIntegerConvertable(key:String) extends VisualizationRuleBuilderFailure
	object IconPartWasInconsistent extends VisualizationRuleBuilderFailure
	object SurroundingSpacesMapKeyNotDeltaIndex extends VisualizationRuleBuilderFailure
	
	sealed trait AdjacentSpacesSpecifierFailure extends VisualizationRuleBuilderFailure
	final case class InnerFormatParseFailure(idx:Int, extra:fastparse.core.Parsed.Failure.Extra[Char,String]) extends AdjacentSpacesSpecifierFailure
	final case class UnknownIdentifierFailure(identifiers:Set[String]) extends AdjacentSpacesSpecifierFailure
	
}
