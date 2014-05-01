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
package com.rayrobdod.boardGame.swingView

import java.awt.Shape
import java.awt.event.MouseListener
import javax.swing.JPanel
import com.rayrobdod.boardGame.Space

/**
 * @since 3.0.0
 */
trait FieldViewer[A] {
	
	/**
	 * A canvas that a process can use in whatever way they want.
	 * Will usually be used to show the Tokens moving around the field
	 */
	val tokenLayer:JPanel
	
	/**
	 * Tells the location of a particular space. So a component
	 * can say "I want to be on top of this space" and it can move
	 * to the given rectangle to do so.
	 */
	def spaceLocation(space:Space[A]):Shape
	
	/**
	 * 
	 */
	def addMouseListenerToSpace(space:Space[A], l:MouseListener):Any
	
	/**
	 * Do something such that the specified space is visible.
	 * 
	 * Whether that's switching maps, or moving a containing ScrollPane.
	 */
	def showSpace(space:Space[A]):Any
}
