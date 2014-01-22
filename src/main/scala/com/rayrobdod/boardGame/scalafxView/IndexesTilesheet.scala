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
package com.rayrobdod.boardGame.scalafxView

import javafx.scene.paint.Color
import scalafx.scene.image.{PixelReader, WritableImage, Image}
import scala.util.Random
import com.rayrobdod.boardGame.RectangularField

/**
 * A tilesheet that prints indexies on a tile
 * 
 * @author Raymond Dodge
 */
object IndexesTilesheet extends RectangularTilesheet {
	override def name = "IndexesTilesheet"
	case class Dimension(val width:Int, val height:Int)
	val dim = new Dimension(32,32)
	
	val lightIcon = new WritableImage(
		new SolidColorPixelReader(Color.MAGENTA), dim.width, dim.height
	)
	val darkIcon  = new WritableImage(
		new SolidColorPixelReader(Color.CYAN), dim.width, dim.height
	)
	
	def getImageFor(f:RectangularField, x:Int, y:Int, rng:Random) = {
		((
			if ((x+y)%2 == 0) {lightIcon} else {darkIcon},
			IndexImage(x,y)
		))
	}
	
	def IndexImage(xIndex:Int, yIndex:Int):Image = {
		import com.rayrobdod.boardGame.swingView.IndexesTilesheet.IndexIcon
		import java.awt.image.BufferedImage
		val swingIcon = new IndexIcon(xIndex, yIndex)
		val swingImage = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB)
		val swingGraphics = swingImage.createGraphics()
		swingIcon.paintIcon(null, swingGraphics, 0, 0)
		
		import javafx.embed.swing.SwingFXUtils
		val fxImage = SwingFXUtils.toFXImage(swingImage, null)
		
		return new Image(fxImage)
	}
}



import javafx.scene.image.{PixelReader => JavaPixelReader,
		WritablePixelFormat, PixelFormat}

class SolidColorPixelReader(c:Color) extends PixelReader {
	
	override def delegate = MyDelegate;
	
	object MyDelegate extends JavaPixelReader {
 
		private val argb = {
			((c.getOpacity() * 256).toInt << 24) +
			((c.getRed()     * 256).toInt << 16) +
			((c.getGreen()   * 256).toInt <<  8) +
			((c.getBlue()    * 256).toInt <<  0)
		}
		
		def getArgb(x:Int, y:Int) = argb
		def getColor(x:Int, y:Int) = c
		def getPixelFormat() = PixelFormat.getIntArgbInstance()
		def getPixels[T <: java.nio.Buffer](
				x:Int, y:Int, w:Int, h:Int,
				pixelformat:WritablePixelFormat[T],
				buffer:T, scanlineStride:Int) {
			(x to (x + w)).foreach{(i:Int) =>
			(y to (y + h)).foreach{(j:Int) =>
				pixelformat.setArgb(buffer, i, j, scanlineStride, argb) 
			}}
		}
		
		def getPixels(
				x:Int, y:Int, w:Int, h:Int,
				pixelformat:WritablePixelFormat[java.nio.IntBuffer],
				buffer:Array[Int], offset:Int, scanlineStride:Int) {
			val buffer2 = java.nio.IntBuffer.wrap(buffer, offset, buffer.length - offset)
			this.getPixels(x,y,w,h,pixelformat,buffer2,scanlineStride)
		}
		def getPixels(
				x:Int, y:Int, w:Int, h:Int,
				pixelformat:WritablePixelFormat[java.nio.ByteBuffer],
				buffer:Array[Byte], offset:Int, scanlineStride:Int) {
			val buffer2 = java.nio.ByteBuffer.wrap(buffer, offset, buffer.length - offset)
			this.getPixels(x,y,w,h,pixelformat,buffer2,scanlineStride)
		}
	}
}
