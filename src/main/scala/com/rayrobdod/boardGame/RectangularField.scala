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
package com.rayrobdod.boardGame

import scala.collection.immutable.Seq
import java.util.concurrent.{Future => JavaFuture, TimeUnit, TimeoutException}

/**
 * A group of spaces such that they form a rectangular board made of
 * [[com.rayrobdod.boardGame.RectangularSpace]]s, such that they follow
 * ecludian geometery.
 * 
 * 
 * @author Raymond Dodge
 * @version 3.0.0
 * @see [[com.rayrobdod.boardGame.RectangularSpace]]
 */
abstract class RectangularField[A]
{
	/**
	 * y is outer layer - x is inner layer
	 * @deprecated
	 */
	def spaces:Seq[Seq[StrictRectangularSpace[A]]]
	
	/** retrives a space from the spaces array. */
	final def space(x:Int, y:Int) = spaces(y)(x)
	final def containsIndexies(x:Int, y:Int) = spaces.isDefinedAt(y) && spaces(y).isDefinedAt(x)
	
	/** 
	 * Creates a future (both {@link java.util.concurrent.Future} and {@link scala.parallel.Future}) that, when called, will
	 * return the same value as {@code space(x,y)}
	 */
	final protected def spaceFuture(x:Int, y:Int) =
	{
		final class RectangularFieldSpaceFuture(x:Int, y:Int)
				extends scala.Function0[Option[StrictRectangularSpace[A]]]
		{
			override def apply = {
				while (!isDone) {Thread.sleep(100L)}
				
				if (RectangularField.this.containsIndexies(x,y))
					Some(RectangularField.this.space(x,y))
				else
					None
			}
			
			// in theory, this should never be called in a state when isDone is false.
			def isDone:Boolean = {spaces != null}
		}
		
		new RectangularFieldSpaceFuture(x,y)
	}
}

/**
 */
object RectangularField
{
	def apply[A](classes:Seq[Seq[A]]):RectangularField[A] = {
		new RectangularField[A] {
			
			val spaces = classes.zipWithIndex.map({(classSeq:Seq[A], j:Int) => 
				classSeq.zipWithIndex.map({(clazz:A, i:Int) => 
					new StrictRectangularSpaceViaFutures(
							typeOfSpace = clazz,
							leftFuture  = spaceFuture(i-1,j),
							upFuture    = spaceFuture(i,j-1),
							rightFuture = spaceFuture(i+1,j),
							downFuture  = spaceFuture(i,j+1)
					)
				}.tupled)
			}.tupled)
		}
	}
}
