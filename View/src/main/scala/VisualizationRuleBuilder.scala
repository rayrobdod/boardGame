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
package com.rayrobdod.boardGame
package view

import scala.util.Random
import scala.collection.immutable.{Seq, Map}
import com.rayrobdod.json.builder.{Builder, PrimitiveSeqBuilder}
import com.rayrobdod.json.parser.Parser
import com.rayrobdod.json.union.{StringOrInt, JsonValue, ParserRetVal}


/**
 * A Builder of VisualizationRule
 * @group VisualizationRuleTilesheet
 */
final class VisualizationRuleBuilder[SpaceClass, Index](
		  spaceClassUnapplier:SpaceClassMatcherFactory[SpaceClass]
		, stringToIndexConverter:Function1[String, Option[IndexConverter[Index]]]
		, coordFunVars:Map[Char, CoordinateFunction[Index, Int]]
) extends Builder[StringOrInt, JsonValue, VisualizationRuleBuilderFailure, ParamaterizedVisualizationRule[SpaceClass, Index, Int]] {
	import VisualizationRuleBuilder.ARBITRARY_NEGATIVE_VALUE
	
	type IconPart = Int
	override type Middle = ParamaterizedVisualizationRule[SpaceClass, Index, IconPart]
	
	override def init:ParamaterizedVisualizationRule[SpaceClass, Index, IconPart] = {
		new ParamaterizedVisualizationRule[SpaceClass, Index, IconPart]()
	}
	
	override def apply[Input, PF, BFE](
			folding:ParamaterizedVisualizationRule[SpaceClass, Index, IconPart],
			key:StringOrInt,
			input:Input,
			parser:Parser[StringOrInt, JsonValue, PF, BFE, Input],
			bfe:BFE
	):ParserRetVal[ParamaterizedVisualizationRule[SpaceClass, Index, IconPart], Nothing, PF, VisualizationRuleBuilderFailure, BFE] = key match {
		case StringOrInt.Left("tileRand") => {
			parser.parsePrimitive(input, ExpectedPrimitive)
				.primitive.flatMap{
					_.ifIsInteger({value =>
						if (value > 0) {ParserRetVal.Complex(folding.copy(tileRand = value))}
						else {ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(value, "Unsigned Integer"), bfe)}
					}, {jsonValue =>
						ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(jsonValue, "Unsigned Integer"), bfe)
					})
			}
		}
		case StringOrInt.Left("indexies") => {
			parser.parsePrimitive(input, ExpectedPrimitive)
				.primitive.flatMap{
					_.ifIsString({exprStr =>
						new CoordinateFunctionSpecifierParser(coordFunVars).parse(exprStr).fold(
							  {x => ParserRetVal.BuilderFailure(x, bfe)}
							, {expr => ParserRetVal.Complex(folding.copy(indexEquation = expr))}
						)
					}, {jsonValue =>
						ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(jsonValue, "String"), bfe)
					})
				}
		}
		case StringOrInt.Left("surroundingSpaces") => {
			val builder = new VisualizationRuleBuilder.SurroundingSpacesBuilder(spaceClassUnapplier, stringToIndexConverter)
			parser.parse(builder, input)
				.primitive.flatMap{value => ParserRetVal.BuilderFailure(ExpectedComplex, bfe)}
				.complex.map{value => folding.copy(surroundingTiles = value)}
		}
		case StringOrInt.Left("tiles") => {
			parser.parse(VisualizationRuleBuilder.IconPartsBuilder, input)
					.complex.map{value => folding.copy(iconParts = value)}
					.primitive.flatMap{_.ifIsInteger(
						  {tileIndex => ParserRetVal.Complex(folding.copy(iconParts = Map(ARBITRARY_NEGATIVE_VALUE -> Seq(tileIndex))))}
						, {other => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(other, "Integer"), bfe)}
					)}
		}
		case _ => ParserRetVal.Complex(folding)
	}
	
	override def finish[BFE](bfe:BFE)(x:Middle) = ParserRetVal.Complex(x)
}


/**
 * A VisualizationRule where each of the overridable methods in represented by one of the constructor parameters
 * @group VisualizationRuleTilesheet
 */
final case class ParamaterizedVisualizationRule[SpaceClass, Index, IconPart] (
	override val iconParts:Map[Int, Seq[IconPart]] = Map.empty[Int, Seq[IconPart]],
	tileRand:Int = 1,
	indexEquation:CoordinateFunction[Index, Boolean] = CoordinateFunction.constant(true),
	surroundingTiles:Map[IndexConverter[Index], SpaceClassMatcher[SpaceClass]] = Map.empty[IndexConverter[Index], SpaceClassMatcher[SpaceClass]]
) extends VisualizationRule[SpaceClass, Index, IconPart] {
	
	override def indexiesMatch(xy:Index):Boolean = {
		indexEquation.apply(xy)
	}
	
	override def surroundingTilesMatch(field:Tiling[_ <: SpaceClass, Index, _], xy:Index):Boolean = {
		
		surroundingTiles.forall({(conversion:IndexConverter[Index], scc:SpaceClassMatcher[SpaceClass]) =>
			val newIndexies = conversion( xy )
			field.spaceClass(newIndexies).map{spaceClass =>
				scc.unapply(spaceClass)
			}.getOrElse(true)
		}.tupled)
	}
	
	override def randsMatch(rng:Random):Boolean = {
		rng.nextInt(tileRand) == 0
	}
	
	final override def priority:Int = {
		surroundingTiles.size * 10000 + tileRand + indexEquation.priority
	}
	
	def mapIconPart[A](x: IconPart => A):ParamaterizedVisualizationRule[SpaceClass, Index, A] = {
		new ParamaterizedVisualizationRule(
			  this.iconParts.mapValues{_.map{x}}
			, tileRand
			, indexEquation
			, surroundingTiles
		)
	}
}


