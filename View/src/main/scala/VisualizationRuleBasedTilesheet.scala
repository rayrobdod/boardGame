/*
	Deduction Tactics
	Copyright (C) 2012-2017  Raymond Dodge

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
import scala.collection.immutable.{Seq, Map, Vector}


/**
 * A tilesheet that aggregates RectangularVisualizationRule and acts based on those rules
 * @group VisualizationRuleTilesheet
 */
final class VisualizationRuleBasedTilesheet[SpaceClass, Index, Dimension, IconPart, Icon](
		  name:String
		, val visualizationRules:Seq[view.VisualizationRule[SpaceClass, Index, IconPart]]
		, compostLayers:Function1[Seq[Seq[IconPart]], Icon]
		, override val iconDimensions:Dimension
) extends Tilesheet[SpaceClass, Index, Dimension, Icon] {
	
	override def getIconFor(field:Tiling[_ <: SpaceClass, Index, _], xy:Index, rng:Random):(Icon, Icon) = {
		type ImageFrames = Seq[IconPart]
		
		val layers:Map[Int, ImageFrames] = {
			import ParamaterizedVisualizationRule.PriorityOrdering
			
			visualizationRules.filter{
				_.matches(field, xy, rng)
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
