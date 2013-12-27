package com.rayrobdod.jsonTilesheetViewer;

import java.net.ContentHandlerFactory;
import java.net.ContentHandler;
import java.util.Map;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * @note
 * this is not optimal, but given the system's inablity to guess
 * mime types, I guess it is required.
 */
object ToggleContentHandlerFactory extends ContentHandlerFactory {
	
	private var current:ContentHandler = null;
	
	def createContentHandler(mime:String) = current;
	
	def setCurrentToTilesheet() = {
		current = new JsonRectangularTilesheetHandler;
	}
}
