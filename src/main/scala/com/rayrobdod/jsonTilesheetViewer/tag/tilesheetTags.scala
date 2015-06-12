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
import java.util.Collections.emptyMap;
import com.rayrobdod.boardGame.swingView.IndexesTilesheet;
import com.rayrobdod.boardGame.swingView.RandomColorTilesheet;
import com.rayrobdod.boardGame.swingView.HashcodeColorTilesheet;

/** 
 */ 
final class IndexiesTilesheetTagResource extends TagResource {
	val name:String = "rayrobdod.name,2013-08:tilesheet-indexies"
	val getContent = IndexesTilesheet
	val getInputStream:InputStream = null
	val getHeaderFields:Map[String,List[String]] = emptyMap[String,List[String]]
}

final class RandomColorTilesheetTagResource extends TagResource {
	val name:String = "rayrobdod.name,2013-08:tilesheet-randcolor"
	def getContent:RandomColorTilesheet = new RandomColorTilesheet
	val getInputStream:InputStream = null
	val getHeaderFields:Map[String,List[String]] = emptyMap[String,List[String]]
}

final class HashcodeColorTilesheetTagResource extends TagResource {
	val name:String = "rayrobdod.name,2015-06-12:tilesheet-hashcolor"
	def getContent:HashcodeColorTilesheet = new HashcodeColorTilesheet
	val getInputStream:InputStream = null
	val getHeaderFields:Map[String,List[String]] = emptyMap[String,List[String]]
}
