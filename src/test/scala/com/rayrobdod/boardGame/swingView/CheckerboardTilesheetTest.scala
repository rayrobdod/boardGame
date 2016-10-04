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
package com.rayrobdod.boardGame.swingView

import org.scalatest.FunSpec
import java.awt.{Dimension, Color}
import javax.swing.Icon;
import com.rayrobdod.swing.SolidColorIcon

class CheckerboardTilesheetTest extends FunSpec {
	describe ("Default CheckerboardTilesheet") {
		it ("light color is white") {
			assertResult(Color.white){CheckerboardTilesheet().light}
		}
		it ("dark color is black") {
			assertResult(Color.black){CheckerboardTilesheet().dark}
		}
		it ("dim is (16,16)") {
			assertResult(new Dimension(16,16)){CheckerboardTilesheet().dim}
		}
		it ("name is 'Checkerboard: ...'") {
			assertResult("Checkerboard: java.awt.Color[r=255,g=255,b=255]/java.awt.Color[r=0,g=0,b=0]"){CheckerboardTilesheet().name}
		}
		it ("toString is 'Checkerboard: ...'") {
			assertResult("Checkerboard: java.awt.Color[r=255,g=255,b=255]/java.awt.Color[r=0,g=0,b=0], java.awt.Dimension[width=16,height=16]"){CheckerboardTilesheet().toString}
		}
		it ("lightIcon is SolidColorIcon(Color.white, 16, 16)") {
			compareIcons(new SolidColorIcon(Color.white, 16, 16)){CheckerboardTilesheet().lightIcon}
		}
		it ("darkIcon is SolidColorIcon(Color.black, 16, 16)") {
			compareIcons(new SolidColorIcon(Color.black, 16, 16)){CheckerboardTilesheet().darkIcon}
		}
		it ("transparentIcon is SolidColorIcon(Color(0, true), 16, 16)") {
			compareIcons(new SolidColorIcon(new Color(0,true), 16, 16)){CheckerboardTilesheet().transparentIcon}
		}
		it ("getIconFor(null, 0, 0, null)._1 is lightIcon") {
			compareIcons(CheckerboardTilesheet().lightIcon){CheckerboardTilesheet().getIconFor(null, 0, 0, null)._1}
		}
		it ("getIconFor(null, 0, 1, null)._1 is darkIcon") {
			compareIcons(CheckerboardTilesheet().darkIcon){CheckerboardTilesheet().getIconFor(null, 0, 1, null)._1}
		}
		it ("getIconFor(null, 0, 0, null)._2 is transparentIcon") {
			compareIcons(CheckerboardTilesheet().transparentIcon){CheckerboardTilesheet().getIconFor(null, 0, 0, null)._2}
		}
	}
	describe ("CheckerboardTilesheet(red, blue, (32,48))") {
		val dut = CheckerboardTilesheet(Color.red, Color.blue, new Dimension(32,48))
		it ("light color is red") {
			assertResult(Color.red){dut.light}
		}
		it ("dark color is blue") {
			assertResult(Color.blue){dut.dark}
		}
		it ("dim is (32,48)") {
			assertResult(new Dimension(32,48)){dut.dim}
		}
		it ("name is 'Checkerboard: ...'") {
			assertResult("Checkerboard: java.awt.Color[r=255,g=0,b=0]/java.awt.Color[r=0,g=0,b=255]"){dut.name}
		}
		it ("toString is 'Checkerboard: ...'") {
			assertResult("Checkerboard: java.awt.Color[r=255,g=0,b=0]/java.awt.Color[r=0,g=0,b=255], java.awt.Dimension[width=32,height=48]"){dut.toString}
		}
		it ("lightIcon is SolidColorIcon(Color.red, 32,48)") {
			compareIcons(new SolidColorIcon(Color.red, 32,48)){dut.lightIcon}
		}
		it ("darkIcon is SolidColorIcon(Color.blue, 32,48)") {
			compareIcons(new SolidColorIcon(Color.blue, 32,48)){dut.darkIcon}
		}
		it ("transparentIcon is SolidColorIcon(Color(0, true), 32,48)") {
			compareIcons(new SolidColorIcon(new Color(0,true), 32,48)){dut.transparentIcon}
		}
		it ("getIconFor(null, 0, 0, null)._1 is lightIcon") {
			compareIcons(dut.lightIcon){dut.getIconFor(null, 0, 0, null)._1}
		}
		it ("getIconFor(null, 0, 1, null)._1 is darkIcon") {
			compareIcons(dut.darkIcon){dut.getIconFor(null, 0, 1, null)._1}
		}
		it ("getIconFor(null, 0, 0, null)._2 is transparentIcon") {
			compareIcons(dut.transparentIcon){dut.getIconFor(null, 0, 0, null)._2}
		}
	}
	
	
	def compareIcons(a:Icon)(b:Icon) = {
		val a2 = a.asInstanceOf[SolidColorIcon]
		val b2 = b.asInstanceOf[SolidColorIcon]
		
		assertResult(a2.getIconColor){b2.getIconColor}
		assertResult(a2.getIconWidth){b2.getIconWidth}
		assertResult(a2.getIconHeight){b2.getIconHeight}
	}
}
