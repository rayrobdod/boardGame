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
				val bounds = iconLocation.bounds(index, dimension)
				icon.paintIcon(this, g, bounds.x, bounds.y)
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
