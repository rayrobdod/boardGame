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

object InitTagProtocol {
	/*
	private val prop:String = "java.protocol.handler.pkgs";
	private val pkg:String = "com.rayrobdod.tagprotocol";
	
	private var value:String = System.getProperty(prop);
	if (value == null || !value.split('|').contains(pkg)) {
		value = if (value == null) {pkg} else {value + "|" + pkg};
		System.setProperty(prop, value);
	} */
	
	
	
	def apply() = {
		try {
			java.net.URL.setURLStreamHandlerFactory(new java.net.URLStreamHandlerFactory() {
				override def createURLStreamHandler(p:String) = p match {
					case "tag" => new com.rayrobdod.tagprotocol.tag.Handler
					case _ => null
				}
			})
		} catch {
			case e:Error => {
				// just assume it has already been set
			}
		}
	}
}
