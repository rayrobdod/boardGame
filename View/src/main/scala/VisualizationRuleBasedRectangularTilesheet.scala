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
package com.rayrobdod.boardGame.view

import com.rayrobdod.boardGame._
import scala.util.Random
import scala.{Function0 => Future}
import scala.annotation.tailrec
import scala.collection.immutable.{Seq, Map, Vector, Set, SortedMap}


/**
 * A tilesheet that aggregates RectangularVisualizationRule and acts based on those rules
 * @version 3.0.0
 */
final class VisualizationRuleBasedRectangularTilesheet[SpaceClass, IconPart, Icon](
		override val name:String,
		val visualizationRules:Seq[view.RectangularVisualizationRule[SpaceClass, IconPart]],
		compostLayers:Function1[Seq[Seq[IconPart]], Icon]
) extends RectangularTilesheet[SpaceClass, Icon] {
	
	override def getIconFor(field:RectangularTiling[_ <: SpaceClass], x:Int, y:Int, rng:Random):(Icon, Icon) = {
		type ImageFrames = Seq[IconPart]
		
		val layers:Map[Int, ImageFrames] = {
			import ParamaterizedRectangularVisualizationRule.PriorityOrdering
			
			visualizationRules.filter{
				_.matches(field, x, y, rng)
			}.toSeq.sorted(PriorityOrdering).foldLeft(Map.empty[Int, ImageFrames]){
				_ ++ _.iconParts
			}
		}
		
		val lowHighLayers = layers.partition{_._1 < 0}
		
		// assumes that all images are the same size
		def mashTogetherLayers(layers:Map[Int, ImageFrames]):Icon = {
			val layers2:Map[Int, ImageFrames] = layers
			val layersInOrder:Seq[ImageFrames] = Vector.empty ++ layers2.toSeq.sortBy{_._1}.map{_._2}.filter{_.length > 0}
			
			val leastCommonFrameNumber:Int = layersInOrder.map{_.length}.fold(1){lcm}
			
			// after this, all layers will have the same number of frames
			val layersWithLCMFrames:Seq[ImageFrames] = layersInOrder.map{(x:ImageFrames) =>
				Seq.fill(leastCommonFrameNumber / x.length){x}.flatten
			}
			
			compostLayers(layersWithLCMFrames)
		}
		
		val lowHighImages = (
			mashTogetherLayers(lowHighLayers._1),
			mashTogetherLayers(lowHighLayers._2) 
		) 
		
		lowHighImages
	}
}