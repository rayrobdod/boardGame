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
 */
package object javafxView {
	type IndexConverter = Function1[(Int, Int), (Int, Int)]
	
	def lcm(x:Int, y:Int):Int = swingView.lcm(x,y)
	
	def gcd(x:Int, y:Int):Int = swingView.gcd(x,y)
}

package javafxView {
	final case class Dimension(val width:Int, val height:Int)
	
	object NilTilesheet extends RectangularTilesheet[Any] {
		import javafx.scene.Node
		import javafx.scene.paint.Color
		import javafx.scene.shape.Rectangle

		override val name:String = "Nil"
		override def getImageFor(f:RectangularField[_ <: Any], x:Int, y:Int, rng:scala.util.Random):(Node,Node) = getImageFor
		
		private def getImageFor = ((blankIcon, blankIcon))
		private def blankIcon = new Rectangle(16,16,Color.TRANSPARENT)
	}
	
	trait SpaceClassMatcherFactory[-SpaceClass] {
		def apply(reference:String):SpaceClassMatcher[SpaceClass]
	}
	
	/** A SpaceClassMatcherFactory that always returns a SpaceClassMatcher that always retuns true */
	object ConstTrueSpaceClassMatcherFactory extends SpaceClassMatcherFactory[Any] {
		def apply(s:String):SpaceClassMatcher[Any] = ConstTrueSpaceClassMatcher
	}
	
	/** A SpaceClassMatcherFactory that always returns a SpaceClassMatcher that always retuns false */
	object ConstFalseSpaceClassMatcherFactory extends SpaceClassMatcherFactory[Any] {
		def apply(s:String):SpaceClassMatcher[Any] = ConstFalseSpaceClassMatcher
	}
}
