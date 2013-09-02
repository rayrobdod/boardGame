package com.rayrobdod.jsonTilesheetViewer.tag;

import java.net.URL;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.MalformedURLException;

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
					/* host */ "???",
					/* port */ -1,
					/* authority */ "???",
					/* userInfo */ "",
					/* path */ "???",
					/* query */ "???",
					/* ref */ "???"
            );
        }
	}
	
	public String toExternalForm(URL u) {
		return "tag:" + u.getAuthority() + ":" + u.getPath() +
				(u.getQuery() != null ? "?" + u.getQuery() : "") +
				(u.getRef()   != null ? "#" + u.getRef() : "");
	}
}

