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

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks
import java.util.ServiceConfigurationError
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8

class RectangularTilesheetLoaderTest extends FunSpec {
	describe("RectangularTilesheetLoader") {
		it ("can find scala objects using dot notation") {
			val dut = new RectangularTilesheetLoader(
				"com.rayrobdod.boardGame.aaaaa",
				ConstTrueSpaceClassMatcherFactory,
				new SingleHardcodedResourceClassLoader(
					"META-INF/services/com.rayrobdod.boardGame.aaaaa",
					new URL("data", "", -1, "com.rayrobdod.boardGame.swingView.NilTilesheet\n", new DataHandler)
				)
			)
			
			assertResult(List(NilTilesheet)){dut.toList}
		}
		ignore ("can find files using slash notation") {}
		it ("errors if service does not exist") {
			val dut = new RectangularTilesheetLoader(
				"com.rayrobdod.boardGame.aaaaa",
				ConstTrueSpaceClassMatcherFactory,
				new SingleHardcodedResourceClassLoader(
					"blarb",
					new URL("data", "", -1, "\n", new DataHandler),
					null
				)
			)
			
			assertResult(List()){dut.toList}
		}
		it ("errors if a nonexistant class is called for") {
			val dut = new RectangularTilesheetLoader(
				"com.rayrobdod.boardGame.aaaaa",
				ConstTrueSpaceClassMatcherFactory,
				new SingleHardcodedResourceClassLoader(
					"META-INF/services/com.rayrobdod.boardGame.aaaaa",
					new URL("data", "", -1, "com.example\n", new DataHandler),
					null
				)
			)
			
			intercept[ServiceConfigurationError] {
				dut.toList
			}
		}
		it ("errors if class is not a Rectangular Tilesheet") {
			val dut = new RectangularTilesheetLoader(
				"com.rayrobdod.boardGame.aaaaa",
				ConstTrueSpaceClassMatcherFactory,
				new SingleHardcodedResourceClassLoader(
					"META-INF/services/com.rayrobdod.boardGame.aaaaa",
					new URL("data", "", -1, "com.rayrobdod.boardGame.swingView.RectangularTilesheetLoaderTest\n", new DataHandler),
					classOf[RectangularTilesheetLoaderTest].getClassLoader
				)
			)
			
			intercept[ServiceConfigurationError] {
				dut.toList
			}
		}
	}
}












	
class SingleHardcodedResourceClassLoader(
		resName:String,
		value:URL,
		parent:ClassLoader = Thread.currentThread().getContextClassLoader()
) extends ClassLoader(parent) {
	protected override def findClass(name:String) = {
		throw new ClassNotFoundException("MockClassLoader cannot find classes")
	}
	protected override def findResource(name:String):URL = {
		if (name == resName) {value} else {null}
	}
	protected override def findResources(name:String):java.util.Enumeration[URL] = {
		if (name == resName) {
			new java.util.Enumeration[URL]() {
				private var _hasNext = true
				override def hasMoreElements = _hasNext
				override def nextElement = {
					if (this._hasNext) {
						_hasNext = false
						value
					} else {
						throw new NoSuchElementException("Singleton Enumeration alreay returned value")
					}
				}
			}
		} else {
			java.util.Collections.emptyEnumeration()
		}
	}
}

class DataHandler extends java.net.URLStreamHandler {
	override def openConnection(u:URL):java.net.URLConnection = {
		new DataConnection(u)
	}
}

class DataConnection(u:URL) extends java.net.URLConnection(u) {
	override def connect():Unit = {}
	override def getInputStream():java.io.InputStream = {
		new java.io.ByteArrayInputStream(u.getFile.getBytes(UTF_8))
	}
}
