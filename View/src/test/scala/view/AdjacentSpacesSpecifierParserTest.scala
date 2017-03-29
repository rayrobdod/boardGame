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

import scala.collection.immutable.Seq
import org.scalatest.FunSpec

class AdjacentSpacesSpecifierParserTest extends FunSpec {
	
	val identifiers = Seq("grass", "dirt", "road", "water", "pit", "ééé", "123", "\uD83D\uDEA7")
	val stringToSome = {s:String => Some(s)}
	val evenLenStringToSome = {s:String => Some(s).filter{_.size % 2 == 0}}
	def parse(s:String) = AdjacentSpacesSpecifierParser.parse[String](s, stringToSome)
	def matcher(s:String) = AdjacentSpacesSpecifierParser.spaceClassMatcherFactory[String](stringToSome).apply(s)
	
	describe ("The function produced by AdjacentSpacesSpecifierParser.parser") {
		
		describe ("when the specifier is a single identifier") {
			it ("should return true if given that identifier") {
				identifiers.foreach{id =>
					assert(parse(id).right.get.unapply(id))
				}
			}
			it ("should return false if given any other value") {
				for (a <- identifiers; z <- identifiers) {
					if (a != z) {
						assert(! parse(a).right.get.unapply(z))
					}
				}
			}
		}
		
		describe ("when the specifier is \"NOT \" followed by a single identifier") {
			it ("should return false if given that identifier") {
				identifiers.foreach{id =>
					assert(! parse(s"NOT $id").right.get.unapply(id))
				}
			}
			it ("should return true if given any other value") {
				for (a <- identifiers; z <- identifiers) {
					if (a != z) {
						assert(parse(s"NOT $a").right.get.unapply(z))
					}
				}
			}
		}
		
		describe ("when the specifier is two identifiers separated by \" OR \"") {
			it ("should return true if given the first identifier") {
				for (a <- identifiers; b <- identifiers) {
					assert(parse(s"$a OR $b").right.get.unapply(a))
				}
			}
			it ("should return true if given the second identifier") {
				for (a <- identifiers; b <- identifiers) {
					assert(parse(s"$a OR $b").right.get.unapply(b))
				}
			}
			it ("should return false if given any other value") {
				for (a <- identifiers; b <- identifiers; z <- identifiers) {
					if (a != z && b != z) {
						assert(! parse(s"$a OR $b").right.get.unapply(z))
					}
				}
			}
		}
		
		describe ("when the specifier is three identifiers separated by \" OR \"") {
			it ("should return true if given the first identifier") {
				for (a <- identifiers; b <- identifiers; c <- identifiers) {
					assert(parse(s"$a OR $b OR $c").right.get.unapply(a))
				}
			}
			it ("should return true if given the second identifier") {
				for (a <- identifiers; b <- identifiers; c <- identifiers) {
					assert(parse(s"$a OR $b OR $c").right.get.unapply(b))
				}
			}
			it ("should return true if given the third identifier") {
				for (a <- identifiers; b <- identifiers; c <- identifiers) {
					assert(parse(s"$a OR $b OR $c").right.get.unapply(c))
				}
			}
			it ("should return false if given any other value") {
				for (a <- identifiers; b <- identifiers; c <- identifiers; z <- identifiers) {
					if (a != z && b != z && c != z) {
						assert(! parse(s"$a OR $b OR $c").right.get.unapply(z))
					}
				}
			}
		}
		
		describe ("when the specifier is two identifiers both prefixed with \"NOT\" and separated by \" AND \"") {
			it ("should return false if given the first identifier") {
				for (a <- identifiers; b <- identifiers) {
					assert(! parse(s"NOT $a AND NOT $b").right.get.unapply(a))
				}
			}
			it ("should return false if given the second identifier") {
				for (a <- identifiers; b <- identifiers) {
					assert(! parse(s"NOT $a AND NOT $b").right.get.unapply(b))
				}
			}
			it ("should return true if given any other value") {
				for (a <- identifiers; b <- identifiers; z <- identifiers) {
					if (a != z && b != z) {
						assert(parse(s"NOT $a AND NOT $b").right.get.unapply(z))
					}
				}
			}
		}
		
		describe ("when the specifier is a value and its complement") {
			it ("should return false") {
				for (a <- identifiers; z <- identifiers) {
					assert(! parse(s"$a AND NOT $a").right.get.unapply(z))
				}
			}
		}
		
		it ("returns an error containing an unknown id when the specifier is just an unknown id") {
			assertResult(UnknownIdentifierFailure(Set("abc"))){
				AdjacentSpacesSpecifierParser.parse[String]("abc", evenLenStringToSome).left.get
			}
		}
		it ("returns an error containing an unknown id when the specifier is NOT followed by an unknown id") {
			assertResult(UnknownIdentifierFailure(Set("def"))){
				AdjacentSpacesSpecifierParser.parse[String]("NOT def", evenLenStringToSome).left.get
			}
		}
		it ("returns an error containing two unknown id when the specifier is two unknown ids OR'd") {
			assertResult(UnknownIdentifierFailure(Set("ghi", "jki"))){
				AdjacentSpacesSpecifierParser.parse[String]("ghi OR jki", evenLenStringToSome).left.get
			}
		}
		it ("returns an error containing one unknown id when the specifier is one known id ORd with an unknown id") {
			assertResult(UnknownIdentifierFailure(Set("jki"))){
				AdjacentSpacesSpecifierParser.parse[String]("gh OR jki", evenLenStringToSome).left.get
			}
		}
		it ("returns an error containing one unknown id when the specifier is one unknown id ORd with an known id") {
			assertResult(UnknownIdentifierFailure(Set("ghi"))){
				AdjacentSpacesSpecifierParser.parse[String]("ghi OR jk", evenLenStringToSome).left.get
			}
		}
		it ("returns an error containing two unknown id when the specifier is two unknown ids AND'd") {
			assertResult(UnknownIdentifierFailure(Set("ghi", "jki"))){
				AdjacentSpacesSpecifierParser.parse[String]("ghi AND jki", evenLenStringToSome).left.get
			}
		}
		it ("returns an error containing one unknown id when the specifier is one known id ANDd with an unknown id") {
			assertResult(UnknownIdentifierFailure(Set("jki"))){
				AdjacentSpacesSpecifierParser.parse[String]("gh AND jki", evenLenStringToSome).left.get
			}
		}
		it ("returns an error containing one unknown id when the specifier is one unknown id ANDd with an known id") {
			assertResult(UnknownIdentifierFailure(Set("ghi"))){
				AdjacentSpacesSpecifierParser.parse[String]("ghi AND jk", evenLenStringToSome).left.get
			}
		}
	}
	
	describe ("AdjacentSpacesSpecifierParser.spaceClassMatcherFactory") {
		it ("gets the same result as parse") {
			val spec = "grass OR dirt OR 123"
			val exp = parse(spec).right.get
			val dut = matcher(spec)
			
			for (z <- identifiers) {
				assertResult(exp.unapply(z)){dut.unapply(z)}
			}
		}
	}
	
	
	
	describe ("AdjacentSpacesSpecifierParser.parser") {
		it ("does not accept \"NOT\" as an identifier") {
			parse("NOT").left.get
		}
		it ("does not accept \"NOT\" as an identifier after a \"NOT \"") {
			parse("NOT NOT").left.get
		}
		it ("does not accept \"AND\" as an identifier") {
			parse("AND").left.get
		}
		it ("does not accept \"OR\" as an identifier") {
			parse("OR").left.get
		}
	}
}
