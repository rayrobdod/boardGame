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

import com.rayrobdod.boardGame.{SpaceClassConstructor, TypeOfCost,
		SpaceClass => BoardGameSpaceClass, NoLandOnAction,
		NoPassOverAction, UniformMovementCost}

/**
 * Generic space class for use in testing
 * @author Raymond Dodge
 * @version 18 Jun 2012
 */
case class SpaceClass(typeOfSpace:Int) extends BoardGameSpaceClass
		with NoLandOnAction with NoPassOverAction with UniformMovementCost
	
/**
 * Generic space class for use in testing
 * @author Raymond Dodge
 * @version 18 Jun 2012
 */
object SpaceClass1 extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = a match {
		case SpaceClass(1) => true
		case _ => false
	}
	val apply = new SpaceClass(1)
}

/**
 * Generic space class for use in testing
 * @author Raymond Dodge
 * @version 18 Jun 2012
 */
object SpaceClass2 extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = a match {
		case SpaceClass(2) => true
		case _ => false
	}
	val apply = new SpaceClass(2)
}

/**
 * Generic space class for use in testing
 * @author Raymond Dodge
 * @version 18 Jun 2012
 */
object SpaceClass3 extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = a match {
		case SpaceClass(3) => true
		case _ => false
	}
	val apply = new SpaceClass(3)
}

/**
 * Generic space class for use in testing
 * @author Raymond Dodge
 * @version 18 Jun 2012
 */
object SpaceClass4 extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = a match {
		case SpaceClass(4) => true
		case _ => false
	}
	val apply = new SpaceClass(4)
}

/**
 * Generic space class for use in testing
 * @author Raymond Dodge
 * @version 18 Jun 2012
 */
object SpaceClass5 extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = a match {
		case SpaceClass(5) => true
		case _ => false
	}
	val apply = new SpaceClass(5)
}

/**
 * Generic inverse space class for use in testing
 * @author Raymond Dodge
 * @version 25 Jun 2012
 */
object NotSpaceClass1 extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = a match {
		case SpaceClass(1) => false
		case _ => true
	}
	val apply = new SpaceClass(-1)
}

/**
 * Generic inverse space class for use in testing
 * @author Raymond Dodge
 * @version 25 Jun 2012
 */
object NotSpaceClass2 extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = a match {
		case SpaceClass(2) => false
		case _ => true
	}
	val apply = new SpaceClass(-2)
}

/**
 * Generic inverse space class for use in testing
 * @author Raymond Dodge
 * @version 25 Jun 2012
 */
object NotSpaceClass3 extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = a match {
		case SpaceClass(3) => false
		case _ => true
	}
	val apply = new SpaceClass(-3)
}

/**
 * Generic inverse space class for use in testing
 * @author Raymond Dodge
 * @version 25 Jun 2012
 */
object NotSpaceClass4 extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = a match {
		case SpaceClass(4) => false
		case _ => true
	}
	val apply = new SpaceClass(-4)
}

/**
 * Generic inverse space class for use in testing
 * @author Raymond Dodge
 * @version 25 Jun 2012
 */
object NotSpaceClass5 extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = a match {
		case SpaceClass(5) => false
		case _ => true
	}
	val apply = new SpaceClass(-5)
}

/**
 * Matches all spaces
 * @author Raymond Dodge
 * @version 2013 Feb 23
 */
object AnySpaceClass extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = true
	val apply = new SpaceClass(-6)
}

/**
 * Matches no spaces
 * @author Raymond Dodge
 * @version 2013 Feb 23
 */
object NotAnySpaceClass extends SpaceClassConstructor
{
	def unapply(a:BoardGameSpaceClass) = false
	val apply = new SpaceClass(-7)
}
