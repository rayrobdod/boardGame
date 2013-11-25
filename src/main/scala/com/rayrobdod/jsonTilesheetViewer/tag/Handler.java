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
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.MalformedURLException;

// TODO: move to utilities
public class Handler extends java.net.URLStreamHandler {

	public static final String urlChar = "[\\w\\-\\_\\.\\!\\~\\*\\'\\(\\)\\%\\&\\=]+";
	public static final String datePattern = "\\d\\d\\d\\d(?:-\\d\\d(?:-\\d\\d)?)?";
	public static final Pattern tagPattern = Pattern.compile("(("+urlChar+"),"+datePattern+"):("+urlChar+")(?:\\?("+urlChar+"))?" );
		
	
	
	protected int getDefaultPort() {return -1;}

	public java.net.URLConnection openConnection(URL u) throws IOException {
		return new URLConnection(u);
	}
	
	public void parseURL(URL u, String spec, int start, int limit) {
		
		final Matcher tagMatcher = tagPattern.matcher(spec.substring(start, limit));
		final String ref1 = spec.substring(limit);
		final String ref2 = (ref1.equals("") ? null : ref1);
		
		
		if (tagMatcher.matches()) {
			this.setURL(u, "tag",
					/* host */ tagMatcher.group(2),
					/* port */ -1,
					/* authority */ tagMatcher.group(1),
					/* userInfo */ null,
					/* path */ tagMatcher.group(3),
					/* query */ tagMatcher.group(4),
					/* ref */ ref2
            );
        } else {
        	// throw new MalformedURLException();
        	
        	
			this.setURL(u, "tag",
					/* host */ null,
					/* port */ -1,
					/* authority */ null,
					/* userInfo */ null,
					/* path */ null,
					/* query */ null,
					/* ref */ null
            );
        }
	}
	
	public String toExternalForm(URL u) {
		return "tag:" + u.getAuthority() + ":" + u.getPath() +
				(u.getQuery() != null ? "?" + u.getQuery() : "") +
				(u.getRef()   != null ? "#" + u.getRef() : "");
	}
}

