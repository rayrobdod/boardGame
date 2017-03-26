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
package com.rayrobdod.boardGame
package view

import scala.util.Random
import scala.collection.immutable.{Seq, Map}
import com.rayrobdod.json.builder.{Builder, PrimitiveSeqBuilder}
import com.rayrobdod.json.parser.Parser
import com.rayrobdod.json.union.{StringOrInt, JsonValue}



/**
 * A Builder of VisualizationRule
 * @version next
 * @group VisualizationRuleTilesheet
 */
final class VisualizationRuleBuilder[SpaceClass, Index, IconPart](
		  tileSeq:Seq[IconPart]
		, spaceClassUnapplier:SpaceClassMatcherFactory[SpaceClass]
		, stringToIndexConverter:Function1[String, Either[(String, Int), IndexConverter[Index]]]
		, coordFunVars:Map[Char, CoordinateFunction[Index, Int]]
) extends Builder[StringOrInt, JsonValue, ParamaterizedVisualizationRule[SpaceClass, Index, IconPart]] {
	import VisualizationRuleBuilder.ARBITRARY_NEGATIVE_VALUE
	
	override def init:ParamaterizedVisualizationRule[SpaceClass, Index, IconPart] = {
		new ParamaterizedVisualizationRule[SpaceClass, Index, IconPart]()
	}
	
	override def apply[Input](
			folding:ParamaterizedVisualizationRule[SpaceClass, Index, IconPart],
			key:StringOrInt,
			input:Input,
			parser:Parser[StringOrInt, JsonValue, Input]
	):Either[(String, Int), ParamaterizedVisualizationRule[SpaceClass, Index, IconPart]] = key match {
		case StringOrInt.Left("tileRand") => {
			parser.parsePrimitive(input).right.flatMap{_.integerToEither{value => if (value > 0) {Right(folding.copy(tileRand = value))} else {Left("tileRand may not be negative", 0)}}}
		}
		case StringOrInt.Left("indexies") => {
			parser.parsePrimitive(input).right.flatMap{_.stringToEither{exprStr =>
				new CoordinateFunctionSpecifierParser(coordFunVars).parse(exprStr).right.map{expr => folding.copy(indexEquation = expr)}
			}}
		}
		case StringOrInt.Left("surroundingSpaces") => {
			val builder = new VisualizationRuleBuilder.SurroundingSpacesBuilder(spaceClassUnapplier, stringToIndexConverter)
			parser.parse(builder, input).complex.toEither.right.map{value => folding.copy(surroundingTiles = value)}
		}
		case StringOrInt.Left("tiles") => {
			parser.parse(VisualizationRuleBuilder.IconPartsBuilder, input).fold(
				{c =>
					c.collapse(tileSeq).right.map{value => folding.copy(iconParts = value)}
				},
				{p:JsonValue => p.integerToEither{tileIndex =>
					Right(folding.copy(iconParts = Map(ARBITRARY_NEGATIVE_VALUE -> Seq(tileSeq(tileIndex.intValue)))))
				}},
				{(msg, idx) => Left((msg, idx))}
			)
		}
		case _ => Right(folding)
	}
}


/**
 * A VisualizationRule where each of the overridable methods in represented by one of the constructor parameters
 * @version next
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
}


/**
 * @group VisualizationRuleTilesheet
 */
private object VisualizationRuleBuilder {
	private final val ARBITRARY_NEGATIVE_VALUE = -127
	
	private[this] type SurroundingSpacesMap[Index, A] = Map[IndexConverter[Index], SpaceClassMatcher[A]]
	
	private class SurroundingSpacesBuilder[Index, A](
		  spaceClassUnapplier:SpaceClassMatcherFactory[A]
		, stringToIndexConverter:Function1[String, Either[(String, Int), IndexConverter[Index]]]
	) extends Builder[StringOrInt, JsonValue, Map[IndexConverter[Index], SpaceClassMatcher[A]]] {
		def init:SurroundingSpacesMap[Index, A] = Map.empty
		def apply[I](folding:SurroundingSpacesMap[Index, A], key:StringOrInt, input:I, parser:Parser[StringOrInt, JsonValue, I]):Either[(String, Int), SurroundingSpacesMap[Index, A]] = {
			val key2 = key.fold(
				stringToIndexConverter,
				{i => Left(i + " does not match pair pattern.", 0)}
			)
			val value2 = parser.parsePrimitive(input).right.flatMap{_.stringToEither{str =>
				Right(spaceClassUnapplier(str))
			}}
			
			key2.right.flatMap{key3 => value2.right.map{value3 =>
				folding + ((key3, value3))
			}}
		}
	}
	
