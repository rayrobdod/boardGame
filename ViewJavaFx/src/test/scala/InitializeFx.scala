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

import java.util.concurrent.CountDownLatch
import org.scalatest.Tag

object FxTest extends Tag("com.rayrobdod.boardGame.javafxView.FxTests")

object InitializeFx {
	
	private[this] var _isSetup:Boolean = false
	
	def setup():Unit = {
		val latch = new CountDownLatch(1);
		javax.swing.SwingUtilities.invokeLater(
			new Runnable() {
				override def run() {
					new javafx.embed.swing.JFXPanel()
					latch.countDown()
				}
			}
		)
		latch.await()
		_isSetup = true
	}
	
	def isSetup:Boolean = _isSetup
}