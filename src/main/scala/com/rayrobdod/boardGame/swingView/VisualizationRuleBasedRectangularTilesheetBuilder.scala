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
package swingView

import java.awt.image.BufferedImage
import java.io.Reader
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import javax.imageio.ImageIO
import scala.collection.immutable.Seq
import com.rayrobdod.util.BlitzAnimImage
import com.rayrobdod.json.builder.{Builder, SeqBuilder}
import com.rayrobdod.json.parser.{Parser, JsonParser}
import com.rayrobdod.json.union.{StringOrInt, JsonValue}
import VisualizationRuleBasedRectangularTilesheetBuilder.Delayed

/**
 * A Builder of VisualizationRuleBasedRectangularTilesheet
 * @constructor
 * @param baseUrl the url to resolve relative urls against
 */
final class VisualizationRuleBasedRectangularTilesheetBuilder[A](
		baseUrl:URL,
		classMap:SpaceClassMatcherFactory[A]
) extends Builder[StringOrInt, JsonValue, Delayed[A]] {
	def init:Delayed[A] = new Delayed[A](classMap)
	def apply[I](a:Delayed[A], key:StringOrInt, input:I, parser:Parser[StringOrInt, JsonValue, I]):Either[(String, Int), Delayed[A]] = key match {
		case StringOrInt.Left("tiles") => {
			parser.parsePrimitive(input).right.flatMap{_.stringToEither{value => Right(a.copy(sheetUrl = new URL(baseUrl, value)))}}
		}
		case StringOrInt.Left("tileWidth") => {
			parser.parsePrimitive(input).right.flatMap{_.integerToEither{value => Right(a.copy(tileWidth = value))}}
		}
		case StringOrInt.Left("tileHeight") => {
			parser.parsePrimitive(input).right.flatMap{_.integerToEither{value => Right(a.copy(tileHeight = value))}}
		}
		case StringOrInt.Left("rules") => {
			parser.parsePrimitive(input).right.flatMap{_.stringToEither{value => Right(a.copy(rules = new URL(baseUrl, value)))}}
		}
		case StringOrInt.Left("name") => {
			parser.parsePrimitive(input).right.flatMap{_.stringToEither{value => Right(a.copy(name = value))}}
		}
		case _ => Right(a)
	}
}

object VisualizationRuleBasedRectangularTilesheetBuilder {
	/**
	 * A case class that collects properties about a VisualizationRuleBasedRectangularTilesheet,
	 * and has a method to construct one based on the properties provided.
	 * Done because a logical "frameImages" depends on three different json properties
	 * and eagerly dealing with those may be inefficient.
	 */
	final case class Delayed[A] (
		classMap:SpaceClassMatcherFactory[A],
		sheetUrl:URL = new URL("http://localhost:80/"),
		tileWidth:Int = 1,
		tileHeight:Int = 1,
		rules:URL = new URL("http://localhost:80/"),
		name:String = "???"
	) {
		def apply():VisualizationRuleBasedRectangularTilesheet[A] = {
			new VisualizationRuleBasedRectangularTilesheet(name, visualizationRules)
		}
		
		private def frameImages:BlitzAnimImage = {
			val sheetImage:BufferedImage = ImageIO.read(sheetUrl)
			val tilesX = sheetImage.getWidth / tileWidth
			val tilesY = sheetImage.getHeight / tileHeight
			
			new BlitzAnimImage(sheetImage, tileWidth, tileHeight, 0, tilesX * tilesY)
		}
		
		private def visualizationRules:Seq[ParamaterizedRectangularVisualizationRule[A]] = {
			val b = new RectangularVisualziationRuleBuilder[A](Seq.empty ++ frameImages.getImages, classMap)
			var r:Reader = new java.io.StringReader("{}")
			try {
				r = new java.io.InputStreamReader(rules.openStream(), UTF_8)
				new JsonParser().parse(new SeqBuilder(b), r).fold(
					{c => c},
					{p => throw new java.text.ParseException("Parsed value: " + p.toString, 0)},
					{(msg, idx) => throw new java.text.ParseException(msg, idx)}
				)
			} finally {
				r.close()
			}
		}
	} 
}

