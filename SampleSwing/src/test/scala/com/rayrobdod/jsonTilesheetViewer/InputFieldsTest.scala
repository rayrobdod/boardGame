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
import scala.util.Random
import scala.collection.immutable.Seq
import com.rayrobdod.boardGame.view._

class InputFieldsTest extends FunSpec {
	import scala.language.existentials
	
	describe ("InputFields.tilesheet") {
		it ("string 'tag:rayrobdod.name,2013-08:tilesheet-indexies' gets an IndexesTilesheet") {
			val inputfields = new InputFields("tag:rayrobdod.name,2013-08:tilesheet-indexies", "", "")
			val tilesheet = inputfields.tilesheet(inputfields.dimension)
			assert{tilesheet.isInstanceOf[IndexesTilesheet[_,_,_]]}
		}
		it ("string 'tag:rayrobdod.name,2013-08:tilesheet-randcolor' gets an RandomColorTilesheet") {
			val inputfields = new InputFields("tag:rayrobdod.name,2013-08:tilesheet-randcolor", "", "")
			val tilesheet = inputfields.tilesheet(inputfields.dimension)
			assert{tilesheet.isInstanceOf[RandomColorTilesheet[_,_,_]]}
		}
		it ("string 'tag:rayrobdod.name,2013-08:tilesheet-nil' gets an NilTilesheet") {
			val inputfields = new InputFields("tag:rayrobdod.name,2013-08:tilesheet-nil", "", "")
			val tilesheet = inputfields.tilesheet(inputfields.dimension)
			assert{tilesheet.isInstanceOf[NilTilesheet[_,_,_]]}
		}
		it ("string 'tag:rayrobdod.name,2015-06-12:tilesheet-hashcolor' gets an HashcodeColorTilesheet") {
			val inputfields = new InputFields("tag:rayrobdod.name,2015-06-12:tilesheet-hashcolor", "", "")
			val tilesheet = inputfields.tilesheet(inputfields.dimension)
			assert{tilesheet.isInstanceOf[HashcodeColorTilesheet[_,_,_]]}
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
	describe ("fieldIsRotationField") {
		it ("is true if the Field is the specified string") {
			assertResult(true){new InputFields("", "tag:rayrobdod.name,2013-08:map-rotate", "").fieldIsRotationField}
		}
		it ("is false otherwise") {
			assertResult(false){new InputFields("", "", "").fieldIsRotationField}
		}
	}
	describe ("field") {
		it ("can get a thing") {
			import com.rayrobdod.boardGame.RectangularField
			val exp = RectangularField(Seq(Seq("a","b","c"), Seq("d","e","f")))
			val url = this.getClass.getResource("abc.csv")
			val inputFields = new InputFields("", url.toString, "")
			assertResult(exp){inputFields.field(inputFields.dimension)}
		}
	}
}
