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
import scala.util.{Either, Left, Right}
import com.rayrobdod.json.parser.{Parser, JsonParser}
import com.rayrobdod.json.builder.{Builder, SeqBuilder, MapBuilder, ThrowBuilder}
import com.rayrobdod.json.union.{StringOrInt, JsonValue}
import VisualizationRuleBasedRectangularTilesheetBuilder.Delayed

final class VisualizationRuleBasedRectangularTilesheetBuilder[SpaceClass, IconPart, Icon](
		baseUrl:URL,
		classMap:SpaceClassMatcherFactory[SpaceClass],
		compostLayers:Function1[Seq[Seq[IconPart]], Icon],
		urlToFrameImages:Function2[URL, java.awt.Dimension, Seq[IconPart]]
) extends Builder[String, JsonValue, Delayed[SpaceClass, IconPart, Icon]] {
	override def init:Delayed[SpaceClass, IconPart, Icon] = new Delayed[SpaceClass, IconPart, Icon](classMap, compostLayers, urlToFrameImages)
	override def apply[Input](a:Delayed[SpaceClass, IconPart, Icon], key:String, input:Input, parser:Parser[String, JsonValue, Input]):Either[(String, Int), Delayed[SpaceClass, IconPart, Icon]] = {
		parser.parsePrimitive(input).right.flatMap{value => Right{
			import JsonValue._
			(key, value) match {
				case ("tiles", JsonValueString(x)) => a.copy(sheetUrl = new URL(baseUrl, x))
				case ("tileWidth", JsonValueNumber(x)) => a.copy(tileWidth = x.intValue)
				case ("tileHeight", JsonValueNumber(x)) => a.copy(tileHeight = x.intValue)
				case ("rules", JsonValueString(x)) => a.copy(rules = new URL(baseUrl, x))
				case ("name", JsonValueString(x)) => a.copy(name = x)
				case _ => a
			}
		}}
	}
}

object VisualizationRuleBasedRectangularTilesheetBuilder {
	
	case class Delayed[SpaceClass, IconPart, Icon] (
		classMap:SpaceClassMatcherFactory[SpaceClass],
		compostLayers:Function1[Seq[Seq[IconPart]], Icon],
		urlToFrameImages:Function2[URL, java.awt.Dimension, Seq[IconPart]],
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
			val b = new RectangularVisualziationRuleBuilder[SpaceClass, IconPart](urlToFrameImages(sheetUrl, new java.awt.Dimension(tileWidth, tileHeight)), classMap).mapKey{StringOrInt.unwrapToString}
			var r:Reader = new java.io.StringReader("{}")
			try {
				r = new java.io.InputStreamReader(rules.openStream(), UTF_8)
				new JsonParser().parse(new SeqBuilder(b), r).fold(
					{x => x},
					{x => throw new java.text.ParseException("Parsed to primitive value", 0)},
					{(s,i) => throw new java.text.ParseException(s,i)}
				)
			} finally {
				r.close()
			}
		}
	} 
}
