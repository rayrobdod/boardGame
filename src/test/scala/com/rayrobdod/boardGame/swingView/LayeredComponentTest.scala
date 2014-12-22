/*
	Deduction Tactics
	Copyright (C) 2014  Raymond Dodge

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

import java.awt.{Component, Graphics, Dimension}
import javax.swing.Icon
import com.rayrobdod.swing.SolidColorIcon

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks

class LayeredComponentTest extends FunSpec {
	
	describe ("LayeredComponent with no layers") {
		it ("reports an intrinsic preferred size of (0,0)") {
			assertResult(new Dimension(0,0)){
				(new LayeredComponent).getPreferredSize
			}
		}
	}
	
}
