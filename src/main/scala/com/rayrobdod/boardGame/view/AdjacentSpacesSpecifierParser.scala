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
 * Contains a parser that converts AdjacentSpacesSpecifiers into a `String => Boolean` function.
 * 
 * a AdjacentSpacesSpecifier is a string that consists of a sequence of SpaceClassIdentifiers,
 * possibly prepended by "NOT", separated by "AND" or "OR", and with mandatory whitespace
 * separating each token. There is currently no explicit grouping, and the precedence order is
 * NOT before OR before AND.
 * 
 */
object AdjacentSpacesSpecifierParser {
	// apparently, scala 2.10 has trouble with finding `parserApi`
	private implicit def strToParserApi(s: String): fastparse.core.ParserApi[Unit, Char, String] = parserApi(s)
	private implicit def parserToParserApi[T](s: Parser[T]): fastparse.core.ParserApi[T, Char, String] = parserApi(s)
	
	
	/* I'd love to bitset, but the list of possible names is unknown */
	/** Represents a set of strings */
	private[this] trait MySet {
		def contains(x:String):Boolean
		
		/** A union of this and rhs */
		final def or(rhs:MySet):MySet = new MySet{
			def contains(x:String) = MySet.this.contains(x) || rhs.contains(x)
			override def toString = s"MySetOr(${MySet.this}, $rhs)"
			def toSerializedString:String = s"${MySet.this} OR $rhs"
		}
		/** a intersection of this and rhs */
		final def and(rhs:MySet):MySet = new MySet{
			def contains(x:String) = MySet.this.contains(x) && rhs.contains(x)
			override def toString = s"MySetAnd(${MySet.this}, $rhs)"
			def toSerializedString:String = s"${MySet.this} AND $rhs"
		}
	}
	/** Represents the set that contains only `value` */
	private[this] final class MySetContains(value:String) extends MySet {
		def contains(x:String):Boolean = {x == value}
		def toSerializedString:String = value
		override def toString = s"MySetContains($value)"
	}
	/** Represents the set that contains all strings except `value` */
	private[this] final class MySetAllBut(value:String) extends MySet {
		def contains(x:String):Boolean = {x != value}
		def toSerializedString:String = "NOT " + value
		override def toString = s"MySetAllBut($value)"
	}
	
	
	private[this] val keywords = Set("NOT", "OR", "AND")
	
	private[this] val whitespace:P[Unit] = CharPred{_.isWhitespace}.opaque("whitespace")
	private[this] val spaceclass:P[String] = CharPred{! _.isWhitespace}.rep(1).!.filter{! keywords.contains(_)}.opaque("an identifier")
	
	private[this] val notPart:P[MySet] = P(
		"NOT" ~/ whitespace.rep(1) ~ spaceclass.map{new MySetAllBut(_)} |
		spaceclass.map{new MySetContains(_)}
	)
	
	private[this] val orPart:P[MySet] = P(
		notPart ~ (whitespace.rep(1) ~ "OR" ~/ whitespace.rep(1) ~ orPart).?
	).map{case (a,bOpt) => bOpt.fold(a){b => a or b}}
	
	private[this] val andPart:P[MySet] = P(
		orPart ~ (whitespace.rep(1) ~ "AND" ~/ whitespace.rep(1) ~ andPart).?
	).map{case (a,bOpt) => bOpt.fold(a){b => a and b}}
	
	val parser:P[Function1[String,Boolean]] = andPart.map{set => {x:String => set.contains(x)}} ~ End
}
