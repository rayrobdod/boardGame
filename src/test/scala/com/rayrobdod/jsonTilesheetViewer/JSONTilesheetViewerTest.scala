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
package com.rayrobdod.jsonTilesheetViewer

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks
import scala.collection.immutable.Seq
import com.rayrobdod.boardGame.ConstTrueSpaceClassMatcher
import com.rayrobdod.boardGame.swingView.IndexConverter
import com.rayrobdod.boardGame.swingView.NilTilesheet
import com.rayrobdod.boardGame.swingView.HashcodeColorTilesheet
import com.rayrobdod.boardGame.swingView.VisualizationRuleBasedRectangularTilesheet
import com.rayrobdod.boardGame.swingView.ParamaterizedRectangularVisualizationRule

class JSONTilesheetViewerTest extends FunSpec {
	val identityIndexConverter:IndexConverter = {x:(Int, Int) => x}
	
	describe ("allClassesInTilesheet") {
		it ("NilTilesheet returns ['']"){
			assertResult(List("")){
				JSONTilesheetViewer.allClassesInTilesheet(NilTilesheet)
			}
		}
		it ("HashcodeColorTilesheet returns ['a','b','c','d']") {
			assertResult(Seq("a", "b", "c", "d")){
				JSONTilesheetViewer.allClassesInTilesheet(new HashcodeColorTilesheet)
			}
		}
		it ("VisualizationRuleBasedRectangularTilesheet() returns []") {
			assertResult(Seq()){
				JSONTilesheetViewer.allClassesInTilesheet(new VisualizationRuleBasedRectangularTilesheet("", Seq(
					new ParamaterizedRectangularVisualizationRule(surroundingTiles = Map(identityIndexConverter -> ConstTrueSpaceClassMatcher))
				)))
			}
		}
		it ("VisualizationRuleBasedRectangularTilesheet('a') returns ['a']") {
			assertResult(Seq("a")){
				JSONTilesheetViewer.allClassesInTilesheet(new VisualizationRuleBasedRectangularTilesheet("", Seq(
					new ParamaterizedRectangularVisualizationRule(surroundingTiles = Map(identityIndexConverter -> StringSpaceClassMatcherFactory.EqualsMatcher("a")))
				)))
			}
		}
		it ("VisualizationRuleBasedRectangularTilesheet('a','b') returns ['a','b']") {
			assertResult(Seq("a","b")){
				JSONTilesheetViewer.allClassesInTilesheet(new VisualizationRuleBasedRectangularTilesheet("", Seq(
					new ParamaterizedRectangularVisualizationRule(surroundingTiles = Map(identityIndexConverter -> StringSpaceClassMatcherFactory.EqualsMatcher("a"))),
					new ParamaterizedRectangularVisualizationRule(surroundingTiles = Map(identityIndexConverter -> StringSpaceClassMatcherFactory.EqualsMatcher("b")))
				)))
			}
		}
		it ("VisualizationRuleBasedRectangularTilesheet('a','!b') returns ['a']") {
			assertResult(Seq("a")){
				JSONTilesheetViewer.allClassesInTilesheet(new VisualizationRuleBasedRectangularTilesheet("", Seq(
					new ParamaterizedRectangularVisualizationRule(surroundingTiles = Map(identityIndexConverter -> StringSpaceClassMatcherFactory.EqualsMatcher("a"))),
					new ParamaterizedRectangularVisualizationRule(surroundingTiles = Map(identityIndexConverter -> StringSpaceClassMatcherFactory.UnequalsMatcher("b")))
				)))
			}
		}
	}
}
