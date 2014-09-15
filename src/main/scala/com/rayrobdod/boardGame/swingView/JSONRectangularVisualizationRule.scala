/*
	Deduction Tactics
	Copyright (C) 2012-2013  Raymond Dodge

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

import com.rayrobdod.boardGame._
import com.rayrobdod.util.BlitzAnimImage
import com.rayrobdod.animation.{AnimationIcon, ImageFrameAnimation}
import scala.util.Random
import scala.{Function0 => Future}
import scala.annotation.tailrec
import scala.collection.immutable.{Seq, Map, Vector, Set, SortedMap}
import scala.collection.mutable.{Map => MMap}
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.{TYPE_INT_RGB => nonAlphaImage, TYPE_INT_ARGB => alphaImage}
import java.net.URL
import javax.swing.{Icon, ImageIcon}
import java.util.regex.{Pattern, Matcher}
import javax.script.{Bindings, SimpleBindings, ScriptEngineManager, Compilable, CompiledScript}

import scala.runtime.{AbstractFunction2 => Function2}



/**
 * @author Raymond Dodge
 * @version 3.0.0
 */
class JSONRectangularVisualizationRule[A](
		jsonMap:Map[String, Any],
		tileSeq:Seq[Image],
		spaceClassUnapplier:SpaceClassMatcherFactory[A]
) extends RectangularVisualizationRule[A] {
	import JSONRectangularVisualizationRule.{asInt, asBoolean, asMapOfFrameIndexies, asIndexTranslationFunction}
	type IndexConverter = Function1[(Int, Int), (Int, Int)]
	
	// Map[layer, frames]
	def iconParts:Map[Int, Seq[Image]] = asMapOfFrameIndexies(jsonMap("tiles")).mapValues{_.map{tileSeq}}
	def surroundingTiles:Map[IndexConverter, SpaceClassMatcher[A]] =
		jsonMap.getOrElse("surroundingSpaces", Map.empty).asInstanceOf[Map[_,_]].map{(x:(Any,Any)) => (( asIndexTranslationFunction(x._1.toString), spaceClassUnapplier(x._2.toString) ))}
	def tileRand:Int = asInt(jsonMap.getOrElse("tileRand", 1))
	def indexEquation:String = jsonMap.getOrElse("indexies", true).toString
	
	override def indexiesMatch(x:Int, y:Int, width:Int, height:Int):Boolean = {
		import JSONRectangularVisualizationRule.{scriptEngine, buildBindings, executeScript}
		
		// identified as a bottleneck
		asBoolean( executeScript(indexEquation, buildBindings(x, y, width, height)) )
	}
	
	override def surroundingTilesMatch(field:RectangularField[_ <: A], x:Int, y:Int) = {
		
		surroundingTiles.forall({(conversion:IndexConverter, scc:SpaceClassMatcher[A]) =>
			val newIndexies = conversion( ((x,y)) )
			if (field.containsIndexies(newIndexies._1, newIndexies._2))
			{
				val spaceClass = field.space(newIndexies._1, newIndexies._2).typeOfSpace
				
				scc.unapply(spaceClass)
			} else {true}
		}.tupled)
	}
	
	override def randsMatch(rng:Random):Boolean = {
		rng.nextInt(tileRand) == 0
	}
	
	final override def priority:Int = {
		@tailrec def countMatches(m:Matcher, total:Int = 0):Int = {
			if (! m.hitEnd) {
				m.find
				countMatches(m, total + 1)
			} else {total}
		}
		import JSONRectangularVisualizationRule.{divisionPattern, andPattern}
		
		surroundingTiles.size * 10000 + tileRand +
			(if (indexEquation != "true") {
				1000 / {
					countMatches( divisionPattern.matcher(indexEquation) ) + 1
				} * {
					countMatches( andPattern.matcher(indexEquation) )
				} + {
					import JSONRectangularVisualizationRule.numberPattern
					
					@tailrec def sumMatches(m:Matcher, total:Int = 0):Int =
					{
						m.find()
						if (! m.hitEnd)
						{
							val number = Integer.parseInt(m.group)
							sumMatches(m, total + number)
						} else {total}
					}
					
					val m = numberPattern.matcher(indexEquation)
					sumMatches(m)
				}
			} else 0)
	}
}


/**
 * @author Raymond Dodge
 * @version 3.0.0
 */
object JSONRectangularVisualizationRule
{
	val divisionPattern = Pattern.compile("[%//]")
	val numberPattern = Pattern.compile("\\d+")
	val andPattern = Pattern.compile("&")
	
	val scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript")
	
	def buildBindings(x:Int, y:Int, width:Int, height:Int):Bindings = {
		val binding = new SimpleBindings
		binding.put("x", x)
		binding.put("y", y)
		binding.put("w", width)
		binding.put("h", height)
		binding
	}
	
	def executeScript(script:String, bindings:Bindings) = {
		scriptEngine.eval(script, bindings)
	}
	
	// TODO: see how much turning this into a function will help
	def asInt(x:Any):Int = x match {
		case y:Int => y
		case y:String => Integer.parseInt(y)
		case y:Integer => y
		case y:Long => y.intValue
	}
	
	def asBoolean(x:Any):Boolean = x match {
		case y:Boolean => y
		case y:Int => y != 0
		case y:String => java.lang.Boolean.parseBoolean(y)
	}
	
	def asMapOfFrameIndexies(frameIndexies:Any) =
	{
		val normalizedFrameIndex:Map[Int, Seq[Int]] = frameIndexies match {
			case x:Int => Map(-127 → Seq(x) )
			case x:Long => Map( -127 → Seq(x.intValue) )
			case x:Seq[_] => Map( -127 → x.map{asInt(_)} )
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
	
	def asIndexTranslationFunction(s:String) =
	{
		import java.util.regex.{Pattern, Matcher}
		
		val pairPattern = Pattern.compile("""\(([\+\-]?\d+),([\+\-]?\d+)\)""")
		val matcher = pairPattern.matcher(s)
		if (!matcher.matches())
			throw new IllegalArgumentException(s + " does not match pair pattern.")
		val firstStr = matcher.group(1)
		val secondStr = matcher.group(2)
		val firstInt = asInt(firstStr)
		val secondInt = asInt(secondStr)
		
		new Function1[(Int, Int), (Int, Int)]
		{
			def apply(x:(Int, Int)) = 
				((x._1 + firstInt, x._2 + secondInt))
		}
	}
	
	
	def PriorityOrdering = Ordering.by[RectangularVisualizationRule[_], Int]{(x:RectangularVisualizationRule[_]) => x.priority}

	object FullOrdering extends Ordering[JSONRectangularVisualizationRule[_]] {
		def compare(x:JSONRectangularVisualizationRule[_], y:JSONRectangularVisualizationRule[_]):Int = {
			(x.tileRand compareTo y.tileRand) match {
				case 0 => (x.indexEquation compareTo y.indexEquation) match {
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
