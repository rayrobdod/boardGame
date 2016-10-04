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
package com.rayrobdod.boardGame.swingView

import org.scalatest.FunSpec
import scala.collection.immutable.{Seq, Map}
import com.rayrobdod.json.parser.JsonParser;
import com.rayrobdod.boardGame.SpaceClassMatcher

class RectangularVisualizationRuleBuilderTest extends FunSpec {
	
	describe("RectangularVisualizationRuleBuilder + JsonParser") {
		it ("do a thing") {
			val src = """{
				"tileRand":5,
				"indexies":"(x + y) % 2 == 0",
				"surroundingSpaces":{
					"(0,0)":"a",
					"(1,1)":"b"
				}
			}"""
			val res = new JsonParser(new RectangularVisualziationRuleBuilder(Nil, MySpaceClassMatcherFactory)).parse(src)
			
			res match {
				case ParamaterizedRectangularVisualizationRule(
						MapUnapplyer(),
						5,
						indexFun,
						MapUnapplyer(
							Tuple2(IndexConverter(0,0), MySpaceClassMatcher("a")),
							Tuple2(IndexConverter(1,1), MySpaceClassMatcher("b"))
						)
				) => assert(indexFun.toString == "(((x + y) % 2) == 0)")
				case _ => fail("res does not match")
			}
		}
	}
	
	object MySpaceClassMatcherFactory extends SpaceClassMatcherFactory[String] {
		def apply(ref:String):SpaceClassMatcher[String] = {
			new MySpaceClassMatcher(ref)
		}
	}
	case class MySpaceClassMatcher(ref:String) extends SpaceClassMatcher[String] {
		def unapply(sc:String):Boolean = {ref == sc}
	}
	object IndexConverter {
		def unapply(f:Function1[(Int, Int), (Int, Int)]):Option[(Int, Int)] = {
			Option(f((0,0)))
		}
	}
	object MapUnapplyer {
		def unapplySeq[A,B](m:Map[A,B]):Option[Seq[(A,B)]] = Option(m.to[Seq])
	}
}
