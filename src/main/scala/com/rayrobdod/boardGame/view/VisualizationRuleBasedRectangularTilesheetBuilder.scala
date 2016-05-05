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
package com.rayrobdod.boardGame
package view

import java.io.Reader
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import scala.collection.immutable.Seq
import com.rayrobdod.json.parser.JsonParser
import com.rayrobdod.json.builder.{Builder, SeqBuilder, MapBuilder}
import VisualizationRuleBasedRectangularTilesheetBuilder.Delayed

final class VisualizationRuleBasedRectangularTilesheetBuilder[SpaceClass, IconPart, Icon](
		baseUrl:URL,
		classMap:SpaceClassMatcherFactory[SpaceClass],
		compostLayers:Function1[Seq[Seq[IconPart]], Icon],
		urlToFrameImages:Function3[URL, Int, Int, Seq[IconPart]]
) extends Builder[Delayed[SpaceClass, IconPart, Icon]] {
	def init:Delayed[SpaceClass, IconPart, Icon] = new Delayed[SpaceClass, IconPart, Icon](classMap, compostLayers, urlToFrameImages)
	def apply(a:Delayed[SpaceClass, IconPart, Icon], key:String, value:Any):Delayed[SpaceClass, IconPart, Icon] = key match {
		case "tiles" => a.copy(sheetUrl = new URL(baseUrl, value.toString)) 
		case "tileWidth" => a.copy(tileWidth = value.asInstanceOf[Long].intValue)
		case "tileHeight" => a.copy(tileHeight = value.asInstanceOf[Long].intValue)
		case "rules" => a.copy(rules = new URL(baseUrl, value.toString))
		case "name" => a.copy(name = value.toString)
		case _ => a
	}
	def childBuilder(key:String):Builder[_] = new MapBuilder
	override val resultType:Class[Delayed[SpaceClass, IconPart, Icon]] = classOf[Delayed[SpaceClass, IconPart, Icon]]
}

object VisualizationRuleBasedRectangularTilesheetBuilder {
	
	case class Delayed[SpaceClass, IconPart, Icon] (
		classMap:SpaceClassMatcherFactory[SpaceClass],
		compostLayers:Function1[Seq[Seq[IconPart]], Icon],
		urlToFrameImages:Function3[URL, Int, Int, Seq[IconPart]],
		sheetUrl:URL = new URL("http://localhost:80/"),
		tileWidth:Int = 1,
		tileHeight:Int = 1,
		rules:URL = new URL("http://localhost:80/"),
		name:String = "???"
	) {
		def apply():VisualizationRuleBasedRectangularTilesheet[SpaceClass, IconPart, Icon] = {
			VisualizationRuleBasedRectangularTilesheet[SpaceClass, IconPart, Icon](name, visualizationRules, compostLayers)
		}
		
		private def visualizationRules:Seq[ParamaterizedRectangularVisualizationRule[SpaceClass, IconPart]] = {
			val b = new RectangularVisualziationRuleBuilder[SpaceClass, IconPart](urlToFrameImages(sheetUrl, tileWidth, tileHeight), classMap)
			var r:Reader = new java.io.StringReader("{}")
			try {
				r = new java.io.InputStreamReader(rules.openStream(), UTF_8)
				new JsonParser(new SeqBuilder(b)).parse(r).map{b.resultType.cast(_)}
			} finally {
				r.close()
			}
		}
	} 
}
