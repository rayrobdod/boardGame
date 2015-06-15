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
package com.rayrobdod.jsonTilesheetViewer

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks
import java.net.URL
import scala.collection.immutable.Seq
import scala.util.Random
import com.rayrobdod.boardGame.RectangularField
import com.rayrobdod.boardGame.swingView.DataHandler

class JsonMapHandlerTest extends FunSpec {
	
	describe ("JsonMapHandler") {
		it ("parses a CSV into a RectangularField") {
			val url = new URL("data", "", -1, "1,2,3\n4,5,6\n7,8,9\n", new DataHandler)
			val expected = RectangularField(Seq(Seq("1","2","3"), Seq("4","5","6"), Seq("7","8","9")))
			val result = (new JsonMapHandler).getContent(url.openConnection())
			assertResult(expected){result}
		}
		it ("parses a CSV into a RectangularField (classes method)") {
			val url = new URL("data", "", -1, "1,2,3\n4,5,6\n7,8,9\n", new DataHandler)
			val expected = RectangularField(Seq(Seq("1","2","3"), Seq("4","5","6"), Seq("7","8","9")))
			val result = (new JsonMapHandler).getContent(url.openConnection(), Array(classOf[RectangularField[_]]))
			assertResult(expected){result}
		}
		it ("parses a CSV into a RectangularField (2)") {
			val url = new URL("data", "", -1, ",\0,\n", new DataHandler)
			val expected = RectangularField(Seq(Seq("","\0","")))
			val result = (new JsonMapHandler).getContent(url.openConnection(), Array(classOf[RectangularField[_]]))
			assertResult(expected){result}
		}
		it ("using classes version of getContent with a class unrelated to RectangularFields returns null") {
			val url = new URL("data", "", -1, "1,2,3\n4,5,6\n7,8,9\n", new DataHandler)
			val expected = null
			val result = (new JsonMapHandler).getContent(url.openConnection(), Array(classOf[String]))
			assertResult(expected){result}
		}
		
		it ("is equal to itself") {
			assert(new JsonMapHandler == new JsonMapHandler)
		}
		it ("is not equal to something else") {
			assert(new JsonMapHandler != "hello")
		}
	}
}
