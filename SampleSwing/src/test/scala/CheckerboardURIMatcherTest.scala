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

import org.scalatest.FunSpec
import java.awt.Color
import com.rayrobdod.boardGame.view._
import CheckerboardURIMatcher.CheckerboardTilesheetDelay

class CheckerboardURIMatcherTest extends FunSpec {
	
	private val transIcon = {() => ((0, -1, -1))}
	private val rgbToIcon = {(c:Color, d:RectangularDimension) => ((c.getRGB, d.width, d.height))} 
	
	
	describe ("CheckerboardTilesheetDelay") {
		def doThing(
			name:String, delay:CheckerboardTilesheetDelay,
			transparentIcon:(Int,Int,Int), lightIcon:(Int,Int,Int), darkIcon:(Int,Int,Int)
		) = {
			it (name) {
				val dut = delay.apply(transIcon, rgbToIcon)
				assertResult( Seq(transparentIcon) ){ dut.getIconFor(null, (0, 0), null).belowFrames }
				assertResult( Seq(lightIcon) ){ dut.getIconFor(null, (0, 0), null).aboveFrames }
				assertResult( Seq(darkIcon) ){ dut.getIconFor(null, (0, 1), null).aboveFrames }
			}
		}
		
		doThing("Default is 24x24 black and white",
			CheckerboardTilesheetDelay(),
			((0, -1, -1)),
			((-1, 24, 24)),
			((0xFF000000, 24, 24))
		)
		doThing("changing 'light' changes the light color",
			CheckerboardTilesheetDelay(light = Color.RED),
			((0, -1, -1)),
			((0xFFFF0000, 24, 24)),
			((0xFF000000, 24, 24))
		)
		doThing("changing 'dark' changes the dark color",
			CheckerboardTilesheetDelay(dark = Color.BLUE),
			((0, -1, -1)),
			((0xFFFFFFFF, 24, 24)),
			((0xFF0000FF, 24, 24))
		)
		doThing("changing 'tileDimension' changes the dimension",
			CheckerboardTilesheetDelay(tileDimension = RectangularDimension(12, 59)),
			((0, -1, -1)),
			((0xFFFFFFFF, 12, 59)),
			((0xFF000000, 12, 59))
		)
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
			assertResult(Some(CheckerboardTilesheetDelay(tileDimension = RectangularDimension(12345,12345)))){
				CheckerboardURIMatcher.unapply(TAG_SHEET_CHECKER + "?size=12345")
			}
		}
		it ("ignores unkown params") {
			assertResult(Some(CheckerboardTilesheetDelay())){
				CheckerboardURIMatcher.unapply(TAG_SHEET_CHECKER + "?blorp=12")
			}
		}
		it ("allows multiple parameters in an arbitrary order") {
			assertResult(Some(CheckerboardTilesheetDelay(light = new Color(123), dark = new Color(45), tileDimension = RectangularDimension(1,1)))){
				CheckerboardURIMatcher.unapply(TAG_SHEET_CHECKER + "?light=123&dark=45&size=1")
			}
		}
		it ("returns None if one of the parameters is invalid") {
			assertResult(None){ CheckerboardURIMatcher.unapply(TAG_SHEET_CHECKER + "?size=meter") }
		}
	}
}
