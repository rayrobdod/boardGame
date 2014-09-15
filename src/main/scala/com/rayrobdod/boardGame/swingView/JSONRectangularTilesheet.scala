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
package com.rayrobdod.boardGame.swingView

import com.rayrobdod.boardGame._
import com.rayrobdod.util.BlitzAnimImage
import com.rayrobdod.animation.{AnimationIcon, ImageFrameAnimation}
import scala.util.Random
import scala.{Function0 => Future}
import scala.annotation.tailrec
import scala.collection.immutable.{Seq, Map, Vector, Set, SortedMap}
import scala.collection.mutable.{Map => MMap}
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.{TYPE_INT_RGB => nonAlphaImage, TYPE_INT_ARGB => alphaImage}
import java.net.URL
import javax.swing.{Icon, ImageIcon}
import java.util.regex.{Pattern, Matcher}
import javax.script.{Bindings, SimpleBindings, ScriptEngineManager, Compilable, CompiledScript}

import scala.runtime.{AbstractFunction2 => Function2}

/**
 * @author Raymond Dodge
 * @version 3.0.0
 */
// class VisualizationRuleBasedRectangularTilesheet extends RectangularTilesheet
class JSONRectangularTilesheet[A](
		val name:String,
		val visualizationRules:Seq[JSONRectangularVisualizationRule[A]]
) extends RectangularTilesheet[A] {
	
	def getIconFor(field:RectangularField[_ <: A], x:Int, y:Int, rng:Random):(Icon, Icon) =
	{
		type ImageFrames = Seq[Image]
		
		val layers:Map[Int, ImageFrames] = {
			import JSONRectangularVisualizationRule.{FullOrdering, PriorityOrdering}
			
			visualizationRules.filter{
				_.matches(field, x, y, rng)
			}.toSeq.sorted(PriorityOrdering).foldLeft(Map.empty[Int, ImageFrames]){
				_ ++ _.iconParts
			}
		}
		
		val lowHighLayers = layers.partition{_._1 < 0}
		
		// assumes that all images are the same size
		def mashTogetherLayers(layers:Map[Int, ImageFrames]):ImageFrames =
		{
			val layers2:Map[Int, ImageFrames] = layers
			val layersInOrder:Seq[ImageFrames] = Vector.empty ++ layers2.toSeq.sortBy{_._1}.map{_._2}.filter{_.length > 0}
			
			object lcm extends Function2[Int, Int, Int]{
				def apply(x:Int, y:Int):Int = {
					x / gcdApply(x,y) * y	
				}
				
				@tailrec def gcdApply(x:Int, y:Int):Int = {
					if (y == 1) {x} else
					if (x == 1) {y} else
					if (x == y) {x} else
					if (x > y) {gcdApply(x, x - y)} else
					{gcdApply(y - x, x)}
				}
			}
			
			val leastCommonFrameNumber:Int = layersInOrder.map{_.length}.fold(1){lcm}
			
			// after this, all layers will have the same number of frames
			val layersWithLCMFrames:Seq[ImageFrames] = layersInOrder.map{(x:ImageFrames) =>
				Seq.fill(leastCommonFrameNumber / x.length){x}.flatten
			}
			
			if (! layersWithLCMFrames.isEmpty)
			{
				// FIXTHIS: assumes all images are the same size
				val imageWidth = layersWithLCMFrames.head.head.getWidth(null)
				val imageHeight = layersWithLCMFrames.head.head.getHeight(null)
				
				// merge all the layers in each frame into one image per frame
				val frames:ImageFrames = layersWithLCMFrames.foldLeft(
						Seq.fill(leastCommonFrameNumber){
							new BufferedImage(imageWidth, imageHeight, alphaImage)
						}
				){(newImage:Seq[BufferedImage], layer:ImageFrames) =>
						newImage.zip(layer).map({(newImage:BufferedImage, layer:Image) =>
							newImage.getGraphics.drawImage(layer, 0, 0, null)
							newImage
						}.tupled)
				}
				
				frames
			}
			else
			{
				Seq(new BufferedImage(1,1,alphaImage))
			}
		}
		
		def imageFramesToIcon(x:ImageFrames):Icon = {
			if (x.length == 1) {
				new ImageIcon(x.head)
			} else {
				new AnimationIcon(new ImageFrameAnimation(x, 1000/5, true))
			}
		}
		
		val lowHighImages = (
			imageFramesToIcon(mashTogetherLayers(lowHighLayers._1)),
			imageFramesToIcon(mashTogetherLayers(lowHighLayers._2)) 
		) 
		
		lowHighImages
	}
}

