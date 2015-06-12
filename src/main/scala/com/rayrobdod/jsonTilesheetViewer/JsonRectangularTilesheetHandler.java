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
package com.rayrobdod.jsonTilesheetViewer;

import java.net.ContentHandler;
import java.net.URLConnection;
import java.io.IOException;
import java.io.StringWriter;
import java.io.InputStreamReader;
import com.rayrobdod.boardGame.swingView.JSONRectangularTilesheet;

/**
 * A contentHandler that will compose a JSONTilesheet from a json and linked documents 
 */
public final class JsonRectangularTilesheetHandler extends ContentHandler {
	
	@Override
	/**
	 * Reads data from a URLConnection's input stream and puts
	 * that data into a string.
	 */
	public JSONRectangularTilesheet<String> getContent(URLConnection urlc) throws IOException {
		
		return JSONRectangularTilesheet.apply(urlc.getURL(), StringSpaceClassMatcherFactory$.MODULE$);
	}
	
	@Override
	/**
	 * if and of the classes are a superclass of JSONRectangularTilesheet,
	 * returns same as getContent(urlc)
	 * Otherwise, returns null.
	 */
	public Object getContent(final URLConnection urlc, final Class[] classes) throws IOException {
		
		for (Class<?> c : classes) {
			if (c.isAssignableFrom(JSONRectangularTilesheet.class)) {
				return this.getContent(urlc);
			}
		}
		
		return null;
		
	}
	
	protected boolean canEquals(Object other) {
		return (other instanceof JsonRectangularTilesheetHandler);
	}
	
	@Override
	public boolean equals(Object other) {
		return (this.canEquals(other) && ((JsonRectangularTilesheetHandler) other).canEquals(this));
	}
}