/**
 * @group VisualizationRuleTilesheet
 */
private object VisualizationRuleBuilder {
	private final val ARBITRARY_NEGATIVE_VALUE = -127
	
	private[this] type SurroundingSpacesMap[Index, A] = Map[IndexConverter[Index], SpaceClassMatcher[A]]
	
	private class SurroundingSpacesBuilder[Index, A](
		  spaceClassUnapplier:SpaceClassMatcherFactory[A]
		, stringToIndexConverter:Function1[String, Option[IndexConverter[Index]]]
	) extends Builder[StringOrInt, JsonValue, VisualizationRuleBuilderFailure, Map[IndexConverter[Index], SpaceClassMatcher[A]]] {
		override type Middle = Map[IndexConverter[Index], SpaceClassMatcher[A]]
		override def init:SurroundingSpacesMap[Index, A] = Map.empty
		override def apply[I, PF, BFE](
				folding:SurroundingSpacesMap[Index, A], key:StringOrInt, input:I, parser:Parser[StringOrInt, JsonValue, PF, BFE, I], bfe:BFE
		):ParserRetVal[Middle, Nothing, PF, VisualizationRuleBuilderFailure, BFE] = {
			
			val key2 = key.fold(stringToIndexConverter, {i => None})
			
			val value2 = parser.parsePrimitive(input, ExpectedPrimitive)
				.primitive.flatMap{_.ifIsString(
					  {str => ParserRetVal.Complex(spaceClassUnapplier(str))}
					, {value => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(value, "String"), bfe)}
				)}
			
			key2.map{key3 => value2.complex.map{value3 =>
				folding + ((key3, value3))
			}}.getOrElse{
				ParserRetVal.BuilderFailure(SurroundingSpacesMapKeyNotDeltaIndex, bfe)
			}
		}
		override def finish[BFE](bfe:BFE)(x:Middle) = ParserRetVal.Complex(x)
	}
	
	def stringToRectangularIndexTranslation(s:String):Option[IndexConverter[RectangularIndex]] = {
		import java.util.regex.Pattern
		
		val pairPattern = Pattern.compile("""\(([\+\-]?\d+),([\+\-]?\d+)\)""")
		val matcher = pairPattern.matcher(s)
		if (!matcher.matches()) {
			None
		} else {
			val firstStr = matcher.group(1)
			val secondStr = matcher.group(2)
			val firstInt = firstStr.toInt
			val secondInt = secondStr.toInt
			
			Option(new RectangularIndexTranslation(firstInt, secondInt))
		}
	}
	private final class RectangularIndexTranslation(dx:Int, dy:Int) extends IndexConverter[RectangularIndex] {
		def apply(xy:RectangularIndex):RectangularIndex = {
			((xy._1 + dx, xy._2 + dy))
		}
	}
	
	def stringToElongatedTriangularIndexTranslation(s:String):Option[IndexConverter[ElongatedTriangularIndex]] = {
		import java.util.regex.Pattern
		
		val pairPattern = Pattern.compile("""\(([\+\-]?\d+),([\+\-]?\d+)\)""")
		val matcher = pairPattern.matcher(s)
		if (!matcher.matches()) {
			None
		} else {
			val firstStr = matcher.group(1)
			val secondStr = matcher.group(2)
			val firstInt = firstStr.toInt
			val secondInt = secondStr.toInt
			
			Option( new IndexConverter[ElongatedTriangularIndex] {
				def apply(input:ElongatedTriangularIndex):ElongatedTriangularIndex = {
					val ElongatedTriangularIndex(inX, inY, inT) = input
					
					// TODO: inT deltas
					new ElongatedTriangularIndex(inX + firstInt, inY + secondInt, inT)
				}
			})
		}
	}
	
	/**
	 * Represents a state that can collect either `Int`s into a `Seq[Int]` or `(Int, Seq[Int])`
	 * into a `Map[Int, Seq[Int]]`, but cannot change which one it is collecting   
	 * 
	 * Implemented as a sealed hierarchy of states.
	   - Start at [[IconPartsBuilderValueNil]].
	   - Call mapAppend or seqAppend to advance.
	   - Call collapse to exit with a result.
	   - IconPartsBuilderValueSeq or IconPartsBuilderValueMap is a success; IconPartsBuilderValueFailure is a failure
	 */
	private sealed trait IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])):IconPartsBuilderValue
		def seqAppend(x:Int):IconPartsBuilderValue
		def result:Either[IconPartWasInconsistent.type, Map[Int, Seq[Int]]]
	}
	private[this] object IconPartsBuilderValueNil extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueMap(Map(x))
		def seqAppend(x:Int) = IconPartsBuilderValueSeq(Seq(x))
		def result = Right(Map.empty)
	}
	private[this] final case class IconPartsBuilderValueSeq(seq:Seq[Int]) extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueFailure
		def seqAppend(x:Int) = IconPartsBuilderValueSeq(seq :+ x)
		def result = Right(Map(ARBITRARY_NEGATIVE_VALUE -> seq))
	}
	private[this] final case class IconPartsBuilderValueMap(map:Map[Int, Seq[Int]]) extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueMap(map + x)
		def seqAppend(x:Int) = IconPartsBuilderValueFailure
		def result = Right(map)
	}
	private[this] object IconPartsBuilderValueFailure extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueFailure
		def seqAppend(x:Int) = IconPartsBuilderValueFailure
		def result = Left(IconPartWasInconsistent)
	}
	
	
	private object IconPartsBuilder extends Builder[StringOrInt, JsonValue, VisualizationRuleBuilderFailure, Map[Int, Seq[Int]]] {
		override type Middle = IconPartsBuilderValue
		override def init:IconPartsBuilderValue = IconPartsBuilderValueNil
		override def apply[I, PF, BFE](
			folding:IconPartsBuilderValue, key:StringOrInt, input:I, parser:Parser[StringOrInt, JsonValue, PF, BFE, I], bfe:BFE
		):ParserRetVal[IconPartsBuilderValue, Nothing, PF, VisualizationRuleBuilderFailure, BFE] = {
			
			key.fold({(s:String) =>
				val keyOpt = try {
					Option(s.toInt)
				} catch {
					case ex:NumberFormatException => None
				}
				
				keyOpt.map{key2 =>
					val builder:Builder[StringOrInt, JsonValue, VisualizationRuleBuilderFailure, Seq[Int]] = {
						new PrimitiveSeqBuilder[Int, VisualizationRuleBuilderFailure](ExpectedPrimitive)
							.flatMapValue[JsonValue, VisualizationRuleBuilderFailure]{jv =>
								jv.ifIsInteger(
									  {x => Right(x)}
									, {x => Left(UnsuccessfulTypeCoercion(x, "Integer"))}
								)
							}
							.mapFailure{_.fold({x => x}, {x => x})}
					}
					parser.parse(builder, input)
							.complex.map{value => folding.mapAppend(key2 -> value)}
							.primitive.flatMap{x => ParserRetVal.BuilderFailure(ExpectedComplex, bfe)}
				}.getOrElse{
					ParserRetVal.BuilderFailure(IconPartMapKeyNotIntegerConvertable(s), bfe)
				}
			}, {(i:Int) =>
				parser.parsePrimitive[ExpectedPrimitive.type](input, ExpectedPrimitive)
						.primitive.flatMap{_.ifIsInteger(
							  {value => ParserRetVal.Complex(folding.seqAppend(value))}
							, {x => ParserRetVal.BuilderFailure(UnsuccessfulTypeCoercion(x, "Integer"), bfe)}
						)}
			})
		}
		override def finish[BFE](bfe:BFE)(x:Middle):ParserRetVal[Map[Int, Seq[Int]], Nothing, Nothing, VisualizationRuleBuilderFailure, BFE] = {
			x.result.fold({ParserRetVal.BuilderFailure(_, bfe)}, {ParserRetVal.Complex(_)})
		}
	}

}


/**
 * @group VisualizationRuleTilesheet
 */
private[view] object ParamaterizedVisualizationRule {
	
	def PriorityOrdering:Ordering[VisualizationRule[_,_,_]] = {
		Ordering.by[VisualizationRule[_,_,_], Int]{(x:VisualizationRule[_,_,_]) => x.priority}
	}

	object FullOrdering extends Ordering[ParamaterizedVisualizationRule[_,_,_]] {
		def compare(x:ParamaterizedVisualizationRule[_,_,_], y:ParamaterizedVisualizationRule[_,_,_]):Int = {
			(x.tileRand compareTo y.tileRand) match {
				case 0 => (x.indexEquation.toString compareTo y.indexEquation.toString) match {
					case 0 => IterableIntOrdering.compare(x.iconParts.keys, y.iconParts.keys) match {
						case i => i
					}
					case i => i
				}
				case i => i
			}
		}
	}
	
	private object IterableIntOrdering extends Ordering[Iterable[Int]] {
		def compare(x:Iterable[Int], y:Iterable[Int]):Int = {
			x.zip(y).foldLeft(0){(i:Int, xy:(Int,Int)) =>
				if (i == 0) {
					xy._1 compareTo xy._2
				} else {i}
			}
		}
	}
}
