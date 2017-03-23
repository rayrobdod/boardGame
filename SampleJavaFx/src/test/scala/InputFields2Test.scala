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
import scala.collection.immutable.Seq
import com.rayrobdod.boardGame.view._
import com.rayrobdod.boardGame.javafxView.FxTests
import com.rayrobdod.boardGame.javafxView.InitializeFx

@FxTests
class InputFields2Test extends FunSpec {
	
	describe ("InputFields.tilesheet") {
		it ("string 'tag:rayrobdod.name,2013-08:tilesheet-indexies' gets an IndexesTilesheet") {
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			assert{new InputFields2("tag:rayrobdod.name,2013-08:tilesheet-indexies", "", "", null).tilesheet.isInstanceOf[IndexesTilesheet[_,_,_]]}
		}
		it ("string 'tag:rayrobdod.name,2013-08:tilesheet-randcolor' gets an RandomColorTilesheet") {
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			assert{new InputFields2("tag:rayrobdod.name,2013-08:tilesheet-randcolor", "", "", null).tilesheet.isInstanceOf[RandomColorTilesheet[_,_,_]]}
		}
		it ("string 'tag:rayrobdod.name,2013-08:tilesheet-nil' gets an NilTilesheet") {
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			assert{new InputFields2("tag:rayrobdod.name,2013-08:tilesheet-nil", "", "", null).tilesheet.isInstanceOf[NilTilesheet[_,_,_]]}
		}
		it ("string 'tag:rayrobdod.name,2015-06-12:tilesheet-hashcolor' gets an HashcodeColorTilesheet") {
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			assert{new InputFields2("tag:rayrobdod.name,2015-06-12:tilesheet-hashcolor", "", "", null).tilesheet.isInstanceOf[HashcodeColorTilesheet[_,_,_]]}
		}
	}
	describe ("InputFields2.rand") {
		it ("string '' corresponds to scala.util.Random"){
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			assertResult(scala.util.Random){new InputFields2("","","", null).rng}
		}
		it ("string 'a' corresponds to a Random that always returns '1'"){
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			assertResult(1){new InputFields2("","","a", null).rng.nextInt}
		}
		it ("string 'b' corresponds to a Random that always returns '0'"){
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			assertResult(0){new InputFields2("","","b", null).rng.nextInt}
		}
		it ("string '123' corresponds to a Random with seed 123"){
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			assertResult(new Random(123).nextInt){new InputFields2("","","123", null).rng.nextInt}
		}
		it ("errors when given input \"abc\""){
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			intercept[IllegalStateException]{
				new InputFields2("","","abc", null).rng
			}
		}
	}
	describe ("fieldIsRotationField") {
		it ("is true if the Field is the specified string") {
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			assertResult(true){new InputFields2("", "tag:rayrobdod.name,2013-08:map-rotate", "", null).fieldIsRotationField}
		}
		it ("is false otherwise") {
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			assertResult(false){new InputFields2("", "", "", null).fieldIsRotationField}
		}
	}
	describe ("field") {
		it ("can get a thing") {
			if (! InitializeFx.isSetup) { InitializeFx.setup() }
			
			import com.rayrobdod.boardGame.RectangularField
			val exp = RectangularField(Seq(Seq("a","b","c"), Seq("d","e","f")))
			val url = this.getClass.getResource("abc.csv")
			assertResult(exp){new InputFields2("", url.toString, "", null).field}
		}
	}
}
