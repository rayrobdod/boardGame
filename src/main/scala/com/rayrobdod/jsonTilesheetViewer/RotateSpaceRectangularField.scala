/*
	Deduction Tactics
	Copyright (C) 2012-2013  Raymond Dodge

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
import com.rayrobdod.boardGame.RectangularField
import com.rayrobdod.boardGame.StrictRectangularSpaceViaFutures

/**
 * An immutable field that, when asked, produces a new field with a
 * new RectangularSpace in a spot
 * @author Raymond Dodge
 * @version 3.0.0
 */
class RotateSpaceRectangularField(
	val rotation:Seq[SpaceClass],
	val spaceIndexes:Seq[Seq[Int]]
) extends RectangularField[SpaceClass] {
	def this(rotation:Seq[SpaceClass],
				width:Int, height:Int) {
		this(rotation, Seq.fill(height, width){0})
	}
	
	override val spaces = spaceIndexes.zipWithIndex.map({(seq:Seq[Int], i:Int) =>
		seq.zipWithIndex.map({(index:Int, j:Int) =>
			new StrictRectangularSpaceViaFutures[SpaceClass](
				typeOfSpace = rotation(index),
				leftFuture = spaceFuture(i-1,j),
				upFuture = spaceFuture(i,j-1),
				rightFuture = spaceFuture(i+1,j),
				downFuture = spaceFuture(i,j+1)
			)
		}.tupled)
	}.tupled)
	
	
	
	def rotate(x:Int, y:Int):RotateSpaceRectangularField = 
	{
		val newSpaceIndexes = spaceIndexes.updated(y,
			spaceIndexes(y).updated(x,
				((spaceIndexes(y)(x) + 1) % rotation.size)
			)
		)
		
		new RotateSpaceRectangularField(rotation, newSpaceIndexes)
	}
	
	def rotate(i:Int):RotateSpaceRectangularField = 
	{
		val lengths:Seq[Int] = spaceIndexes.map{_.size}
		
		val coords:Either[Int,(Int,Int)] = lengths.zipWithIndex.foldLeft[Either[Int,(Int,Int)]](Left(i))
		{(indexesLeftResultRight:Either[Int,(Int,Int)], (lengthI:(Int, Int))) =>
			{
				val (length, i) = lengthI
				
				indexesLeftResultRight match
				{
					case right:Right[_,_] => right 
					case Left(x:Int) => if (x >= length) {
							Left(x - length)
						} else {
							Right((i, x))
						}
				}
			}
		}
		
		coords match
		{
			case x:Left[_,_] => throw new IndexOutOfBoundsException("param was greater than size of array")
			case Right(Tuple2(i:Int,j:Int)) => this.rotate(j,i)
		}
	}
}
