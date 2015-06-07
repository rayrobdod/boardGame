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
import javax.swing.Icon
import scala.collection.immutable.Map
import scala.util.Random
import com.rayrobdod.boardGame.RectangularFieldIndex

/**
 * A [[Layer]] which consists of a 2d sequence of icons.
 */
final class RectangularTilemapLayer(
		tiles:Map[RectangularFieldIndex, Icon]
) extends Layer {
	
	private val mapX = tiles.map{_._1._1}.min
	private val mapY = tiles.map{_._1._2}.min
	private val mapWidth   = tiles.map{_._1._1}.max - tiles.map{_._1._1}.min + 1
	private val mapHeight  = tiles.map{_._1._2}.max - tiles.map{_._1._2}.min + 1
	private val tileWidth  = tiles.map{_._2.getIconWidth}.max
	private val tileHeight = tiles.map{_._2.getIconHeight}.max
	
	
	override def getEast:Int  = tileWidth * -mapX
	override def getNorth:Int = tileHeight * -mapY
	override def getWest:Int  = tileWidth * mapWidth - getEast
	override def getSouth:Int = tileHeight * mapHeight - getNorth
	
	
	override def paintLayer(c:Component, g:Graphics, offsetX:Int, offsetY:Int):Unit = {
		// tiles should not overlap
		tiles.foreach({(index:RectangularFieldIndex, icon:Icon) =>
			val iconX = index._1 * tileWidth
			val iconY = index._2 * tileHeight
			
			icon.paintIcon(c, g, iconX + offsetX, iconY + offsetY)
		}.tupled)
	}
	
	
	def spaceBounds(index:RectangularFieldIndex):java.awt.Shape = {
		val iconX = index._1 * tileWidth
		val iconY = index._2 * tileHeight
		
		new java.awt.Rectangle(iconX, iconY, tileWidth, tileHeight)
	}
	
	
	
	private var mouseListeners:Map[RectangularFieldIndex, Seq[MouseListener]] = Map.empty.withDefaultValue(Nil)
	def addMouseListener(index:RectangularFieldIndex, ml:MouseListener) {
		mouseListeners = mouseListeners + ((index, mouseListeners(index) :+ ml))
	}
	override def clicked(e:MouseEvent) {
		// tiles should not overlap
		mouseListeners.foreach({(index:RectangularFieldIndex, ml:Seq[MouseListener]) =>
			val iconX = index._1 * tileWidth
			val iconY = index._2 * tileHeight
			
			val translatedE = new MouseEvent(
				e.getSource.asInstanceOf[Component],
				e.getID,
				e.getWhen,
				e.getModifiers,
				e.getX - iconX,
				e.getY - iconY,
				e.getXOnScreen,
				e.getYOnScreen,
				e.getClickCount,
				e.isPopupTrigger,
				e.getButton
			)
			
			if (0 <= translatedE.getX && translatedE.getX < tileWidth &&
					0 <= translatedE.getY && translatedE.getY < tileHeight) {
				ml.foreach{_.mouseClicked(translatedE)}
			}
		}.tupled)
	}
}
