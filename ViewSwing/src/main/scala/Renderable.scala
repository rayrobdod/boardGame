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
package com.rayrobdod.boardGame.view

import java.awt.Graphics
import java.awt.event.{MouseListener, MouseEvent}
import javax.swing.{JComponent, Icon}
import scala.collection.immutable.Map

private[view] final class SwingRenderable[Index, Dimension](
		  tiles:Map[Index, Icon]
		, dimension:Dimension
 )(implicit
		iconLocation:IconLocation[Index, Dimension]
) extends Renderable[Index, JComponent] {
	
	private[this] val bounds:java.awt.Rectangle = {
		val retVal = new java.awt.Rectangle(0,0,0,0)
		tiles.keySet.foreach{idx =>
			retVal.add(iconLocation.bounds(idx, dimension))
		}
		retVal
	}
	
	val component:JComponent = new JComponent{
		override def paintComponent(g:Graphics):Unit = {
			// tiles should not overlap
			tiles.foreach({(index:Index, icon:Icon) =>
				val tileBounds = iconLocation.bounds(index, dimension)
				icon.paintIcon(this, g, tileBounds.x, tileBounds.y)
			}.tupled)
		}
		
		override def getMaximumSize():java.awt.Dimension = {
			if (this.isMaximumSizeSet) {
				super.getMaximumSize()
			} else {
				SwingRenderable.this.bounds.getSize
			}
		}
		override def getPreferredSize():java.awt.Dimension = {
			if (this.isPreferredSizeSet) {
				super.getPreferredSize()
			} else {
				this.getMaximumSize()
			}
		}
	}
	
	def addOnClickHandler(idx:Index, f:Function0[Unit]):Unit = {
		component.addMouseListener(new MouseListener() {
			def mouseClicked(e:MouseEvent):Unit = {
				if (iconLocation.hit((e.getX, e.getY), dimension) == idx) {f()}
			}
			def mouseEntered(e:MouseEvent):Unit = {
			}
			def mouseExited(e:MouseEvent):Unit = {
			}
			def mousePressed(e:MouseEvent):Unit = {
			}
			def mouseReleased(e:MouseEvent):Unit = {
			}
		})
	}
	
}
