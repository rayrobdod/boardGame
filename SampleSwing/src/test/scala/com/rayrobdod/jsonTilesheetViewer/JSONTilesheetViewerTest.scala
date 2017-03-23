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
import com.rayrobdod.boardGame.RectangularIndex
import com.rayrobdod.boardGame.ConstTrueSpaceClassMatcher
import com.rayrobdod.boardGame.view.RectangularDimension
import com.rayrobdod.boardGame.view.IndexConverter
import com.rayrobdod.boardGame.view.NilTilesheet
import com.rayrobdod.boardGame.view.HashcodeColorTilesheet
import com.rayrobdod.boardGame.view.VisualizationRuleBasedTilesheet
import com.rayrobdod.boardGame.view.ParamaterizedVisualizationRule

class JsonTilesheetViewerTest extends FunSpec {
	val identityIndexConverter:IndexConverter[RectangularIndex] = {x:(Int, Int) => x}
	
	describe ("allClassesInTilesheet") {
		it ("NilTilesheet returns ['']"){
			assertResult(List("")){
				allClassesInTilesheet(new NilTilesheet(() => "", RectangularDimension(-1,-1)))
			}
		}
		it ("HashcodeColorTilesheet returns ['a','b','c','d']") {
			assertResult(Seq("a", "b", "c", "d")){
				allClassesInTilesheet(new HashcodeColorTilesheet({() => 1},{(a) => 1}, RectangularDimension(-1,-1)))
			}
		}
		it ("VisualizationRuleBasedTilesheet() returns []") {
			assertResult(Seq()){
				allClassesInTilesheet(new VisualizationRuleBasedTilesheet[SpaceClass, RectangularIndex, RectangularDimension, Int, Seq[Seq[Int]]](
					  ""
					, Seq(
						new ParamaterizedVisualizationRule(surroundingTiles = Map(identityIndexConverter -> ConstTrueSpaceClassMatcher))
					)
					, {a:Seq[Seq[Int]] => a}
					, RectangularDimension(-1,-1)
				))
			}
		}
		it ("VisualizationRuleBasedTilesheet('a') returns ['a']") {
			assertResult(Seq("a")){
				allClassesInTilesheet(new VisualizationRuleBasedTilesheet[SpaceClass, RectangularIndex, RectangularDimension, Int, Seq[Seq[Int]]](
					  ""
					, Seq(
						new ParamaterizedVisualizationRule(surroundingTiles = Map(identityIndexConverter -> StringSpaceClassMatcherFactory.EqualsMatcher("a")))
					)
					, {a:Seq[Seq[Int]] => a}
					, RectangularDimension(-1,-1)
				))
			}
		}
		it ("VisualizationRuleBasedTilesheet('a','b') returns ['a','b']") {
			assertResult(Seq("a","b")){
				allClassesInTilesheet(new VisualizationRuleBasedTilesheet[SpaceClass, RectangularIndex, RectangularDimension, Int, Seq[Seq[Int]]](
					  ""
					, Seq(
						new ParamaterizedVisualizationRule(surroundingTiles = Map(identityIndexConverter -> StringSpaceClassMatcherFactory.EqualsMatcher("a"))),
						new ParamaterizedVisualizationRule(surroundingTiles = Map(identityIndexConverter -> StringSpaceClassMatcherFactory.EqualsMatcher("b")))
					)
					, {a:Seq[Seq[Int]] => a}
					, RectangularDimension(-1,-1)
				))
			}
		}
		it ("VisualizationRuleBasedTilesheet('a','!b') returns ['a']") {
			assertResult(Seq("a")){
				allClassesInTilesheet(new VisualizationRuleBasedTilesheet[SpaceClass, RectangularIndex, RectangularDimension, Int, Seq[Seq[Int]]](
					  ""
					, Seq(
						new ParamaterizedVisualizationRule(surroundingTiles = Map(identityIndexConverter -> StringSpaceClassMatcherFactory.EqualsMatcher("a"))),
						new ParamaterizedVisualizationRule(surroundingTiles = Map(identityIndexConverter -> StringSpaceClassMatcherFactory.UnequalsMatcher("b")))
					)
					, {a:Seq[Seq[Int]] => a}
					, RectangularDimension(-1,-1)
				))
			}
		}
	}
}
