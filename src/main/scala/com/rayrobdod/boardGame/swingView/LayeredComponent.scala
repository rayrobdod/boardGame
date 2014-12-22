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

import javax.swing.JComponent
import java.awt.{Component, Graphics}
import java.awt.event.{MouseListener, MouseAdapter, MouseEvent}
import scala.collection.mutable.Buffer

final class LayeredComponent extends JComponent {
	private val layers = Buffer.empty[Layer]
	
	def addLayer(x:Layer) = {layers += x; this.repaint()}
	def removeLayer(x:Layer) = {layers -= x; this.repaint()}
	def removeAllLayers() = {layers.clear; this.repaint()}
	
	var offsetX:Int = 0
	var offsetY:Int = 0
	
	
	this.setLayout(null)
	override def getPreferredSize = {
		if (this.isPreferredSizeSet) {
			super.getPreferredSize
		} else {
			new java.awt.Dimension(
				offsetX + (0 +: layers.map{_.getWest}).max,
				offsetY + (0 +: layers.map{_.getSouth}).max
			)
		}
	}
	
	protected override def paintComponent(g:Graphics) {
		layers.foreach{(l:Layer) =>
			l.paintLayer(this, g, offsetX, offsetY)
		}
	}
	
	this.addMouseListener(new MouseAdapter() {
		override def mouseClicked(e:MouseEvent) = {
			val translatedE = new MouseEvent(
				e.getSource.asInstanceOf[Component],
				e.getID,
				e.getWhen,
				e.getModifiers,
				e.getX - offsetX,
				e.getY - offsetY,
				e.getXOnScreen,
				e.getYOnScreen,
				e.getClickCount,
				e.isPopupTrigger,
				e.getButton
			)
				
			layers.foreach{(l:Layer) => 
				l.clicked(translatedE)
			}
		}
	})
}
