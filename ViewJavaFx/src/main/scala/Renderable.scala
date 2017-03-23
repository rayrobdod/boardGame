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
