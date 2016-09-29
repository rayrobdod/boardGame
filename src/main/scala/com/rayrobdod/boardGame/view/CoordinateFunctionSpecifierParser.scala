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

import fastparse.all._
import scala.language.implicitConversions

/**
 * Contains a parser that converts CoordinateFunctionSpecifier into a `(Int,Int,Int,Int) => Boolean` function.
 * 
 * Basically, think the smallest subset of C-styled languages to create a boolean-returning
 * expression consisting of integer and boolean algebra.
 * 
 * Theoretically, this could use the built-in Javascript interpreter, but I don't know the security
 * implications of an action like that.
 */
object CoordinateFunctionSpecifierParser {
	// apparently, scala 2.10 has trouble with finding `parserApi`
	private implicit def strToParserApi(s: String): fastparse.core.ParserApi[Unit, Char, String] = parserApi(s)
	private implicit def parserToParserApi[T](s: Parser[T]): fastparse.core.ParserApi[T, Char, String] = parserApi(s)
	
	
	trait CoordinateFunction[@specialized(Int, Boolean) A] {
		def apply(x:Int, y:Int, w:Int, h:Int):A
		private[CoordinateFunctionSpecifierParser] def zipMap[B, C](rhs:CoordinateFunction[B], mapping:(A,B) => C, name:String) = {
			new CoordinateFunction[C]{
				def apply(x:Int, y:Int, w:Int, h:Int):C =
					mapping(CoordinateFunction.this.apply(x,y,w,h), rhs.apply(x,y,w,h))
				override def toString = name
			}
		}
	}
	object CoordinateFunction {
		def constant[@specialized(Int, Boolean) A](a:A) = new CoordinateFunction[A]{
			def apply(x:Int, y:Int, w:Int, h:Int):A = a
			override def toString:String = a.toString
		}
	}
	
	// Two `P`s containing "(" means that neither can be followed by a cut
	
	private[this] val whitespace:P[Unit] = CharPred{_.isWhitespace}.opaque("whitespace")
	private[this] val variable:P[CoordinateFunction[Int]] = CharIn("xywh").!.map{_ match {
		case "x" => new CoordinateFunction[Int]{def apply(x:Int, y:Int, w:Int, h:Int) = x; override def toString = "x"}
		case "y" => new CoordinateFunction[Int]{def apply(x:Int, y:Int, w:Int, h:Int) = y; override def toString = "y"}
		case "w" => new CoordinateFunction[Int]{def apply(x:Int, y:Int, w:Int, h:Int) = w; override def toString = "w"}
		case "h" => new CoordinateFunction[Int]{def apply(x:Int, y:Int, w:Int, h:Int) = h; override def toString = "h"}
	}}
	private[this] val number:P[CoordinateFunction[Int]] = CharIn('0' to '9').rep(1).!.map{str => CoordinateFunction.constant(str.toInt)}
	
	private[this] val parensInt:P[CoordinateFunction[Int]] = P( "(" ~ addSub ~ ")" )
	private[this] val factor:P[CoordinateFunction[Int]] = variable | number | parensInt
	
	private[this] val divMul:P[CoordinateFunction[Int]] = P(
		factor ~ (whitespace.rep ~ CharIn("*/%").! ~/ whitespace.rep ~ factor).rep
	).map{case (a, bcSeq) => bcSeq.foldLeft(a){case (folding, (b,c)) => b match {
		case "*" => folding.zipMap(c, {(x,y:Int) => x * y}, s"($folding * $c)")
		case "/" => folding.zipMap(c, {(x,y:Int) => x / y}, s"($folding / $c)")
		case "%" => folding.zipMap(c, {(x,y:Int) => x % y}, s"($folding % $c)")
	}}}
	
	private[this] val addSub:P[CoordinateFunction[Int]] = P(
		divMul ~ (whitespace.rep ~ CharIn("+-").! ~/ whitespace.rep ~ divMul).rep
	).map{case (a, bcSeq) => bcSeq.foldLeft(a){(folding, bc) => bc._1 match {
		case "+" => folding.zipMap(bc._2, {(x,y:Int) => x + y}, s"($folding + ${bc._2})")
		case "-" => folding.zipMap(bc._2, {(x,y:Int) => x - y}, s"($folding - ${bc._2})")
	}}}
	
	private[this] val comparisons:P[CoordinateFunction[Boolean]] = P(
		addSub ~ whitespace.rep ~ StringIn("==","<=",">=",">","<","!=").! ~/ whitespace.rep ~ addSub
	).map{case (a,b,c) => b match {
		case "==" => a.zipMap(c, {(x,y:Int) => x == y}, s"($a == $c)")
		case "!=" => a.zipMap(c, {(x,y:Int) => x != y}, s"($a != $c)")
		case ">=" => a.zipMap(c, {(x,y:Int) => x >= y}, s"($a >= $c)")
		case "<=" => a.zipMap(c, {(x,y:Int) => x <= y}, s"($a <= $c)")
		case ">"  => a.zipMap(c, {(x,y:Int) => x >  y}, s"($a > $c)")
		case "<"  => a.zipMap(c, {(x,y:Int) => x <  y}, s"($a < $c)")
	}}
	
	private[this] val trueP:P[CoordinateFunction[Boolean]] = P("true").map{x => CoordinateFunction.constant(true)}
	private[this] val falseP:P[CoordinateFunction[Boolean]] = P("false").map{x => CoordinateFunction.constant(false)}
	private[this] val parensBool:P[CoordinateFunction[Boolean]] = P( "(" ~ and ~ ")" )
	private[this] val boolUnit:P[CoordinateFunction[Boolean]] = comparisons | trueP | falseP | parensBool
	
	private[this] val or:P[CoordinateFunction[Boolean]] = P(
		boolUnit ~ (whitespace.rep ~ "||" ~/ whitespace.rep ~ boolUnit).rep
	).map{case (a, cSeq) => cSeq.foldLeft(a){(folding, c) =>
		folding.zipMap(c, {(x,y:Boolean) => x || y}, s"($folding || $c)")
	}}
	
	private[this] val and:P[CoordinateFunction[Boolean]] = P(
		or ~ (whitespace.rep ~ "&&" ~/ whitespace.rep ~ or).rep
	).map{case (a, cSeq) => cSeq.foldLeft(a){(folding, c) =>
		folding.zipMap(c, {(x,y:Boolean) => x && y}, s"($folding && $c)")
	}}
	
	val parser:P[CoordinateFunction[Boolean]] = and ~ End

}