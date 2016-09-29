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
	
	val identifiers = Seq("grass", "dirt", "road", "water", "pit", "ééé", "123")
	import AdjacentSpacesSpecifierParser.parser

	describe ("The function produced by AdjacentSpacesSpecifierParser.parser") {
		
		describe ("when the specifier is a single identifier") {
			it ("should return true if given that identifier") {
				identifiers.foreach{id =>
					assert(parser.parse(id).get.value.apply(id))
				}
			}
			it ("should return false if given any other value") {
				for (a <- identifiers; z <- identifiers) {
					if (a != z) {
						assert(! parser.parse(a).get.value.apply(z))
					}
				}
			}
		}
		
		describe ("when the specifier is \"NOT \" followed by a single identifier") {
			it ("should return false if given that identifier") {
				identifiers.foreach{id =>
					assert(! parser.parse(s"NOT $id").get.value.apply(id))
				}
			}
			it ("should return true if given any other value") {
				for (a <- identifiers; z <- identifiers) {
					if (a != z) {
						assert(parser.parse(s"NOT $a").get.value.apply(z))
					}
				}
			}
		}
		
		describe ("when the specifier is two identifiers separated by \" OR \"") {
			it ("should return true if given the first identifier") {
				for (a <- identifiers; b <- identifiers) {
					assert(parser.parse(s"$a OR $b").get.value.apply(a))
				}
			}
			it ("should return true if given the second identifier") {
				for (a <- identifiers; b <- identifiers) {
					assert(parser.parse(s"$a OR $b").get.value.apply(b))
				}
			}
			it ("should return false if given any other value") {
				for (a <- identifiers; b <- identifiers; z <- identifiers) {
					if (a != z && b != z) {
						assert(! parser.parse(s"$a OR $b").get.value.apply(z))
					}
				}
			}
		}
		
		describe ("when the specifier is three identifiers separated by \" OR \"") {
			it ("should return true if given the first identifier") {
				for (a <- identifiers; b <- identifiers; c <- identifiers) {
					assert(parser.parse(s"$a OR $b OR $c").get.value.apply(a))
				}
			}
			it ("should return true if given the second identifier") {
				for (a <- identifiers; b <- identifiers; c <- identifiers) {
					assert(parser.parse(s"$a OR $b OR $c").get.value.apply(b))
				}
			}
			it ("should return true if given the third identifier") {
				for (a <- identifiers; b <- identifiers; c <- identifiers) {
					assert(parser.parse(s"$a OR $b OR $c").get.value.apply(c))
				}
			}
			it ("should return false if given any other value") {
				for (a <- identifiers; b <- identifiers; c <- identifiers; z <- identifiers) {
					if (a != z && b != z && c != z) {
						assert(! parser.parse(s"$a OR $b OR $c").get.value.apply(z))
					}
				}
			}
		}
		
		describe ("when the specifier is two identifiers both prefixed with \"NOT\" and separated by \" AND \"") {
			it ("should return false if given the first identifier") {
				for (a <- identifiers; b <- identifiers) {
					assert(! parser.parse(s"NOT $a AND NOT $b").get.value.apply(a))
				}
			}
			it ("should return false if given the second identifier") {
				for (a <- identifiers; b <- identifiers) {
					assert(! parser.parse(s"NOT $a AND NOT $b").get.value.apply(b))
				}
			}
			it ("should return true if given any other value") {
				for (a <- identifiers; b <- identifiers; z <- identifiers) {
					if (a != z && b != z) {
						assert(parser.parse(s"NOT $a AND NOT $b").get.value.apply(z))
					}
				}
			}
		}
		
		describe ("when the specifier is a value and its complement") {
			it ("should return false") {
				for (a <- identifiers; z <- identifiers) {
					assert(! parser.parse(s"$a AND NOT $a").get.value.apply(z))
				}
			}
		}
	}
	
	describe ("AdjacentSpacesSpecifierParser.parser") {
		it ("does not accept \"NOT\" as an identifier") {
			parser.parse("NOT").fold[Unit]({(parser, idx, extra) =>
					// success
				}, {(res, idx) =>
					fail("Parse succeeded: " + res)
			})
		}
		it ("does not accept \"NOT\" as an identifier after a \"NOT \"") {
			parser.parse("NOT NOT").fold[Unit]({(parser, idx, extra) =>
					// success
				}, {(res, idx) =>
					fail("Parse succeeded: " + res)
			})
		}
		it ("does not accept \"AND\" as an identifier") {
			parser.parse("AND").fold[Unit]({(parser, idx, extra) =>
					// success
				}, {(res, idx) =>
					fail("Parse succeeded: " + res)
			})
		}
		it ("does not accept \"OR\" as an identifier") {
			parser.parse("OR").fold[Unit]({(parser, idx, extra) =>
					// success
				}, {(res, idx) =>
					fail("Parse succeeded: " + res)
			})
		}
	}
}
