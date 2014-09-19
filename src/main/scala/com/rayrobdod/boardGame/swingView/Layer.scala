/*
	Deduction Tactics
	Copyright (C) 2014-2014  Raymond Dodge

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
package com.rayrobdod.boardGame.swingView

import java.awt.{Component, Graphics}
import java.awt.event.{MouseListener, MouseAdapter, MouseEvent}

trait Layer {
	/**
	 * How far left of the origin does this layer go?
	 */
	def getEast:Int
	/**
	 * How far up from the origin does this layer go?
	 */
	def getNorth:Int
	/**
	 * How far right of the origin does this layer go?
	 */
	def getWest:Int
	/**
	 * How far down from the origin does this layer go?
	 */
	def getSouth:Int
	
	
	
	/**
	 * Paints this layer
	 */
	def paintLayer(c:Component, g:Graphics, x:Int, y:Int)
	
	
	
	/**
	 * Performs an action in resopnse to a mouse click.
	 */
	def clicked(e:MouseEvent)
}
