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


/*
 * Types of costs that I can concieve of on short notice
 */
 
/**
 * superclass of types of costs. These are used so that
 * a space can allow different types of actions through at
 * different rates.
 * @author Raymond Dodge
 * @version 05 Apr 2012
 */
trait TypeOfCost

/**
 * The cost of a token moving
 * @author Raymond Dodge
 * @version 01 Apr 2012
 * @version 05 Apr 2012 - extends TypeOfCost now
 */
case object TokenMovementCost extends TypeOfCost

/**
 * The cost of performing a physical attack across an area
 * @author Raymond Dodge
 * @version 01 Apr 2012
 * @version 05 Apr 2012 - extends TypeOfCost now
 */
case object PhysicalStrikeCost extends TypeOfCost

/**
 * The cost of performing a magical attack across an area
 * @author Raymond Dodge
 * @version 01 Apr 2012
 * @version 05 Apr 2012 - extends TypeOfCost now
 */
case object MagicalStrikeCost extends TypeOfCost

/**
 * The cost of sound moving through an area
 * @author Raymond Dodge
 * @version 01 Apr 2012
 * @version 05 Apr 2012 - extends TypeOfCost now
 */
case object SoundPenetrationCost extends TypeOfCost

/**
 * The cost of water moving through an area
 * @author Raymond Dodge
 * @version 01 Apr 2012
 * @version 05 Apr 2012 - extends TypeOfCost now
 */
case object LiquidPenetrationCost extends TypeOfCost
