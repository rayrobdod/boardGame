package com.rayrobdod.jsonTilesheetViewer.tag;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import com.rayrobdod.boardGame.swingView.*;
import com.rayrobdod.jsonTilesheetViewer.CheckerboardURIMatcher;

class URLConnection extends java.net.URLConnection {
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
	
	public InputStream getInputStream() throws IOException {
		return null;
	}
}
