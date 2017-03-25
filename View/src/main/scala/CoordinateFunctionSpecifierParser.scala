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
 * A parser that converts CoordinateFunctionSpecifier strings into a `Index => Boolean` function.
 * 
 * Basically, think the smallest subset of C-styled languages to create a boolean-returning
 * expression consisting of integer and boolean algebra.
 * 
 * In order of precedence, the supported operators are:
 * 
  1. Boolean And
    - Bool && Bool ⇒ Bool
  1. Boolean Or
    - Bool || Bool ⇒ Bool
  1. Integer comparison
    - Int == Int ⇒ Bool
    - Int != Int ⇒ Bool
    - Int >= Int ⇒ Bool
    - Int <= Int ⇒ Bool
    - Int > Int ⇒ Bool
    - Int < Int ⇒ Bool
  1. Addition and Subtraction
    - Int + Int ⇒ Int
    - Int - Int ⇒ Int
  1. Division and Multiplication
    - Int * Int ⇒ Int
    - Int / Int ⇒ Int
    - Int % Int ⇒ Int
  1. Constants and variables
    - `true` => Bool
    - `false` => Bool
    - 1*DIGIT => Int
    - anything in vars.keySet (but ideally between 'a' and 'z') => Int
 * 
 * This also allows optional whitespace around any token, parenthesis around any expression to change precidence
 * 
 * Notably, no unary not and no if statements ; however anything that can be
 * expressed with unary-not or trinary-if should be expressable with the operators
 * provided.
 *  - ex: `!(a < b)` is equivalent to `a >= b`
 *  - ex: `(a == b ? c : d)` is equivalent to `(a == b && c) || (a != b && d)`
 * 
 * Theoretically, this could use the built-in Javascript interpreter, but that seems like a terrible
 * idea security-wise
 * 
 * @group CoordinateFunction
 * 
 * @since next
 * @constructor
 * @param vars a translation from variable names to a value
 */
final class CoordinateFunctionSpecifierParser[Index](vars:Map[Char, CoordinateFunction[Index, Int]]) {
	// apparently, scala 2.10 has trouble with finding `parserApi`
	private implicit def strToParserApi(s: String): fastparse.core.ParserApi[Unit, Char, String] = parserApi(s)
	private implicit def parserToParserApi[T](s: Parser[T]): fastparse.core.ParserApi[T, Char, String] = parserApi(s)
	
	
	// Two `P`s containing "(" means that neither can be followed by a cut
	
	private[this] val whitespace:P[Unit] = CharPred{_.isWhitespace}.opaque("whitespace")
	private[this] val variable:P[CoordinateFunction[Index, Int]] = CharIn(vars.keySet.to[Seq]).!.map{x => vars(x.charAt(0))}
	private[this] val number:P[CoordinateFunction[Any, Int]] = CharIn('0' to '9').rep(1).!.map{str => CoordinateFunction.constant(str.toInt)}
	
	private[this] val parensInt:P[CoordinateFunction[Index, Int]] = P( "(" ~ addSub ~ ")" )
	private[this] val factor:P[CoordinateFunction[Index, Int]] = variable | number | parensInt
	
	private[this] val divMul:P[CoordinateFunction[Index, Int]] = P(
		factor ~ (whitespace.rep ~ CharIn("*/%").! ~/ whitespace.rep ~ factor).rep
	).map{case (a, bcSeq) => bcSeq.foldLeft(a){case (folding, (b,c)) => b match {
		case "*" => folding.zipWith(c, {(x,y:Int) => x * y}, s"($folding * $c)")
		case "/" => folding.zipWith(c, {(x,y:Int) => x / y}, s"($folding / $c)", incrementDivCount = 1)
		case "%" => folding.zipWith(c, {(x,y:Int) => x % y}, s"($folding % $c)", incrementDivCount = 1)
	}}}
	
