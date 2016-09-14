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

import com.rayrobdod.boardGame.view
import org.scalatest.{FunSuite, FunSpec}
import scala.collection.immutable.{Seq, Map}
import com.rayrobdod.json.parser.{JsonParser, IdentityParser}
import com.rayrobdod.json.union.{StringOrInt, JsonValue}
import com.rayrobdod.boardGame.SpaceClassMatcher

class RectangularVisualizationRuleBuilderTest extends FunSpec {
	
	describe("RectangularVisualizationRuleBuilder") {
		describe ("tileRand") {
			val dut = new RectangularVisualziationRuleBuilder(Nil, MySpaceClassMatcherFactory)
			
			it ("Accepts positive integer value") {
				assertResult(4){dut.apply(dut.init, "tileRand", JsonValue(4), new IdentityParser[String, JsonValue]).right.get.tileRand}
			}
			it ("Rejects negative integer value") {
				assertResult(0){dut.apply(dut.init, "tileRand", JsonValue(-4), new IdentityParser[String, JsonValue]).left.get._2}
			}
			it ("Rejects zero value") {
				assertResult(0){dut.apply(dut.init, "tileRand", JsonValue(0), new IdentityParser[String, JsonValue]).left.get._2}
			}
			it ("Rejects float value") {
				assertResult(0){dut.apply(dut.init, "tileRand", JsonValue(1.5), new IdentityParser[String, JsonValue]).left.get._2}
			}
			it ("Rejects String value") {
				assertResult(0){dut.apply(dut.init, "tileRand", JsonValue("abc"), new IdentityParser[String, JsonValue]).left.get._2}
			}
		}
		describe ("indexies") {
			val dut = new RectangularVisualziationRuleBuilder(Nil, MySpaceClassMatcherFactory)
			
			it ("Accepts string") {
				assertResult("x + y"){dut.apply(dut.init, "indexies", JsonValue("x + y"), new IdentityParser[String, JsonValue]).right.get.indexEquation}
			}
			it ("Rejects not-string") {
				assertResult(0){dut.apply(dut.init, "indexies", JsonValue(54), new IdentityParser[String, JsonValue]).left.get._2}
			}
		}
		describe ("surroundingSpaces") {
			val dut = new RectangularVisualziationRuleBuilder(Nil, MySpaceClassMatcherFactory)
			val jsonParser = new JsonParser().mapKey[String](StringOrInt.unwrapToString)
			
			it ("Rejects primitive") {
				assertResult(0){dut.apply(dut.init, "surroundingSpaces", JsonValue(54), new IdentityParser[String, JsonValue]).left.get._2}
			}
			it ("Accepts a correctly-formatted string -> string pair") {
				val res = dut.apply[Iterable[Char]](dut.init, "surroundingSpaces", """{ "(0,0)": "abc" }""", jsonParser).right.get.surroundingTiles
				
				assert(res.size == 1)
				val ((IndexConverter(x,y), MySpaceClassMatcher(ref))) =  res.head
				assert(x == 0 && y == 0 && ref == "abc")
			}
			ignore ("Rejects a pair containing a Number value") {
				assertResult(10){ dut.apply[Iterable[Char]](dut.init, "surroundingSpaces", """{ "(0,0)": 53 }""", jsonParser).left.get._2 }
			}
			ignore ("Rejects a pair containing a nested-value") {
				assertResult(10){ dut.apply[Iterable[Char]](dut.init, "surroundingSpaces", """{ "(0,0)": [1,2,3] }""", jsonParser).left.get._2 }
			}
			ignore ("Rejects a pair containing an incorrectly-formatted key") {
				// throws instead
				assertResult(3){ dut.apply[Iterable[Char]](dut.init, "surroundingSpaces", """{ "abc": "abc" }""", jsonParser).left.get._2 }
			}
		}
		describe ("tiles") {
			val tiles = ('a' to 'z').map{_.toString}
			val dut = new RectangularVisualziationRuleBuilder(tiles, MySpaceClassMatcherFactory)
			val jsonParser = new JsonParser().mapKey[String](StringOrInt.unwrapToString)
			
			it ("Accepts a primitive int value, treating it as a not-animated tile at some negative layer") {
				val res = dut.apply(dut.init, "tiles", JsonValue(3), new IdentityParser[String, JsonValue]).right.get.iconParts
				
				assert(res.size == 1)
				val ((layer, Seq(frame))) = res.head
				assert(layer < 0 && frame == tiles(3))
			}
			it ("Accepts a sequence of int values, treating it as a multi-frame animation at some negative layer") {
				val res = dut.apply[Iterable[Char]](dut.init, "tiles", "[3,10,7]", jsonParser).right.get.iconParts
				assertResult(Map(-127 -> Seq("d", "k", "h"))){res}
			}
			ignore ("Accepts an empty sequence of int values, treating it as a zero-frame animation at some negative layer") {
				val res = dut.apply[Iterable[Char]](dut.init, "tiles", "[]", jsonParser).right.get.iconParts
				assertResult(Map(-127 -> Seq())){res}
			}
			it ("Accepts a map of intable strings to seq of int values, treating it as a multi-frame animation at the specified layers") {
				val res = dut.apply[Iterable[Char]](dut.init, "tiles", "{\"1\":[0,1],\"2\":[2,3]}", jsonParser).right.get.iconParts
				assertResult(Map(1 -> Seq("a","b"), 2 -> Seq("c", "d"))){res}
			}
			ignore ("(MAYBE) Accepts a map of intable strings to int values, treating it as a not-animated tile at the specified layers") {
				val res = dut.apply[Iterable[Char]](dut.init, "tiles", "{\"1\":1,\"2\":2}", jsonParser).right.get.iconParts
				assertResult(Map(1 -> Seq("b"), 2 -> Seq("c"))){res}
			}
			it ("Rejects a primitive string value") {
				assertResult(0){dut.apply(dut.init, "tiles", JsonValue("abc"), new IdentityParser[String, JsonValue]).left.get._2}
			}
			it ("Rejects a primitive intable string value") {
				assertResult(0){dut.apply(dut.init, "tiles", JsonValue("43"), new IdentityParser[String, JsonValue]).left.get._2}
			}
			ignore ("Rejects a sequence-of-strings") {
				// throws instead
				assertResult(0){dut.apply[Iterable[Char]](dut.init, "tiles", """["a", "b", "c"]""", jsonParser).left.get._2}
			}
		}
	}
	
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
			val builder = new RectangularVisualziationRuleBuilder(Nil, MySpaceClassMatcherFactory).mapKey[StringOrInt](StringOrInt.unwrapToString)
			val res = new JsonParser().parse(builder, src).fold({x => x}, {x => throw new IllegalArgumentException("Was primitiive" + x)}, {(s,i) => throw new java.text.ParseException(s,i)})
			
			assert (res match {
				case ParamaterizedRectangularVisualizationRule(
						MapUnapplyer(),
						5,
						"(x + y) % 2 == 0",
						MapUnapplyer(
							Tuple2(IndexConverter(0,0), MySpaceClassMatcher("a")),
							Tuple2(IndexConverter(1,1), MySpaceClassMatcher("b"))
						)
				) => true
				case _ => false
			})
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
