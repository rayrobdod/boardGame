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
package com.rayrobdod.boardGame.javafxView

import javafx.scene.Node
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.image.{PixelReader, WritableImage, Image}
import javafx.scene.paint.Color
import javafx.scene.text.Text
import scala.util.Random
import com.rayrobdod.boardGame.RectangularField

/**
 * A tilesheet that prints indexies on a tile
 * 
 * @author Raymond Dodge
 * @version next
 */
object IndexesTilesheet extends RectangularTilesheet[Any] {
	override def name:String = "IndexesTilesheet"
	private val dim = new Dimension(48,24)
	
	private def lightIcon = {
		val retVal = new javafx.scene.shape.Rectangle
		retVal.setWidth(dim.width)
		retVal.setHeight(dim.height)
		retVal.setFill(Color.MAGENTA)
		retVal
	}
	private def darkIcon  = {
		val retVal = new javafx.scene.shape.Rectangle
		retVal.setWidth(dim.width)
		retVal.setHeight(dim.height)
		retVal.setFill(Color.CYAN)
		retVal
	}
	
	override def getIconFor(f:RectangularField[_], x:Int, y:Int, rng:Random):(Node, Node) = {
		((
			if ((x+y)%2 == 0) {lightIcon} else {darkIcon},
			IndexImage(x,y)
		))
	}
	
	private def IndexImage(xIndex:Int, yIndex:Int):Node = {
		new Text("(" + xIndex + "," + yIndex + ")")
	}
}
