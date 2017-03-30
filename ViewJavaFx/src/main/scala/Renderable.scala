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
		  tiles:Map[Index, AnimationFrames[Node]]
		, dimension:Dimension
		, framesPerSecond:Short
 )(implicit
		iconLocation:IconLocation[Index, Dimension]
) extends Renderable[Index, Node] {
	
	private[this] val nanosPerSec:Long = 1e9.longValue
	private[this] val nanosPerFrame:Long = nanosPerSec / framesPerSecond
	private[this] val modulus = tiles.values.map{_.length}.foldLeft(1){lcm}
	
	private object AnimationTimer extends javafx.animation.AnimationTimer{
		private var previousNowOpt:Option[Long] = None
		private var remainder:Long = 0
		private var currentFrame:Int = 0
		
		override def stop() {
			super.stop();
			this.previousNowOpt = None
			this.remainder = 0
		}
		
		override def handle(now:Long):Unit = {
			previousNowOpt.map{previousNow =>
				remainder = (now - previousNow) + remainder
				
				val frameIncrement = (remainder / nanosPerFrame).intValue
				remainder = remainder % nanosPerFrame
				val previousFrame = currentFrame
				currentFrame = (currentFrame + frameIncrement) % modulus
				
				tiles.values.foreach{tileSeq =>
					val hideTile = previousFrame % tileSeq.length
					val showTile = currentFrame % tileSeq.length
					
					tileSeq(hideTile).setVisible(false)
					tileSeq(showTile).setVisible(true)
				}
			}
			previousNowOpt = Option(now)
		}
	}
	
	// if only one frame, then there is nothing to animate
	if (modulus > 2) {
		AnimationTimer.start()
	}
	
	val component:Node = {
		tiles.foreach{idxIcnseq =>
			val (index, iconSeq) = idxIcnseq
			val bounds = iconLocation.bounds(index, dimension)
			
			iconSeq.tail.foreach{icon =>
				// animate by setting one of the icons to visible and the rest to not visible
				// so, for the first frame, set everything but the head to not visible
				icon.setVisible(false)
			}
			iconSeq.foreach{icon =>
				icon.relocate(bounds.x, bounds.y)
			}
		}
		new javafx.scene.Group(tiles.values.to[Seq].flatten:_*)
	}
	
	def addOnClickHandler(idx:Index, f:Function0[Unit]):Unit = {
		tiles(idx).foreach{
			_.setOnMouseClicked(new EventHandler[MouseEvent] {
				override def handle(e:MouseEvent):Unit = { f.apply() }
			})
		}
	}
	
}
