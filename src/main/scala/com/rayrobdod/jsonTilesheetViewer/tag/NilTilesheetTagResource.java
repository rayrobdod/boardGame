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
package com.rayrobdod.jsonTilesheetViewer.tags;

import com.rayrobdod.tagprotocol.tag.TagResource;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import static java.util.Collections.singletonList;
import com.rayrobdod.boardGame.swingView.NilTilesheet$;
import com.rayrobdod.boardGame.swingView.RectangularTilesheet;

/** 
 */ 
public final class NilTilesheetTagResource implements TagResource {
	private static final String ENCODING = "utf-8";
	
	public String name() {
		return "rayrobdod.name,2013-08:tilesheet-nil";
	}
	
	/** Returns an visually void Component */
	public RectangularTilesheet getContent() {
		return NilTilesheet$.MODULE$;
	}
	
	/** Returns an empty inputstream */
	public InputStream getInputStream() { return null; }
	
	public Map<String,List<String>> getHeaderFields() {
		return java.util.Collections.emptyMap();
	}
}
