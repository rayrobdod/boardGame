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
package javafxView

import java.io.Reader
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import scala.collection.immutable.Seq
import javafx.scene.image.{Image, WritableImage}
import com.rayrobdod.json.parser.JsonParser
import com.rayrobdod.json.builder.{Builder, SeqBuilder, MapBuilder}
import VisualizationRuleBasedRectangularTilesheetBuilder.Delayed

class VisualizationRuleBasedRectangularTilesheetBuilder[A](
		baseUrl:URL,
		classMap:SpaceClassMatcherFactory[A]
) extends Builder[VisualizationRuleBasedRectangularTilesheetBuilder.Delayed[A]] {
	def init:Delayed[A] = new Delayed[A](classMap)
	def apply(a:Delayed[A], key:String, value:Any):Delayed[A] = key match {
		case "tiles" => a.copy(sheetUrl = new URL(baseUrl, value.toString)) 
		case "tileWidth" => a.copy(tileWidth = value.asInstanceOf[Long].intValue)
		case "tileHeight" => a.copy(tileHeight = value.asInstanceOf[Long].intValue)
		case "rules" => a.copy(rules = new URL(baseUrl, value.toString))
		case "name" => a.copy(name = value.toString)
		case _ => a
	}
	def childBuilder(key:String):Builder[_] = new MapBuilder
	override val resultType:Class[Delayed[A]] = classOf[Delayed[A]]
}

object VisualizationRuleBasedRectangularTilesheetBuilder {
	case class Delayed[A] (
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
		
		private def frameImages:Seq[Image] = {
			val sheetImage:Image = new Image(sheetUrl.toString)
			val tilesX = sheetImage.getWidth.intValue / tileWidth
			val tilesY = sheetImage.getHeight.intValue / tileHeight
			
			(0 to (sheetImage.getWidth.intValue - tileWidth) by tileWidth).flatMap{x:Int =>
				(0 to (sheetImage.getHeight.intValue - tileHeight) by tileHeight).map{y:Int =>
					val retVal = new WritableImage(tileWidth, tileHeight)
					retVal.getPixelWriter().setPixels(
							0, 0,
							tileWidth, tileHeight,
							sheetImage.getPixelReader,
							x, y
					)
					retVal
				}
			}
		}
		
		private def visualizationRules:Seq[ParamaterizedRectangularVisualizationRule[A]] = {
			val b = new RectangularVisualziationRuleBuilder[A](Seq.empty ++ frameImages, classMap)
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

