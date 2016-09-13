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
import scala.annotation.tailrec
import scala.collection.immutable.{Seq, Map, Set}
import scala.util.{Either, Left, Right}
import java.util.regex.{Pattern, Matcher}
import javax.script.{Bindings, SimpleBindings, ScriptEngineManager, Compilable, CompiledScript}
import com.rayrobdod.json.builder.{Builder, SeqBuilder, MapBuilder, ThrowBuilder, PrimitiveSeqBuilder}
import com.rayrobdod.json.parser.Parser
import com.rayrobdod.json.union.{StringOrInt, JsonValue}
import ParamaterizedRectangularVisualizationRule.{asBoolean, asIndexTranslationFunction}




/**
 * @since next
 */
final class RectangularVisualziationRuleBuilder[SpaceClass, IconPart](
		tileSeq:Seq[IconPart],
		spaceClassUnapplier:SpaceClassMatcherFactory[SpaceClass]
) extends Builder[String, JsonValue, ParamaterizedRectangularVisualizationRule[SpaceClass, IconPart]] {
	override def init:ParamaterizedRectangularVisualizationRule[SpaceClass, IconPart] = new ParamaterizedRectangularVisualizationRule[SpaceClass, IconPart]()
	
	override def apply[Input](folding:ParamaterizedRectangularVisualizationRule[SpaceClass, IconPart], key:String, input:Input, parser:Parser[String, JsonValue, Input]):Either[(String, Int), ParamaterizedRectangularVisualizationRule[SpaceClass, IconPart]] = key match {
		case "tileRand" => {
			parser.parsePrimitive(input).right.flatMap{_ match {
				case JsonValue.JsonValueNumber(x) => Right(folding.copy(tileRand = x.intValue))
				case _ => Left("tileRand value not number", 0)
			}}
		}
		case "indexies" => {
			parser.parsePrimitive(input).right.flatMap{_ match {
				case JsonValue.JsonValueString(x) => Right(folding.copy(indexEquation = x))
				case _ => Left("indexies value not string", 0)
			}}
		}
		case "surroundingSpaces" => {
			val builder = MapBuilder.apply.mapKey[String](asIndexTranslationFunction).mapValue[JsonValue](x => spaceClassUnapplier(JsonValue.unwrap(x).toString))
			parser.parse(builder, input).fold(
				{x => Right(folding.copy(surroundingTiles = x.mapValues{_ match {case Right(x) => x; case _ => throw new IllegalArgumentException("surroundingSpaces value not thing")}}))},
				{x => Left("surroundingSpaces value was primitive", 0)},
				{(s,i) => Left(s,i)}
			)
		}
		case "tiles" => {
			val ARBITRARY_NEGATIVE_VALUE = -127
			
			parser.parse(MapBuilder(new PrimitiveSeqBuilder[String, JsonValue]), input).fold(
				{x =>
					val x2:Map[String, Either[Seq[JsonValue], JsonValue]] = scala.collection.immutable.TreeMap.empty[String, Either[Seq[JsonValue], JsonValue]] ++ x
					val x3:Option[Either[Map[String, Seq[JsonValue]], Map[String, JsonValue]]] = x2.toList match {
						case Nil => None
						case (a, Left(b)) :: tail => tail.foldLeft[Option[Map[String, Seq[JsonValue]]]](Option(Map(a -> b))){(folding, item) => item match {
							case (x, Left(y)) => folding.map{z =>  z + (x -> y)}
							case (x, Right(y)) => None
						}}.map{Left(_)}
						case (a, Right(b)) :: tail => tail.foldLeft[Option[Map[String, JsonValue]]](Option(Map(a -> b))){(folding, item) => item match {
							case (x, Right(y)) => folding.map{z =>  z + (x -> y)}
							case (x, Left(y)) => None
						}}.map{Right(_)}
					}
					x3.foldLeft[Either[(String, Int), ParamaterizedRectangularVisualizationRule[SpaceClass,IconPart]]]( Left("tiles not legal value", 0) ){(a,b) => b match {
						case Left(x4:Map[String, Seq[JsonValue]]) => Right{
							val x5:Map[String, Seq[JsonValue]] = x4
							val x6:Map[Int, Seq[IconPart]] = x5.map{case (a, b) => ((a.toInt, b.map{case JsonValue.JsonValueNumber(c) => tileSeq(c.intValue); case _ => throw new ClassCastException("tiles not legal value")}))}
							
							folding.copy(iconParts = x6)
						}
						case Right(x4:Map[String, JsonValue]) => Right{
							val x5:Map[Int, IconPart] = x4.map{case (a, JsonValue.JsonValueNumber(c)) => ((a.toInt, tileSeq(c.intValue))); case _ => throw new ClassCastException("tiles not legal value")}
							val x6:Seq[IconPart] = x5.to[Seq].sortBy{_._1}.map{_._2}
							
							folding.copy(iconParts = Map(ARBITRARY_NEGATIVE_VALUE → x6))
						}
					}}
				},
				{x => x match {
					case JsonValue.JsonValueNumber(x) => Right(folding.copy(iconParts = Map(ARBITRARY_NEGATIVE_VALUE → Seq(tileSeq(x.intValue)))))
					case _ => Left("tiles unexpected value: " + x, 0)
				}},
				{(s,i) => Left(s,i)}
			)
		}
		case _ => Right(folding)
	}
}


