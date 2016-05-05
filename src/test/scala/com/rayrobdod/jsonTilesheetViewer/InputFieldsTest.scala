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
import scala.util.Random
import com.rayrobdod.boardGame.view.IndexesTilesheet
import com.rayrobdod.boardGame.view.RandomColorTilesheet
import com.rayrobdod.boardGame.view.NilTilesheet

class InputFieldsTest extends FunSpec {
	InitTagProtocol.apply()
	
	
	describe ("InputFields.tilesheet") {
		it ("string 'tag:rayrobdod.name,2013-08:tilesheet-indexies' gets an IndexesTilesheet") {
			assert{new InputFields("tag:rayrobdod.name,2013-08:tilesheet-indexies", "", "").tilesheet.isInstanceOf[IndexesTilesheet[_]]}
		}
		it ("string 'tag:rayrobdod.name,2013-08:tilesheet-randcolor' gets an RandomColorTilesheet") {
			assert{new InputFields("tag:rayrobdod.name,2013-08:tilesheet-randcolor", "", "").tilesheet.isInstanceOf[RandomColorTilesheet[_]]}
		}
		it ("string 'tag:rayrobdod.name,2013-08:tilesheet-nil' gets an NilTilesheet") {
			assert{new InputFields("tag:rayrobdod.name,2013-08:tilesheet-nil", "", "").tilesheet.isInstanceOf[NilTilesheet[_]]}
		}
	}
	describe ("InputFields.rand") {
		it ("string '' corresponds to scala.util.Random"){
			assertResult(scala.util.Random){new InputFields("","","").rng}
		}
		it ("string 'a' corresponds to a Random that always returns '1'"){
			assertResult(1){new InputFields("","","a").rng.nextInt}
		}
		it ("string 'b' corresponds to a Random that always returns '0'"){
			assertResult(0){new InputFields("","","b").rng.nextInt}
		}
		it ("string '123' corresponds to a Random with seed 123"){
			assertResult(new Random(123).nextInt){new InputFields("","","123").rng.nextInt}
		}
		it ("errors when given input \"abc\""){
			intercept[IllegalStateException]{
				new InputFields("","","abc").rng
			}
		}
	}
}
