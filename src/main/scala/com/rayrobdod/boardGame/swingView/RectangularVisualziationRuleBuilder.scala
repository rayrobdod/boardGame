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
package swingView

import scala.util.Random
import scala.collection.immutable.{Seq, Map}
import java.awt.Image
import com.rayrobdod.json.builder.{Builder, PrimitiveSeqBuilder}
import com.rayrobdod.json.parser.Parser
import com.rayrobdod.json.union.{StringOrInt, JsonValue}
import view.{CoordinateFunctionSpecifierParser => coordinateFunctionParser}
import view.CoordinateFunctionSpecifierParser.CoordinateFunction



/**
 * A Builder of RectangularVisualizationRule
 * @version next
 */
class RectangularVisualziationRuleBuilder[A](
		tileSeq:Seq[Image],
		spaceClassUnapplier:SpaceClassMatcherFactory[A]
) extends Builder[StringOrInt, JsonValue, ParamaterizedRectangularVisualizationRule[A]] {
	import RectangularVisualziationRuleBuilder.ARBITRARY_NEGATIVE_VALUE
	
	def init:ParamaterizedRectangularVisualizationRule[A] = new ParamaterizedRectangularVisualizationRule[A]()
	def apply[I](a:ParamaterizedRectangularVisualizationRule[A], key:StringOrInt, input:I, parser:Parser[StringOrInt, JsonValue, I]):Either[(String, Int), ParamaterizedRectangularVisualizationRule[A]] = key match {
		case StringOrInt.Left("tileRand") => {
			parser.parsePrimitive(input).right.flatMap{_.integerToEither{value => Right(a.copy(tileRand = value))}}
		}
		case StringOrInt.Left("indexies") => {
			parser.parsePrimitive(input).right.flatMap{_.stringToEither{exprStr =>
				coordinateFunctionParser.parse(exprStr).right.map{expr => a.copy(indexEquation = expr)}
			}}
		}
		case StringOrInt.Left("surroundingSpaces") => {
			val builder = new RectangularVisualziationRuleBuilder.SurroundingSpacesBuilder(spaceClassUnapplier)
			parser.parse(builder, input).complex.toEither.right.map{value => a.copy(surroundingTiles = value)}
		}
		case StringOrInt.Left("tiles") => {
			parser.parse(RectangularVisualziationRuleBuilder.IconPartsBuilder, input).fold(
				{c =>
					c.collapse(tileSeq).right.map{value => a.copy(iconParts = value)}
				},
				{p:JsonValue => p.integerToEither{tileIndex =>
					Right(a.copy(iconParts = Map(ARBITRARY_NEGATIVE_VALUE -> Seq(tileSeq(tileIndex.intValue)))))
				}},
				{(msg, idx) => Left((msg, idx))}
			)
		}
		case _ => Right(a)
	}
}


/**
 * A RectangularVisualizationRule where each of the overridable methods in represented by one of the constructor parameters
 * @version next
 */
final case class ParamaterizedRectangularVisualizationRule[A] (
	override val iconParts:Map[Int, Seq[Image]] = Map.empty[Int, Seq[Image]],
	tileRand:Int = 1,
	indexEquation:CoordinateFunction[Boolean] = CoordinateFunction.constant(true),
	surroundingTiles:Map[IndexConverter, SpaceClassMatcher[A]] = Map.empty[IndexConverter, SpaceClassMatcher[A]]
) extends RectangularVisualizationRule[A] {
	override def indexiesMatch(x:Int, y:Int, width:Int, height:Int):Boolean = {
		indexEquation.apply(x, y, width, height)
	}
	
	override def surroundingTilesMatch(field:RectangularField[_ <: A], x:Int, y:Int):Boolean = {
		
		surroundingTiles.forall({(conversion:IndexConverter, scc:SpaceClassMatcher[A]) =>
			val newIndexies = conversion( ((x,y)) )
			if (field.contains((newIndexies._1, newIndexies._2)))
			{
				val spaceClass = field(newIndexies._1, newIndexies._2).typeOfSpace
				
				scc.unapply(spaceClass)
			} else {true}
		}.tupled)
	}
	
	override def randsMatch(rng:Random):Boolean = {
		rng.nextInt(tileRand) == 0
	}
	
	final override def priority:Int = {
		surroundingTiles.size * 10000 + tileRand + indexEquation.priority
	}
}


private object RectangularVisualziationRuleBuilder {
	private final val ARBITRARY_NEGATIVE_VALUE = -127
	
	private[this] type SurroundingSpacesMap[A] = Map[IndexConverter, SpaceClassMatcher[A]]
	
	private class SurroundingSpacesBuilder[A](
		spaceClassUnapplier:SpaceClassMatcherFactory[A]
	) extends Builder[StringOrInt, JsonValue, Map[IndexConverter, SpaceClassMatcher[A]]] {
		def init:SurroundingSpacesMap[A] = Map.empty
		def apply[I](folding:SurroundingSpacesMap[A], key:StringOrInt, input:I, parser:Parser[StringOrInt, JsonValue, I]):Either[(String, Int), SurroundingSpacesMap[A]] = {
			val key2 = key.fold(
				{s => asIndexTranslationFunction(s)},
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
	
	def asIndexTranslationFunction(s:String):Either[(String, Int), IndexConverter] = {
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
			
			Right(new Function1[(Int, Int), (Int, Int)]{
				def apply(x:(Int, Int)):(Int, Int) = 
					((x._1 + firstInt, x._2 + secondInt))
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
		def collapse(tileIndex:Function1[Int, Image]):Either[(String, Int), Map[Int, Seq[Image]]]
	}
	private[this] object IconPartsBuilderValueNil extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueMap(Map(x))
		def seqAppend(x:Int) = IconPartsBuilderValueSeq(Seq(x))
		def collapse(tileIndex:Function1[Int, Image]) = Right(Map.empty)
	}
	private[this] final case class IconPartsBuilderValueSeq(seq:Seq[Int]) extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueFailure
		def seqAppend(x:Int) = IconPartsBuilderValueSeq(x +: seq)
		def collapse(tileIndex:Function1[Int, Image]) = Right(Map(ARBITRARY_NEGATIVE_VALUE -> seq.map{tileIndex}))
	}
	private[this] final case class IconPartsBuilderValueMap(map:Map[Int, Seq[Int]]) extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueMap(map + x)
		def seqAppend(x:Int) = IconPartsBuilderValueFailure
		def collapse(tileIndex:Function1[Int, Image]) = Right(map.mapValues{_.map{tileIndex}})
	}
	private[this] object IconPartsBuilderValueFailure extends IconPartsBuilderValue {
		def mapAppend(x:(Int, Seq[Int])) = IconPartsBuilderValueFailure
		def seqAppend(x:Int) = IconPartsBuilderValueFailure
		def collapse(tileIndex:Function1[Int, Image]) = Left(("frame indexies map parsing failed", 0))
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
 */
private[swingView] object JSONRectangularVisualizationRule
{
	def PriorityOrdering:Ordering[RectangularVisualizationRule[_]] = {
		Ordering.by[RectangularVisualizationRule[_], Int]{(x:RectangularVisualizationRule[_]) => x.priority}
	}

	object FullOrdering extends Ordering[ParamaterizedRectangularVisualizationRule[_]] {
		def compare(x:ParamaterizedRectangularVisualizationRule[_], y:ParamaterizedRectangularVisualizationRule[_]):Int = {
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
