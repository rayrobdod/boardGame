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

import org.scalatest.FunSpec
import scala.collection.immutable.{Seq, Map}
import com.rayrobdod.json.parser.{JsonParser, IdentityParser}
import com.rayrobdod.json.union.{StringOrInt, JsonValue, ParserRetVal}
import com.rayrobdod.boardGame.SpaceClassMatcher

class VisualizationRuleBuilderTest extends FunSpec {
	
	describe("VisualizationRuleBuilder") {
		describe ("tileRand") {
			val dut = new VisualizationRuleBuilder(
					  MySpaceClassMatcherFactory
					, stringToIndexConverter = VisualizationRuleBuilder.stringToRectangularIndexTranslation
					, coordFunVars = CoordinateFunctionSpecifierParser.rectangularVars
			)
			
			it ("Accepts positive integer value") {
				assert(
					dut.apply(dut.init, "tileRand", JsonValue(4), new IdentityParser[JsonValue]) match {
						case ParserRetVal.Complex(ParamaterizedVisualizationRule(_, 4, _, _)) => true
					}
				)
			}
			it ("Rejects negative integer value") {
				assert(
					dut.apply(dut.init, "tileRand", JsonValue(-4), new IdentityParser[JsonValue]) match {
						case ParserRetVal.BuilderFailure(_) => true
					}
				)
			}
			it ("Rejects zero value") {
				assert(
					dut.apply(dut.init, "tileRand", JsonValue(0), new IdentityParser[JsonValue]) match {
						case ParserRetVal.BuilderFailure(_) => true
					}
				)
			}
			it ("Rejects float value") {
				assert(
					dut.apply(dut.init, "tileRand", JsonValue(1.5), new IdentityParser[JsonValue]) match {
						case ParserRetVal.BuilderFailure(_) => true
					}
				)
			}
			it ("Rejects String value") {
				assert(
					dut.apply(dut.init, "tileRand", JsonValue("abc"), new IdentityParser[JsonValue]) match {
						case ParserRetVal.BuilderFailure(_) => true
					}
				)
			}
		}
		describe ("indexies") {
			val dut = new VisualizationRuleBuilder(
					  MySpaceClassMatcherFactory
					, stringToIndexConverter = VisualizationRuleBuilder.stringToRectangularIndexTranslation
					, coordFunVars = CoordinateFunctionSpecifierParser.rectangularVars
			)
			
			it ("Accepts string") {
				assert(
					dut.apply(dut.init, "indexies", JsonValue("x == y"), new IdentityParser[JsonValue]) match {
						case ParserRetVal.Complex(ParamaterizedVisualizationRule(_, _, equ, _)) => equ.toString == "(x == y)"
					}
				)
			}
			it ("Rejects not-string") {
				assert(
					dut.apply(dut.init, "indexies", JsonValue(54), new IdentityParser[JsonValue]) match {
						case ParserRetVal.BuilderFailure(_) => true
					}
				)
			}
		}
		describe ("surroundingSpaces") {
			val dut = new VisualizationRuleBuilder(
					  MySpaceClassMatcherFactory
					, stringToIndexConverter = VisualizationRuleBuilder.stringToRectangularIndexTranslation
					, coordFunVars = CoordinateFunctionSpecifierParser.rectangularVars
			)
			val jsonParser = new JsonParser()
			
			it ("Rejects primitive") {
				assert(
					dut.apply(dut.init, "surroundingSpaces", JsonValue(54), new IdentityParser[JsonValue]) match {
						case ParserRetVal.BuilderFailure(_) => true
					}
				)
			}
			it ("Accepts a correctly-formatted string -> string pair") {
				dut.apply(dut.init, "surroundingSpaces", CountingReader("""{ "(0,0)": "abc" }"""), jsonParser) match {
					case ParserRetVal.Complex(ParamaterizedVisualizationRule(_, _, _, MapUnapplyer((IndexConverter(0,0), MySpaceClassMatcher("abc"))))) => {
						true
					}
				}
			}
			it ("Rejects a pair containing a Number value") {
				dut.apply(dut.init, "surroundingSpaces", CountingReader("""{ "(0,0)": 53 }"""), jsonParser) match {
					case ParserRetVal.BuilderFailure(_) => true
					case _ => fail()
				}
			}
			it ("Rejects a pair containing a nested-value") {
				assert(
					dut.apply(dut.init, "surroundingSpaces", CountingReader("""{ "(0,0)": [1,2,3] }"""), jsonParser) match {
						case ParserRetVal.BuilderFailure(_) => true
						case _ => false
					}
				)
			}
			it ("Rejects a pair containing an incorrectly-formatted key") {
				assert(
					dut.apply(dut.init, "surroundingSpaces", CountingReader("""{ "abc": "abc" }"""), jsonParser) match {
						case ParserRetVal.BuilderFailure(_) => true
					}
				)
			}
		}
		describe ("tiles") {
			val dut = new VisualizationRuleBuilder(
					  MySpaceClassMatcherFactory
					, stringToIndexConverter = VisualizationRuleBuilder.stringToRectangularIndexTranslation
					, coordFunVars = CoordinateFunctionSpecifierParser.rectangularVars
			)
			
			val jsonParser = new JsonParser()
			
			it ("Accepts a primitive int value, treating it as a not-animated tile at some negative layer") {
				dut.apply(dut.init, "tiles", JsonValue(3), new IdentityParser[JsonValue]) match {
					case ParserRetVal.Complex(ParamaterizedVisualizationRule(MapUnapplyer((layer, Seq(frame))), _, _, _)) => {
						assert(layer < 0 && frame == 3)
					}
				}
			}
			it ("Accepts a sequence of int values, treating it as a multi-frame animation at some negative layer") {
				dut.apply(dut.init, "tiles", CountingReader("[3,10,7]"), jsonParser) match {
					case ParserRetVal.Complex(ParamaterizedVisualizationRule(MapUnapplyer((layer, frames)), _, _, _)) => {
						assert(layer < 0)
						assertResult(Seq(3, 10, 7)){frames}
					}
				}
			}
			ignore ("Accepts an empty sequence of int values, treating it as a zero-frame animation at some negative layer") {
				dut.apply(dut.init, "tiles", CountingReader("[]"), jsonParser) match {
					case ParserRetVal.Complex(ParamaterizedVisualizationRule(MapUnapplyer((layer, frames)), _, _, _)) => {
						assert(layer < 0)
						assertResult(Seq()){frames}
					}
				}
			}
			it ("Accepts a map of intable strings to seq of int values, treating it as a multi-frame animation at the specified layers") {
				dut.apply(dut.init, "tiles", CountingReader("""{"1":[0,1],"2":[2,3]}"""), jsonParser) match {
					case ParserRetVal.Complex(ParamaterizedVisualizationRule(MapUnapplyer((layer1, frames1), (layer2, frames2)), _, _, _)) => {
						assertResult(1){layer1}
						assertResult(Seq(0,1)){frames1}
						assertResult(2){layer2}
						assertResult(Seq(2,3)){frames2}
					}
				}
			}
			ignore ("(MAYBE) Accepts a map of intable strings to int values, treating it as a not-animated tile at the specified layers") {
				dut.apply(dut.init, "tiles", CountingReader("""{"1":1,"2":2}"""), jsonParser) match {
					case ParserRetVal.Complex(ParamaterizedVisualizationRule(MapUnapplyer((layer1, frames1), (layer2, frames2)), _, _, _)) => {
						assertResult(1){layer1}
						assertResult(Seq(1)){frames1}
						assertResult(2){layer2}
						assertResult(Seq(2)){frames2}
					}
				}
			}
			it ("Rejects a primitive string value") {
				assert(
					dut.apply(dut.init, "tiles", JsonValue("abc"), new IdentityParser[JsonValue]) match {
						case ParserRetVal.BuilderFailure(_) => true
					}
				)
			}
			it ("Rejects a primitive intable string value") {
				assert(
					dut.apply(dut.init, "tiles", JsonValue("43"), new IdentityParser[JsonValue]) match {
						case ParserRetVal.BuilderFailure(_) => true
					}
				)
			}
			it ("Rejects a sequence-of-strings") {
				assert(
					dut.apply(dut.init, "tiles", CountingReader("""["a", "b", "c"]"""), jsonParser) match {
						case ParserRetVal.BuilderFailure(_) => true
					}
				)
			}
		}
	}
	
	describe("VisualizationRuleBuilder + JsonParser") {
		it ("do a thing") {
			val src = """{
				"tileRand":5,
				"indexies":"(x + y) % 2 == 0",
				"surroundingSpaces":{
					"(0,0)":"a",
					"(1,1)":"b"
				}
			}"""
			val builder = new VisualizationRuleBuilder(
					  MySpaceClassMatcherFactory
					, stringToIndexConverter = VisualizationRuleBuilder.stringToRectangularIndexTranslation
					, coordFunVars = CoordinateFunctionSpecifierParser.rectangularVars
			).mapKey[StringOrInt](StringOrInt.unwrapToString)
			val res = new JsonParser().parse(builder, src).fold(
					  {x => x}
					, {x => throw new IllegalArgumentException("Was primitiive" + x)}
					, {x => throw new IllegalArgumentException(x.toString)}
					, {x => throw new IllegalArgumentException(x.toString)}
			)
			
			res match {
				case ParamaterizedVisualizationRule(
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
	object CountingReader {
		def apply(x:String) = new com.rayrobdod.json.parser.CountingReader(new java.io.StringReader(x))
	}
}
