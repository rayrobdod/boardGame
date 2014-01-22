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

import scala.util.Random
import com.rayrobdod.boardGame.{RectangularField, RectangularSpace,
		SpaceClassConstructor, Space
}
import scalafx.scene.layout.{Pane, StackPane, GridPane}
import scalafx.scene.image.{Image, ImageView}



class RectangularFieldComponent(
		tilesheet:RectangularTilesheet,
		field:RectangularField,
		rng:Random = Random
) extends StackPane {
	case class Point(val x:Int, val y:Int)
	
	private val flatPoints:Seq[Point] = {
		field.spaces.indices.map{(y) =>
			field.spaces(y).indices.map{(x) =>
				new Point(x,y)
			}
		}.flatten
	}
	private val spaces:Seq[RectangularSpace] = {
		flatPoints.map{(p:Point) =>
			field.space(x = p.x, y = p.y)
		}
	}
	private val (lowIcons:Seq[Image], highIcons:Seq[Image]) = {
		flatPoints.map{(p:Point) =>
			tilesheet.getImageFor(field, p.x, p.y, rng)
		}.unzip
	}
	
	private val lowLayer  = new FieldComponentLayer(flatPoints.zip(lowIcons))
	val tokenLayer:Pane   = new Pane(null)
	private val highLayer = new FieldComponentLayer(flatPoints.zip(highIcons))
	
	content = Seq(lowLayer, tokenLayer, highLayer)
	
	
	
	private class FieldComponentLayer(icons:Seq[(Point, Image)])
				extends GridPane
	{
		val labels = icons.map{(a) => ((a._1, new ImageView(a._2)))}
		labels.foreach({(p:Point, i:ImageView) =>
			FieldComponentLayer.this.add(i, p.x, p.y)
		}.tupled)
	}
	
}
