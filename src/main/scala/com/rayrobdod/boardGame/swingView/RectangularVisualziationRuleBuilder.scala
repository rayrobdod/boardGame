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
import com.rayrobdod.json.builder.{Builder, MapBuilder}
import JSONRectangularVisualizationRule.{asMapOfFrameIndexies, asIndexTranslationFunction}
import view.{CoordinateFunctionSpecifierParser => coordinateFunctionParser}
import view.CoordinateFunctionSpecifierParser.CoordinateFunction



/**
 * A Builder of RectangularVisualizationRule
 * @version next
 */
class RectangularVisualziationRuleBuilder[A](
		tileSeq:Seq[Image],
		spaceClassUnapplier:SpaceClassMatcherFactory[A]
) extends Builder[ParamaterizedRectangularVisualizationRule[A]] {
	def init:ParamaterizedRectangularVisualizationRule[A] = new ParamaterizedRectangularVisualizationRule[A]()
	def apply(a:ParamaterizedRectangularVisualizationRule[A], key:String, value:Any):ParamaterizedRectangularVisualizationRule[A] = key match {
		case "tileRand" => a.copy(tileRand = value.asInstanceOf[Long].intValue) 
		case "indexies" => a.copy(indexEquation = coordinateFunctionParser.parse(value.toString).left.map{case (msg, idx) => throw new java.text.ParseException(msg, idx)}.merge)
		case "surroundingSpaces" => 
			a.copy(surroundingTiles = value.asInstanceOf[Map[_,_]].map{(x:(Any,Any)) => 
				(( asIndexTranslationFunction(x._1.toString), spaceClassUnapplier(x._2.toString) ))
			})
		case "tiles" => a.copy(iconParts = asMapOfFrameIndexies(value).mapValues{_.map{tileSeq}})
		case _ => a
	}
	def childBuilder(key:String):Builder[_] = new MapBuilder
	override val resultType:Class[ParamaterizedRectangularVisualizationRule[A]] = classOf[ParamaterizedRectangularVisualizationRule[A]]
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


/**
 * @version next
 */
private[swingView] object JSONRectangularVisualizationRule
{
	private[this] def asInt(x:Any):Int = x match {
		case y:Int => y
		case y:String => Integer.parseInt(y)
		case y:Integer => y
		case y:Long => y.intValue
	}
	
	def asMapOfFrameIndexies(frameIndexies:Any):Map[Int, Seq[Int]] =
	{
		val ARBITRARY_NEGATIVE_VALUE = -127
		
		val normalizedFrameIndex:Map[Int, Seq[Int]] = frameIndexies match {
			case x:Int => Map(ARBITRARY_NEGATIVE_VALUE → Seq(x))
			case x:Long => Map(ARBITRARY_NEGATIVE_VALUE → Seq(x.intValue))
			case x:Seq[_] => Map(ARBITRARY_NEGATIVE_VALUE → x.map{asInt(_)})
			case x:Map[_, _] => {
				x.map{(y:Tuple2[_,_]) => (( asInt(y._1), y._2)) }
						.mapValues{_ match {
							case y:Int => Seq(y)
							case y:Long => Seq(y.intValue)
							case y:Seq[_] => y.map{asInt(_)}
						}}
			}
		}
		
		normalizedFrameIndex
	}
	
	def asIndexTranslationFunction(s:String):Function1[(Int, Int), (Int, Int)] =
	{
		import java.util.regex.Pattern
		
		val pairPattern = Pattern.compile("""\(([\+\-]?\d+),([\+\-]?\d+)\)""")
		val matcher = pairPattern.matcher(s)
		if (!matcher.matches()) {
			throw new IllegalArgumentException(s + " does not match pair pattern.")
		}
		val firstStr = matcher.group(1)
		val secondStr = matcher.group(2)
		val firstInt = asInt(firstStr)
		val secondInt = asInt(secondStr)
		
		new Function1[(Int, Int), (Int, Int)]
		{
			def apply(x:(Int, Int)):(Int, Int) = 
				((x._1 + firstInt, x._2 + secondInt))
		}
	}
	
	
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