	def stringToRectangularIndexTranslation(s:String):Either[(String, Int), IndexConverter[RectangularIndex]] = {
		import java.util.regex.Pattern
		
		val pairPattern = Pattern.compile("""\(([\+\-]?\d+),([\+\-]?\d+)\)""")
		val matcher = pairPattern.matcher(s)
		if (!matcher.matches()) {
			Left((s + " does not match pair pattern.", 0))
		} else {
			val firstStr = matcher.group(1)
			val secondStr = matcher.group(2)
			val firstInt = firstStr.toInt
			val secondInt = secondStr.toInt
			
			Right(new RectangularIndexTranslation(firstInt, secondInt))
		}
	}
	private final class RectangularIndexTranslation(dx:Int, dy:Int) extends IndexConverter[RectangularIndex] {
		def apply(xy:RectangularIndex):RectangularIndex = {
			((xy._1 + dx, xy._2 + dy))
		}
	}
	
	def stringToElongatedTriangularIndexTranslation(s:String):Either[(String, Int), IndexConverter[ElongatedTriangularIndex]] = {
		import java.util.regex.Pattern
		
		val pairPattern = Pattern.compile("""\(([\+\-]?\d+),([\+\-]?\d+)\)""")
		val matcher = pairPattern.matcher(s)
		if (!matcher.matches()) {
			Left((s + " does not match pair pattern.", 0))
		} else {
			val firstStr = matcher.group(1)
			val secondStr = matcher.group(2)
			val firstInt = firstStr.toInt
			val secondInt = secondStr.toInt
			
			Right( new IndexConverter[ElongatedTriangularIndex] {
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
		def collapse[IconPart](tileIndex:Function1[Int, IconPart]):Either[(String, Int), Map[Int, Seq[IconPart]]]
	}
	private[this] object IconPartsBuilderValueNil extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueMap(Map(x))
		def seqAppend(x:Int) = IconPartsBuilderValueSeq(Seq(x))
		def collapse[IconPart](tileIndex:Function1[Int, IconPart]) = Right(Map.empty)
	}
	private[this] final case class IconPartsBuilderValueSeq(seq:Seq[Int]) extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueFailure
		def seqAppend(x:Int) = IconPartsBuilderValueSeq(seq :+ x)
		def collapse[IconPart](tileIndex:Function1[Int, IconPart]) = Right(Map(ARBITRARY_NEGATIVE_VALUE -> seq.map{tileIndex}))
	}
	private[this] final case class IconPartsBuilderValueMap(map:Map[Int, Seq[Int]]) extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueMap(map + x)
		def seqAppend(x:Int) = IconPartsBuilderValueFailure
		def collapse[IconPart](tileIndex:Function1[Int, IconPart]) = Right(map.mapValues{_.map{tileIndex}})
	}
	private[this] object IconPartsBuilderValueFailure extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueFailure
		def seqAppend(x:Int) = IconPartsBuilderValueFailure
		def collapse[IconPart](tileIndex:Function1[Int, IconPart]) = Left(("frame indexies map parsing failed", 0))
	}
	
	
	private object IconPartsBuilder extends Builder[StringOrInt, JsonValue, IconPartsBuilderValue] {
		override def init:IconPartsBuilderValue = IconPartsBuilderValueNil
		override def apply[I](folding:IconPartsBuilderValue, key:StringOrInt, input:I, parser:Parser[StringOrInt, JsonValue, I]):Either[(String, Int), IconPartsBuilderValue] = {
			
			key.fold({(s:String) =>
				val key2 = s.toInt
				val builder = new PrimitiveSeqBuilder[Int].flatMapValue[JsonValue]{jv => jv.integerToEither{x => Right(x)}}
				parser.parse(builder, input).complex.toEither.right.map{value => folding.mapAppend(key2 -> value)}
			}, {(i:Int) =>
				parser.parsePrimitive(input).right.flatMap{_.integerToEither{value => Right(folding.seqAppend(value))}}
			})
		}
	}
}


/**
 * @version next
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
