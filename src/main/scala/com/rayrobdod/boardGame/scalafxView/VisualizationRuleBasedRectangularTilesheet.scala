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
package com.rayrobdod.boardGame.javafxView

import com.rayrobdod.boardGame._
import scala.util.Random
import scala.{Function0 => Future}
import scala.annotation.tailrec
import scala.collection.immutable.{Seq, Map, Vector, Set, SortedMap}
import scala.collection.mutable.{Map => MMap}
import java.net.URL
import java.util.regex.{Pattern, Matcher}
import javax.script.{Bindings, SimpleBindings, ScriptEngineManager, Compilable, CompiledScript}
import javafx.scene.Node
import javafx.scene.image.{Image, ImageView, WritableImage}

/**
 * @version 3.0.0
 */
final case class VisualizationRuleBasedRectangularTilesheet[A](
		override val name:String,
		val visualizationRules:Seq[RectangularVisualizationRule[A]]
) extends RectangularTilesheet[A] {
	
	override def getImageFor(field:RectangularField[_ <: A], x:Int, y:Int, rng:Random):(Node, Node) =
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
			
			val leastCommonFrameNumber:Int = layersInOrder.map{_.length}.fold(1){lcm}
			
			// after this, all layers will have the same number of frames
			val layersWithLCMFrames:Seq[ImageFrames] = layersInOrder.map{(x:ImageFrames) =>
				Seq.fill(leastCommonFrameNumber / x.length){x}.flatten
			}
			
			if (! layersWithLCMFrames.isEmpty)
			{
				// FIXTHIS: assumes all images are the same size
				val imageWidth = layersWithLCMFrames.head.head.getWidth.intValue
				val imageHeight = layersWithLCMFrames.head.head.getHeight.intValue
				
				// merge all the layers in each frame into one image per frame
				val frames:ImageFrames = layersWithLCMFrames.foldLeft(
						Seq.fill(leastCommonFrameNumber){
							new WritableImage(imageWidth, imageHeight)
						}
				){(newImage:Seq[WritableImage], layer:ImageFrames) =>
						newImage.zip(layer).map({(newImage:WritableImage, layer:Image) =>
							newImage.getPixelWriter().setPixels(
									0, 0,
									imageWidth, imageHeight,
									layer.getPixelReader,
									0, 0
							)
							newImage
						}.tupled)
				}
				
				frames
			}
			else
			{
				Seq(new WritableImage(1,1))
			}
		}
		
		def imageFramesToIcon(x:ImageFrames):Node = {
			if (x.length == 1) {
				new ImageView(x.head)
			} else {
				// TODO: Animation
				new ImageView(x.headOption.getOrElse(new WritableImage(1,1)))
			}
		}
		
		val lowHighImages = (
			imageFramesToIcon(mashTogetherLayers(lowHighLayers._1)),
			imageFramesToIcon(mashTogetherLayers(lowHighLayers._2)) 
		) 
		
		lowHighImages
	}
}

