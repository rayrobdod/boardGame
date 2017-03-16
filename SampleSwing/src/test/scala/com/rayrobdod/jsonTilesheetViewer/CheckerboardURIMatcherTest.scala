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
package com.rayrobdod.jsonTilesheetViewer

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks
import java.awt.{Dimension, Color}
import scala.util.Random
import scala.collection.immutable.Seq
import com.rayrobdod.boardGame.view._
import CheckerboardURIMatcher.CheckerboardTilesheetDelay

class CheckerboardURIMatcherTest extends FunSpec {
	
	private val transIcon = {(d:Dimension) => ((0, d.width, d.height))}
	private val rgbToIcon = {(c:Color, d:Dimension) => ((c.getRGB, d.width, d.height))} 
	
	
	describe ("CheckerboardTilesheetDelay") {
		it ("Default is 24x24 black and white") {
			val dut = CheckerboardTilesheetDelay().apply(transIcon, rgbToIcon)
			assertResult( ((0, 24, 24)) ){ dut.transparentIcon() }
			assertResult( ((-1, 24, 24)) ){ dut.lightIcon() }
			assertResult( ((0xFF000000, 24, 24)) ){ dut.darkIcon() }
		}
		it ("changing 'light' changes the light color") {
			val dut = CheckerboardTilesheetDelay(light = Color.RED).apply(transIcon, rgbToIcon)
			assertResult( ((0, 24, 24)) ){ dut.transparentIcon() }
			assertResult( ((0xFFFF0000, 24, 24)) ){ dut.lightIcon() }
			assertResult( ((0xFF000000, 24, 24)) ){ dut.darkIcon() }
		}
		it ("changing 'dark' changes the dark color") {
			val dut = CheckerboardTilesheetDelay(dark = Color.BLUE).apply(transIcon, rgbToIcon)
			assertResult( ((0, 24, 24)) ){ dut.transparentIcon() }
			assertResult( ((0xFFFFFFFF, 24, 24)) ){ dut.lightIcon() }
			assertResult( ((0xFF0000FF, 24, 24)) ){ dut.darkIcon() }
		}
		it ("changing 'tileDimension' changes the dimension") {
			val dut = CheckerboardTilesheetDelay(tileDimension = new Dimension(12, 59)).apply(transIcon, rgbToIcon)
			assertResult( ((0, 12, 59)) ){ dut.transparentIcon() }
			assertResult( ((0xFFFFFFFF, 12, 59)) ){ dut.lightIcon() }
			assertResult( ((0xFF000000, 12, 59)) ){ dut.darkIcon() }
		}
	}
	describe ("CheckerboardURIMatcher.unapply") {
		it ("Returns none if the string does not start with TAG_SHEET_CHECKER") {
			assertResult(None){CheckerboardURIMatcher.unapply("asdfasdf")}
		}
		it ("Returns Some(CheckerboardTilesheetDelay()) with no uri paramters") {
			assertResult(Some(CheckerboardTilesheetDelay())){
				CheckerboardURIMatcher.unapply(TAG_SHEET_CHECKER)
			}
		}
		it ("'light' uri param changes light color") {
			assertResult(Some(CheckerboardTilesheetDelay(light = new Color(12345)))){
				CheckerboardURIMatcher.unapply(TAG_SHEET_CHECKER + "?light=12345")
			}
		}
		it ("'dark' uri param changes dark color") {
			assertResult(Some(CheckerboardTilesheetDelay(dark = new Color(12345)))){
				CheckerboardURIMatcher.unapply(TAG_SHEET_CHECKER + "?dark=12345")
			}
		}
		it ("'size' uri param changes size") {
			assertResult(Some(CheckerboardTilesheetDelay(tileDimension = new Dimension(12345,12345)))){
				CheckerboardURIMatcher.unapply(TAG_SHEET_CHECKER + "?size=12345")
			}
		}
		it ("ignores unkown params") {
			assertResult(Some(CheckerboardTilesheetDelay())){
				CheckerboardURIMatcher.unapply(TAG_SHEET_CHECKER + "?blorp=12")
			}
		}
		it ("allows multiple parameters in an arbitrary order") {
			assertResult(Some(CheckerboardTilesheetDelay(light = new Color(123), dark = new Color(45), tileDimension = new Dimension(1,1)))){
				CheckerboardURIMatcher.unapply(TAG_SHEET_CHECKER + "?light=123&dark=45&size=1")
			}
		}
		it ("returns None if one of the parameters is invalid") {
			assertResult(None){ CheckerboardURIMatcher.unapply(TAG_SHEET_CHECKER + "?size=meter") }
		}
	}
}
