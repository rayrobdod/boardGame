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

import com.rayrobdod.boardGame.RectangularField
import com.rayrobdod.boardGame.RectangularFieldIndex


object RotateRectangularField{
	def apply(rotation:Seq[SpaceClass],
			currentField:RectangularField[SpaceClass],
			spaceToRotate:RectangularFieldIndex):RectangularField[SpaceClass] =
	{
		val currentClasses = currentField.map{x => ((x._1, x._2.typeOfSpace))}
		val currentClassRotate = currentClasses(spaceToRotate)
		val nextClassRotate = rotation( (rotation.indexOf(currentClassRotate) + 1) % rotation.size )
		val nextClasses = currentClasses + ((spaceToRotate, nextClassRotate))
		val nextField = RectangularField.apply(nextClasses)
		
		return nextField
	}
}
