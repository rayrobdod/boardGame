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

import javafx.scene.Node
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import scala.collection.immutable.{Seq, Map}

private[view] final class JavaFxRenderable[Index, Dimension](
		  tiles:Map[Index, Node]
		, dimension:Dimension
 )(implicit
		iconLocation:IconLocation[Index, Dimension]
) extends Renderable[Index, Node] {
	
	val component:Node = {
		tiles.foreach{idxIcn:(Index, Node) =>
			val (index, icon) = idxIcn
			val bounds = iconLocation.bounds(index, dimension)
			
			icon.relocate(bounds.x, bounds.y)
		}
		new javafx.scene.Group(tiles.values.to[Seq]:_*)
	}
	
	def addOnClickHandler(idx:Index, f:Function0[Unit]):Unit = {
		tiles(idx).setOnMouseClicked(new EventHandler[MouseEvent] {
			override def handle(e:MouseEvent):Unit = { f.apply() }
		})
	}
	
}
