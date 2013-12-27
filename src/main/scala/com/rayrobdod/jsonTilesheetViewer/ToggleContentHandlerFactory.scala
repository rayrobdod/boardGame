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
	
	def createContentHandler(mime:String) = current;
	
	def setCurrentToTilesheet() {
		current = new JsonRectangularTilesheetHandler;
	}
	def setCurrentToField() {
		current = new JsonMapHandler;
	}
}
