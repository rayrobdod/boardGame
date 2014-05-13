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
package com.rayrobdod.boardGame.swingView

import java.awt.{Component, Point}
import javax.swing.{JLabel, Icon, SwingWorker}
import com.rayrobdod.boardGame.{Token, Space, RectangularSpace}

/**
 * A component that can react to actions that a token may take
 * 
 * @author Raymond Dodge
 * @since 04 Aug 2011
 * @version 3.0.0
 * @tparam A the type of spaceclass used by this class
 */
class TokenComponent[A](final var fieldComp:FieldViewer[A]) extends JLabel {
	
	final var movementSpeed:Int = 15
	
	
	final def moveToSpace(space:Space[A]) = {
		
		val endSpaceBounds = fieldComp.spaceLocation(space).getBounds
		val endLocation = new Point(
				(endSpaceBounds.getX + endSpaceBounds.getWidth  / 2 - this.getWidth  / 2).intValue,
				(endSpaceBounds.getY + endSpaceBounds.getHeight / 2 - this.getHeight / 2).intValue
		)
		val startLocation = this.getLocation()
		
		
		val a = new TokenComponent.TokenMover(this, startLocation, endLocation, movementSpeed * 100)
		a.execute()
	}
	
	
	
	
	
}

/**
 * @todo This seems like something for the com.rayrobdod.animation package
 * @since 3.0.0
 */
object TokenComponent {
	private val sleepTime = 100
	
	/** @param percent 0 <= percent <= 1 */
	def pointAlongLine(start:Point, end:Point, percent:Float):Point = {
		new Point(
			(start.getX + (end.getX - start.getX) * percent).intValue,
			(start.getY + (end.getY - start.getY) * percent).intValue
		)
	}
	
	final class TokenMover(comp:Component, start:Point, end:Point, totalTime:Int) extends SwingWorker[Point, Point] {
		override def doInBackground():Point = {
			val startTime = System.currentTimeMillis
			
			while ((!this.isCancelled) && (startTime + totalTime < System.currentTimeMillis)) {
				val timeSoFar:Float = System.currentTimeMillis - startTime 
				val percent:Float = timeSoFar / totalTime
				this.publish(pointAlongLine(start, end, percent))
				
				Thread.sleep(sleepTime)
			}
			end
		}
		
		override def process(chunks:java.util.List[Point]) {
			comp.setLocation(chunks.get(chunks.size() - 1))
		}
		
		override def done() {
			comp.setLocation(end)
		}
	}
}
