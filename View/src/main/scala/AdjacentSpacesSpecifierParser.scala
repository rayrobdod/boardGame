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
import com.rayrobdod.boardGame.{SpaceClassMatcher, ConstFalseSpaceClassMatcher}

/**
 * A parser that converts AdjacentSpacesSpecifier strings into
 * [[com.rayrobdod.boardGame.SpaceClassMatcher SpaceClassMatcher]]s.
 * 
 * a AdjacentSpacesSpecifier is a string that consists of a sequence of SpaceClassIdentifiers,
 * each identifier potentially prepended by "NOT", separated by "AND" or "OR", and with mandatory whitespace
 * separating each token. There is currently no explicit grouping, and the precedence order is
 * NOT before OR before AND.
 * 
 * Alternatively, in something like ABNF (but not quite ABNF since this isn't
 * limited to ASCII and writing &lt;identifier&gt;'s description in ABNF is impractical):
 * 
 * {{{
 * specifier  = 1*andpart
 * andpart    = orpart *( 1*whitespace and 1*whitespace orpart )
 * orpart     = notpart *( 1*whitespace or 1*whitespace notpart )
 * notpart    = [ not 1*whitespace ] identifier
 * 
 * not        = %x4e.4f.54 ; "NOT"
 * and        = %x41.4e.44 ; "AND"
 * or         = %x4f.52    ; "OR"
 * whitespace = ?? ; any character for which `java.lang.Char::isWhitespace` is true
 *                 ; Oracle jre1.8.0_111 says %x09 / %x0a / %x0b / %x0c / %x0d / %x1c / %x1d / %x1e / %x1f / %x20 /
 *                 ;               %x1680 / %x180e / %x2000 / %x2001 / %x2002 / %x2003 / %x2004 / %x2005 / %x2006 /
 *                 ;                %x2008 / %x2009 / %x200a / %x2028 / %x2029 / %x205f / %x3000
 * identifier = ?? ; a sequence of 1 or more non-whitespace characters which does not match &lt;not&gt;, &lt;and&gt; or &lt;or&gt;
 * }}}
 * 
 * @group SpaceClassMatcherFactory
 * @since next
 */
object AdjacentSpacesSpecifierParser {
	// apparently, scala 2.10 has trouble with finding `parserApi`
	private implicit def strToParserApi(s: String): fastparse.core.ParserApi[Unit, Char, String] = parserApi(s)
	private implicit def parserToParserApi[T](s: Parser[T]): fastparse.core.ParserApi[T, Char, String] = parserApi(s)
	
	
	/* I'd love to bitset, but the list of possible names is unknown */
	/** Represents a set of strings */
	private[this] trait MySet[A] {
		def contains(x:A):Boolean
		/** @return Left values that were tried but returned None; Right the mapped set */
		def mapOpt[B](fun:A => Option[B]):Either[Set[A],MySet[B]]
		
		/** A union of this and rhs */
		final def or(rhs:MySet[A]):MySet[A] = new MySet[A]{
			def contains(x:A) = MySet.this.contains(x) || rhs.contains(x)
			def mapOpt[B](fun:A => Option[B]):Either[Set[A],MySet[B]] = (MySet.this.mapOpt(fun), rhs.mapOpt(fun)) match {
				case (Right(a), Right(b)) => Right(a or b)
				case (Left(a), Left(b)) => Left(a ++ b)
				case (Left(a), _) => Left(a)
				case (_, Left(b)) => Left(b)
			}
		}
		/** a intersection of this and rhs */
		final def and(rhs:MySet[A]):MySet[A] = new MySet[A]{
			def contains(x:A) = MySet.this.contains(x) && rhs.contains(x)
			def mapOpt[B](fun:A => Option[B]):Either[Set[A],MySet[B]] = (MySet.this.mapOpt(fun), rhs.mapOpt(fun)) match {
				case (Right(a), Right(b)) => Right(a and b)
				case (Left(a), Left(b)) => Left(a ++ b)
				case (Left(a), _) => Left(a)
				case (_, Left(b)) => Left(b)
			}
		}
	}
	/** Represents the set that contains only `value` */
	private[this] final class MySetContains[A](value:A) extends MySet[A] {
		def contains(x:A):Boolean = {x == value}
		override def mapOpt[B](fun:A => Option[B]):Either[Set[A],MySet[B]] = {
			fun(value).fold[Either[Set[A],MySet[B]]]{Left(Set(value))}{x => Right(new MySetContains(x))}
		}
	}
	/** Represents the set that contains all strings except `value` */
	private[this] final class MySetAllBut[A](value:A) extends MySet[A] {
		def contains(x:A):Boolean = {x != value}
		override def mapOpt[B](fun:A => Option[B]):Either[Set[A],MySet[B]] = {
			fun(value).fold[Either[Set[A],MySet[B]]]{Left(Set(value))}{x => Right(new MySetAllBut(x))}
		}
	}
	
	
	private[this] val keywords = Set("NOT", "OR", "AND")
	
