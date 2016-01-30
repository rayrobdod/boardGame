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
package com.rayrobdod.jsonTilesheetViewer;

import java.net.ContentHandlerFactory;
import java.net.ContentHandler;


/**
 * 
 * 
 * This could be a class instead of an object, but only one
 * ContentHandlerFactory exists at a time, ever, so what's the point?
 * 
 * this is not optimal, but given the system's inablity to guess
 * mime types, I guess it is required.
 */
object ToggleContentHandlerFactory extends ContentHandlerFactory {
	
	private var current:ContentHandler = null;
	
	def createContentHandler(mime:String):ContentHandler = current;
	
	def setCurrentToTilesheet() {
		current = new JsonRectangularTilesheetHandler;
	}
	def setCurrentToTilesheetFx() {
		current = new JsonRectangularTilesheetHandler2;
	}
	def setCurrentToField() {
		current = new JsonMapHandler;
	}
}