/**
 * @author Raymond Dodge
 * @version 3.0.0
 */
object JSONRectangularTilesheet
{
	import java.nio.charset.StandardCharsets.UTF_8
	import javax.imageio.ImageIO
	import java.io.InputStreamReader
	import com.rayrobdod.javaScriptObjectNotation.parser.JSONParser
	import com.rayrobdod.javaScriptObjectNotation.parser.listeners.ToScalaCollection
	import scala.collection.JavaConversions.mapAsScalaMap

	def apply[A](baseURL:URL, classMap:SpaceClassMatcherFactory[A]):JSONRectangularTilesheet[A] = {
		
		val baseMap = {
			val fileReader = new InputStreamReader(baseURL.openStream(), UTF_8)
			val listener = ToScalaCollection()
			JSONParser.parse(listener, fileReader)
			fileReader.close()
			listener.resultMap
		}
		
		val frameImage = {
			val sheetURL:URL = new URL(baseURL, baseMap("tiles").toString)
			val sheetImage:BufferedImage = ImageIO.read(sheetURL)
			val tileWidth:Int = baseMap("tileWidth").toString.toInt
			val tileHeight:Int = baseMap("tileHeight").toString.toInt
			val tilesX = sheetImage.getWidth / tileWidth
			val tilesY = sheetImage.getHeight / tileHeight
			
			new BlitzAnimImage(sheetImage, tileWidth, tileHeight, 0, tilesX * tilesY)
		}
		
		val rulesJSON = {
			val rulesURL:URL = new URL(baseURL, baseMap("rules").toString)
			val fileReader = new InputStreamReader(rulesURL.openStream(), UTF_8)
			val listener = ToScalaCollection()
			JSONParser.parse(listener, fileReader)
			fileReader.close()
			
			val rules1:Seq[Map[_,_]] = Seq.empty ++ listener.resultSeq.map{_ match {
				case x:scala.collection.Map[_,_] => Map.empty ++ x
				case x:Any => Map.empty
			}}.filterNot{_.isEmpty}
			
			rules1.map{_.map{pair:(Any,Any) => (pair._1.toString, pair._2)}}
		}
		
		
		this.apply(
			baseMap("name").toString,
			frameImage,
			classMap,
			rulesJSON
		)
	}
	
	def apply[A](
			name:String,
			frameImage:BlitzAnimImage,
			classMap:SpaceClassMatcherFactory[A],
			rulesJSON:Seq[Map[String,Any]]
	):JSONRectangularTilesheet[A] = {
		
		val rules:Seq[JSONRectangularVisualizationRule[A]] = {
			val rulesJSON2:Seq[Map[String,_]] = rulesJSON.map{_.map{
				pair:(Any,Any) => (pair._1.toString, pair._2)
			}}
			
			rulesJSON2.map{new JSONRectangularVisualizationRule(_, Seq.empty ++ frameImage.getImages, classMap)}
		}
		
		this.apply(name, rules)
	}
	
	def apply[A](
			name:String,
			rules:Seq[JSONRectangularVisualizationRule[A]]
	):JSONRectangularTilesheet[A] = {
		new JSONRectangularTilesheet(name, rules)
	}
	
}
