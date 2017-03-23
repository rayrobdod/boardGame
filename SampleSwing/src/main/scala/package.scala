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
package com.rayrobdod

import scala.collection.immutable.Seq

/**
 * 
 */
package object jsonTilesheetViewer {
	type SpaceClass = String
	
	/** @since next */
	val TAG_MAP_ROTATE:String = "tag:rayrobdod.name,2013-08:map-rotate"
	
	/** @since next */
	val TAG_SHEET_NIL:String = "tag:rayrobdod.name,2013-08:tilesheet-nil"
	/** @since next */
	val TAG_SHEET_INDEX:String = "tag:rayrobdod.name,2013-08:tilesheet-indexies"
	/** @since next */
	val TAG_SHEET_RAND:String = "tag:rayrobdod.name,2013-08:tilesheet-randcolor"
	/** @since next */
	val TAG_SHEET_HASH:String = "tag:rayrobdod.name,2015-06-12:tilesheet-hashcolor"
	/** @since next */
	val TAG_SHEET_CHECKER:String = "tag:rayrobdod.name,2013-08:tilesheet-checker"
	
	
	
	def allClassesInTilesheet(f:com.rayrobdod.boardGame.view.Tilesheet[SpaceClass, _, _, _]):Seq[SpaceClass] = {
		import com.rayrobdod.boardGame.SpaceClassMatcher
		import com.rayrobdod.boardGame.view.ParamaterizedVisualizationRule
		import com.rayrobdod.boardGame.view.VisualizationRuleBasedTilesheet
		import com.rayrobdod.boardGame.view.HashcodeColorTilesheet
		import StringSpaceClassMatcherFactory.EqualsMatcher
		
		val a = f match {
			case x:VisualizationRuleBasedTilesheet[SpaceClass, _, _, _, _] => {
				val a:Seq[ParamaterizedVisualizationRule[SpaceClass, _, _]] = x.visualizationRules.map{_.asInstanceOf[ParamaterizedVisualizationRule[SpaceClass, _, _]]}
				val b:Seq[Map[_, SpaceClassMatcher[SpaceClass]]] = a.map{_.surroundingTiles}
				val c:Seq[Seq[SpaceClassMatcher[SpaceClass]]] = b.map{(a) => (Seq.empty ++ a.toSeq).map{_._2}}
				val d:Seq[SpaceClassMatcher[SpaceClass]] = c.flatten
				
				val e:Seq[Option[SpaceClass]] = d.map{_ match {
					case EqualsMatcher(ref) => Option(ref)
					case _ => None
				}}
				val f:Seq[SpaceClass] = e.flatten.distinct
				
				f
			}
			// designed to be one of each color // green, blue, red, white
			//case x:HashcodeColorTilesheet[SpaceClass] => Seq("AWv", "Ahf", "\u43c8\u0473\u044b", "")
			case x:HashcodeColorTilesheet[_, _, _] => Seq("a", "b", "c", "d")
			case _ => Seq("")
		}
		
		a
	}
}