	private[this] val addSub:P[CoordinateFunction[Index, Int]] = P(
		divMul ~ (whitespace.rep ~ CharIn("+-").! ~/ whitespace.rep ~ divMul).rep
	).map{case (a, bcSeq) => bcSeq.foldLeft(a){(folding, bc) => bc._1 match {
		case "+" => folding.zipWith(bc._2, {(x,y:Int) => x + y}, s"($folding + ${bc._2})")
		case "-" => folding.zipWith(bc._2, {(x,y:Int) => x - y}, s"($folding - ${bc._2})")
	}}}
	
	private[this] val comparisons:P[CoordinateFunction[Index, Boolean]] = P(
		addSub ~ whitespace.rep ~ StringIn("==","<=",">=",">","<","!=").! ~/ whitespace.rep ~ addSub
	).map{case (a,b,c) => b match {
		case "==" => a.zipWith(c, {(x,y:Int) => x == y}, s"($a == $c)")
		case "!=" => a.zipWith(c, {(x,y:Int) => x != y}, s"($a != $c)")
		case ">=" => a.zipWith(c, {(x,y:Int) => x >= y}, s"($a >= $c)")
		case "<=" => a.zipWith(c, {(x,y:Int) => x <= y}, s"($a <= $c)")
		case ">"  => a.zipWith(c, {(x,y:Int) => x >  y}, s"($a > $c)")
		case "<"  => a.zipWith(c, {(x,y:Int) => x <  y}, s"($a < $c)")
	}}
	
	private[this] val trueP:P[CoordinateFunction[Index, Boolean]] = P("true").map{x => CoordinateFunction.constant(true)}
	private[this] val falseP:P[CoordinateFunction[Index, Boolean]] = P("false").map{x => CoordinateFunction.constant(false)}
	private[this] val parensBool:P[CoordinateFunction[Index, Boolean]] = P( "(" ~ and ~ ")" )
	private[this] val boolUnit:P[CoordinateFunction[Index, Boolean]] = comparisons | trueP | falseP | parensBool
	
	private[this] val or:P[CoordinateFunction[Index, Boolean]] = P(
		boolUnit ~ (whitespace.rep ~ "||" ~/ whitespace.rep ~ boolUnit).rep
	).map{case (a, cSeq) => cSeq.foldLeft(a){(folding, c) =>
		folding.zipWith(c, {(x,y:Boolean) => x || y}, s"($folding || $c)")
	}}
	
	private[this] val and:P[CoordinateFunction[Index, Boolean]] = P(
		or ~ (whitespace.rep ~ "&&" ~/ whitespace.rep ~ or).rep
	).map{case (a, cSeq) => cSeq.foldLeft(a){(folding, c) =>
		folding.zipWith(c, {(x,y:Boolean) => x && y}, s"($folding && $c)", incrementAndCount = 1)
	}}
	
	private[this] val parser:P[CoordinateFunction[Index, Boolean]] = and ~ End
	
	def parse(spec:String):Either[(String,Int), CoordinateFunction[Index, Boolean]] = {
		parser.parse(spec).fold({(_, idx, extra) => Left(extra.toString, idx)}, {(res, idx) => Right(res)})
	}
}

/**
 * @group CoordinateFunction
 */
object CoordinateFunctionSpecifierParser {
	import com.rayrobdod.boardGame.RectangularIndex
	import com.rayrobdod.boardGame.HorizontalHexagonalIndex
	
	val rectangularVars:Map[Char, CoordinateFunction[RectangularIndex, Int]] = Map(
		'x' -> new CoordinateFunction[RectangularIndex, Int]{def apply(xy:(Int, Int)) = xy._1; override def toString = "x"},
		'y' -> new CoordinateFunction[RectangularIndex, Int]{def apply(xy:(Int, Int)) = xy._2; override def toString = "y"}
	)
	
	val hexagonalVars:Map[Char, CoordinateFunction[HorizontalHexagonalIndex, Int]] = Map(
		'i' -> new CoordinateFunction[HorizontalHexagonalIndex, Int]{def apply(ij:(Int, Int)) = ij._1; override def toString = "i"},
		'j' -> new CoordinateFunction[HorizontalHexagonalIndex, Int]{def apply(ij:(Int, Int)) = ij._2; override def toString = "j"}
	)
}