/**
 * @since next
 */
final case class ParamaterizedRectangularVisualizationRule[SpaceClass, IconPart] (
	override val iconParts:Map[Int, Seq[IconPart]] = Map.empty[Int, Seq[IconPart]],
	tileRand:Int = 1,
	indexEquation:String = "true", // TODO: string? really?
	surroundingTiles:Map[IndexConverter, SpaceClassMatcher[SpaceClass]] = Map.empty[IndexConverter, SpaceClassMatcher[SpaceClass]]
) extends RectangularVisualizationRule[SpaceClass, IconPart] {
	override def indexiesMatch(x:Int, y:Int, width:Int, height:Int):Boolean = {
		import ParamaterizedRectangularVisualizationRule.{scriptEngine, buildBindings, executeScript}
		
		// identified as a bottleneck
		asBoolean( executeScript(indexEquation, buildBindings(x, y, width, height)) )
	}
	
	override def surroundingTilesMatch(field:RectangularField[_ <: SpaceClass], x:Int, y:Int):Boolean = {
		
		surroundingTiles.forall({(conversion:IndexConverter, scc:SpaceClassMatcher[SpaceClass]) =>
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
		@tailrec def countMatches(m:Matcher, total:Int = 0):Int = {
			if (m.find()) {
				countMatches(m, total + 1)
			} else {total}
		}
		import ParamaterizedRectangularVisualizationRule.{divisionPattern, andPattern}
		
		surroundingTiles.size * 10000 + tileRand +
			(if (indexEquation != "true") {
				1000 / {
					countMatches( divisionPattern.matcher(indexEquation) ) + 1
				} * {
					countMatches( andPattern.matcher(indexEquation) ) + 1
				} + {
					import ParamaterizedRectangularVisualizationRule.numberPattern
					
					@tailrec def sumMatches(m:Matcher, total:Int = 0):Int = {
						if (m.find()) {
							val number = Integer.parseInt(m.group)
							sumMatches(m, total + number)
						} else {total}
					}
					
					val m = numberPattern.matcher(indexEquation)
					sumMatches(m)
				}
			} else {0})
	}
}


/**
 * @version 3.0.0
 */
object ParamaterizedRectangularVisualizationRule
{
	private val divisionPattern = Pattern.compile("[%//]")
	private val numberPattern = Pattern.compile("\\d+")
	private val andPattern = Pattern.compile("&+")
	
	private val scriptEngine = {
		val retVal = new ScriptEngineManager(null).getEngineByName("JavaScript")
		if (retVal == null) throw new NullPointerException("scriptEngine not found")
		retVal
	}
	
	def buildBindings(x:Int, y:Int, width:Int, height:Int):Bindings = {
		val binding = new SimpleBindings
		binding.put("x", x)
		binding.put("y", y)
		binding.put("w", width)
		binding.put("h", height)
		binding
	}
	
	def executeScript(script:String, bindings:Bindings):Any = {
		scriptEngine.eval(script, bindings)
	}
	
	private def asBoolean(x:Any):Boolean = x match {
		case y:Boolean => y
		case y:Int => y != 0
		case y:String => java.lang.Boolean.parseBoolean(y)
	}
	
	def asIndexTranslationFunction(s:String):Function1[(Int, Int), (Int, Int)] =
	{
		import java.util.regex.{Pattern, Matcher}
		
		val pairPattern = Pattern.compile("""\(([\+\-]?\d+),([\+\-]?\d+)\)""")
		val matcher = pairPattern.matcher(s)
		if (!matcher.matches()) {
			throw new IllegalArgumentException(s + " does not match pair pattern.")
		}
		val firstStr = matcher.group(1)
		val secondStr = matcher.group(2)
		val firstInt = firstStr.toInt
		val secondInt = secondStr.toInt
		
		new Function1[(Int, Int), (Int, Int)]
		{
			def apply(x:(Int, Int)):(Int, Int) = 
				((x._1 + firstInt, x._2 + secondInt))
		}
	}
	
	
	def PriorityOrdering:Ordering[RectangularVisualizationRule[_,_]] = {
		Ordering.by[RectangularVisualizationRule[_,_], Int]{(x:RectangularVisualizationRule[_,_]) => x.priority}
	}
}
