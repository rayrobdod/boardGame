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
package com.rayrobdod.boardGame.view

import java.awt.{Component, Graphics}
import java.awt.event.{MouseListener, MouseEvent}
import javax.swing.Icon
import com.rayrobdod.swing.SolidColorIcon
import com.rayrobdod.boardGame.view.Swing._

import org.scalatest.FunSpec

class SwingRectangularTilemapComponentTest extends FunSpec {
	val icon32 = new SolidColorIcon(java.awt.Color.cyan, 32, 32)
	val icon1632 = new SolidColorIcon(java.awt.Color.cyan, 16, 32)
	
	def tiles(x:Range, y:Range, i:Icon):Map[(Int, Int), Icon] = {
		x.map{a => y.map{b => (( ((a,b)), i ))}}.flatten.toMap
	}
	
	
	
	describe ("Bounds checking") {
	describe ("SwingRectangularTilemapComponent with icon32 and map(0,0)to(3,3)") {
		val uut = new SwingRectangularTilemapComponent(tiles(0 to 3, 0 to 3, icon32))
		
		it ("getWest is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.width}
		}
		it ("getSouth is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.height}
		}
	}
	describe ("SwingRectangularTilemapComponent with icon32 and map(-2,-2)to(1,1)") {
		val uut = new SwingRectangularTilemapComponent(tiles(-2 to 1, -2 to 1, icon32))
		
		it ("getWest is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.width}
		}
		it ("getSouth is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.height}
		}
	}
	describe ("SwingRectangularTilemapComponent with icon32 and map(0,2)to(0,5)") {
		val uut = new SwingRectangularTilemapComponent(tiles(0 to 0, 2 to 5, icon32))
		
		it ("getWest is (32 * 1)"){
			assertResult(1 * 32){uut.getMaximumSize.width}
		}
		it ("getSouth is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.height}
		}
	}
	describe ("SwingRectangularTilemapComponent with icon1632 and map(-2,-2)to(1,1)") {
		val uut = new SwingRectangularTilemapComponent(tiles(-2 to 1, -2 to 1, icon1632))
		
		it ("getWest is (16 * 4)"){
			assertResult(4 * 16){uut.getMaximumSize.width}
		}
		it ("getSouth is (32 * 4)"){
			assertResult(4 * 32){uut.getMaximumSize.height}
		}
	}
	}
	describe ("mouse events") {
	describe ("with icon size 32x32") {
		class afsd extends SwingRectangularTilemapComponent(tiles(0 to 3, 0 to 3, icon32)) {
			override def processMouseEvent(e:MouseEvent):Unit = super.processMouseEvent(e)
		}
		
		it ("click at 0,0 is inside tile 0,0") {
			val uut = new afsd()
			val mock = new MockMouseListener()
			uut.addMouseListener(((0,0)), mock)
			uut.processMouseEvent(new MouseEvent(uut, MouseEvent.MOUSE_CLICKED, -1, -1, 0, 0, 1, false))
			assertResult(1){mock.mouseClickCount}
		}
		it ("click at 0,0 is not inside tile 1,0") {
			val uut = new afsd()
			val mock = new MockMouseListener()
			uut.addMouseListener(((1,0)), mock)
			uut.processMouseEvent(new MouseEvent(uut, MouseEvent.MOUSE_CLICKED, -1, -1, 0, 0, 1, false))
			assertResult(0){mock.mouseClickCount}
		}
		it ("click at 31,31 is inside tile 0,0") {
			val uut = new afsd()
			val mock = new MockMouseListener()
			uut.addMouseListener(((0,0)), mock)
			uut.processMouseEvent(new MouseEvent(uut, MouseEvent.MOUSE_CLICKED, -1, -1, 31, 31, 1, false))
			assertResult(1){mock.mouseClickCount}
		}
		it ("click at 32,32 is inside tile 1,1") {
			val uut = new afsd()
			val mock = new MockMouseListener()
			uut.addMouseListener(((1,1)), mock)
			uut.processMouseEvent(new MouseEvent(uut, MouseEvent.MOUSE_CLICKED, -1, -1, 32, 32, 1, false))
			assertResult(1){mock.mouseClickCount}
		}
		it ("click at 33,33 is inside tile 1,1") {
			val uut = new afsd()
			val mock = new MockMouseListener()
			uut.addMouseListener(((1,1)), mock)
			uut.processMouseEvent(new MouseEvent(uut, MouseEvent.MOUSE_CLICKED, -1, -1, 33, 33, 1, false))
			assertResult(1){mock.mouseClickCount}
		}
	}
	}
	
	
	
	
	class MockPaintIcon(width:Int, height:Int, expectedX:Int, expectedY:Int) extends javax.swing.Icon {
		def getIconWidth = width
		def getIconHeight = height
		def paintIcon(c:Component, g:Graphics, x:Int, y:Int) = {
			assertResult(expectedX){x}
			assertResult(expectedY){y}
		}
	}
	class MockMouseListener extends MouseListener {
		var mouseClickCount = 0
		def mouseClicked(e:MouseEvent):Unit = {
			mouseClickCount = mouseClickCount + 1
		}
		var mouseEnteredCount = 0
		def mouseEntered(e:MouseEvent):Unit = {
			mouseEnteredCount = mouseEnteredCount + 1
		}
		var mouseExitedCount = 0
		def mouseExited(e:MouseEvent):Unit = {
			mouseExitedCount = mouseExitedCount + 1
		}
		var mousePressedCount = 0
		def mousePressed(e:MouseEvent):Unit = {
			mousePressedCount = mousePressedCount + 1
		}
		var mouseReleasedCount = 0
		def mouseReleased(e:MouseEvent):Unit = {
			mouseReleasedCount = mouseReleasedCount + 1
		}
	}
}