	private[this] val whitespace:P[Unit] = CharPred{_.isWhitespace}.opaque("whitespace")
	private[this] val spaceclass:P[String] = CharPred{! _.isWhitespace}.rep(1).!.filter{! keywords.contains(_)}.opaque("an identifier")
	
	private[this] val notPart:P[MySet[String]] = P(
		"NOT" ~/ whitespace.rep(1) ~ spaceclass.map{new MySetAllBut(_)} |
		spaceclass.map{new MySetContains(_)}
	)
	
	private[this] val orPart:P[MySet[String]] = P(
		notPart ~ (whitespace.rep(1) ~ "OR" ~/ whitespace.rep(1) ~ orPart).?
	).map{case (a,bOpt) => bOpt.fold(a){b => a or b}}
	
	private[this] val andPart:P[MySet[String]] = P(
		orPart ~ (whitespace.rep(1) ~ "AND" ~/ whitespace.rep(1) ~ andPart).?
	).map{case (a,bOpt) => bOpt.fold(a){b => a and b}}
	
	private[this] val parser:P[MySet[String]] = andPart ~ End
	
	
	
	private[this] final class SpaceClassMatcherFromMySet[SpaceClass](backing:MySet[SpaceClass]) extends SpaceClassMatcher[SpaceClass] {
		override def unapply(sc:SpaceClass):Boolean = backing.contains(sc)
	}
	
	// TODO: resource strings
	private[this] final val unknownError:String = "Unknown Identifiers: "
	
	/**
	 * Parse `spec` into a SpaceClassMatcher, using the format described in the class documentation
	 * 
	 * @param spec the string to parse
	 * @param derefSpaceClass a partial function that takes identifiers and returns the appropriate SpaceClass
	 * @return
		- A right containing the parsed SpaceClassMatcher
		- A left containing the identifiers that were in the spec but not in `derefSpaceClass`
	 */
	def parse[SpaceClass](spec:String, derefSpaceClass:String => Option[SpaceClass]):Either[(String,Int), SpaceClassMatcher[SpaceClass]] = {
		parser.parse(spec).fold({(_, idx, extra) => Left(extra.toString, idx)}, {(res, idx) => 
			res.mapOpt(derefSpaceClass)
				.right.map{new SpaceClassMatcherFromMySet(_)}
				.left.map{x:Set[String] => (x.mkString(unknownError, ", ", ""), 0)}
		})
	}
	
	/**
	 * Creates a SpaceClassMatcherFactory that parses strings using the format described in the class documentation 
	 * 
	 * If the parsing of the string fails, the Factory will return a [[com.rayrobdod.boardGame.ConstFalseSpaceClassMatcher]]
	 * 
	 * @param derefSpaceClass a partial function that takes identifiers and returns the appropriate SpaceClass
	 */
	def spaceClassMatcherFactory[SpaceClass](derefSpaceClass:String => Option[SpaceClass]):SpaceClassMatcherFactory[SpaceClass] = {
		new SpaceClassMatcherFactory[SpaceClass] {
			def apply(spec:String):SpaceClassMatcher[SpaceClass] = {
				AdjacentSpacesSpecifierParser.this.parse(spec, derefSpaceClass).fold({err => ConstFalseSpaceClassMatcher}, {m => m})
			}
		}
	}
}
