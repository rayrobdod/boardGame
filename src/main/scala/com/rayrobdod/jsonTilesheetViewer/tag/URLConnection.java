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
package com.rayrobdod.jsonTilesheetViewer.tag;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import com.rayrobdod.boardGame.swingView.*;
import com.rayrobdod.jsonTilesheetViewer.CheckerboardURIMatcher;

public class URLConnection extends java.net.URLConnection {
	public URLConnection(URL u) {
		super(u);
	}
	
	public void connect() throws IOException {
		this.connected = true;
	}
	
	public Object getContent() throws IOException {
		if (this.getURL().getAuthority().equals("rayrobdod.name,2013-08")) {
			scala.Option<CheckerboardTilesheet> object = 
					CheckerboardURIMatcher.unapply(this.getURL());
			
			if (object.isDefined()) {
				return object.get();
			}
			
			switch (this.getURL().getPath()) {
				case "tilesheet-nil":
					return NilTilesheet$.MODULE$;
				case "tilesheet-indexies":
					return IndexesTilesheet$.MODULE$;
				case "tilesheet-randcolor":
					return new RandomColorTilesheet(
							new java.awt.Dimension(64,24));
						
				default:
					throw new IOException("Unknown tag:rayrobod.name,2013-08");
			}
		} else {
			throw new IOException("Unknown tag");
		}
	}
}
