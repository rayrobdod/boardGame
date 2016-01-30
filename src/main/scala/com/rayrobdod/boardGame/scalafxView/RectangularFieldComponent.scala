/*
	Deduction Tactics
	Copyright (C) 2012-2015  Raymond Dodge

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
import javafx.scene.layout.GridPane
import scala.collection.immutable.Map
import scala.util.Random
import com.rayrobdod.boardGame.RectangularField
import com.rayrobdod.boardGame.RectangularFieldIndex

object RectangularFieldComponent {
	def apply[A](
			field:RectangularField[A],
			tilesheet:RectangularTilesheet[A],
			rng:Random = Random
	):(GridPane, GridPane) = {
		
		val a:Map[(Int, Int), (Node, Node)] = field.map{x => ((x._1, tilesheet.getImageFor(field, x._1._1, x._1._2, rng) )) }
		val top = a.mapValues{_._1}
		val bot = a.mapValues{_._2}
		
		val topComp = new GridPane()
		top.foreach{case ((x,y), node) =>
			topComp.add(node, x, y)
			GridPane.setHgrow(node, javafx.scene.layout.Priority.ALWAYS)
			GridPane.setVgrow(node, javafx.scene.layout.Priority.ALWAYS)
			GridPane.setHalignment(node, javafx.geometry.HPos.CENTER)
			GridPane.setValignment(node, javafx.geometry.VPos.CENTER)
		}
		val botComp = new GridPane()
		bot.foreach{case ((x,y), node) =>
			botComp.add(node, x, y)
			GridPane.setHgrow(node, javafx.scene.layout.Priority.ALWAYS)
			GridPane.setVgrow(node, javafx.scene.layout.Priority.ALWAYS)
			GridPane.setHalignment(node, javafx.geometry.HPos.CENTER)
			GridPane.setValignment(node, javafx.geometry.VPos.CENTER)
		}
		
		((topComp, botComp))
	}
}
