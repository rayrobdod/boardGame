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

import org.scalatest.FunSpec
import org.scalatest.prop.PropertyChecks

class CoordinateFunctionSpecifierParserTest extends FunSpec with PropertyChecks {
	def parse(s:String) = new CoordinateFunctionSpecifierParser(CoordinateFunctionSpecifierParser.rectangularVars).parse(s)
	
	describe ("The function produced by AdjacentSpacesSpecifierParser.parser") {
		describe ("when the specifier is \"true\"") {
			val parserResult = parse("true").right.get
			it ("returns true for all inputs") {
				forAll{(x:Int, y:Int) =>
					assert( parserResult((x,y)) )
				}
			}
		}
		describe ("when the specifier is \"false\"") {
			val parserResult = parse("false").right.get
			it ("returns false for all inputs") {
				forAll{(x:Int, y:Int) =>
					assert(! parserResult((x,y)) )
				}
			}
		}
		describe ("when the specifier is \"x==2\"") {
			val parserResult = parse("x==2").right.get
			it ("returns true when x is 2") {
				forAll{(y:Int) =>
					assert(parserResult((2, y)) )
				}
			}
			it ("returns false otherwise") {
				forAll{(x:Int, y:Int) => whenever(x != 2) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"y==2\"") {
			val parserResult = parse("y==2").right.get
			it ("returns true when y is 2") {
				forAll{(x:Int) =>
					assert(parserResult((x, 2)) )
				}
			}
			it ("returns false otherwise") {
				forAll{(x:Int, y:Int) => whenever(y != 2) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x != 2\"") {
			val parserResult = parse("x != 2").right.get
			it ("returns false when x is 2") {
				forAll{(y:Int) =>
					assert(! parserResult((2, y)) )
				}
			}
			it ("returns true otherwise") {
				forAll{(x:Int, y:Int) => whenever(x != 2) {
					assert(parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x <= 2\"") {
			val parserResult = parse("x <= 2").right.get
			it ("returns true when x is <= 2") {
				forAll{(x:Int, y:Int) => whenever(x <= 2) {
					assert(parserResult((x, y)) )
				}}
			}
			it ("returns false otherwise") {
				forAll{(x:Int, y:Int) => whenever(! (x <= 2)) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x >= 2\"") {
			val parserResult = parse("x >= 2").right.get
			it ("returns true when x is >= 2") {
				forAll{(x:Int, y:Int) => whenever(x >= 2) {
					assert(parserResult((x, y)) )
				}}
			}
			it ("returns false otherwise") {
				forAll{(x:Int, y:Int) => whenever(! (x >= 2)) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x < 2\"") {
			val parserResult = parse("x < 2").right.get
			it ("returns true when x is < 2") {
				forAll{(x:Int, y:Int) => whenever(x < 2) {
					assert(parserResult((x, y)) )
				}}
			}
			it ("returns false otherwise") {
				forAll{(x:Int, y:Int) => whenever(! (x < 2)) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x > 2\"") {
			val parserResult = parse("x > 2").right.get
			it ("returns true when x is > 2") {
				forAll{(x:Int, y:Int) => whenever(x > 2) {
					assert(parserResult((x, y)) )
				}}
			}
			it ("returns false otherwise") {
				forAll{(x:Int, y:Int) => whenever(! (x > 2)) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x + y == 2\"") {
			val parserResult = parse("x + y == 2").right.get
			it ("returns true when y == 2 - x") {
				forAll{(x:Int) => {
					val y = 2 - x
					assert(parserResult((x, y)) )
				}}
			}
			it ("returns false otherwise") {
				forAll{(x:Int, y:Int) => whenever(x + y != 2) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x - y == 2\"") {
			val parserResult = parse("x - y == 2").right.get
			it ("returns true when x == 2 + y") {
				forAll{(y:Int) => {
					val x = y + 2
					assert(parserResult((x, y)) )
				}}
			}
			it ("returns false otherwise") {
				forAll{(x:Int, y:Int) => whenever(x - y != 2) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x * 2 == y\"") {
			val parserResult = parse("x * 2 == y").right.get
			it ("returns true when x * 2 == y") {
				forAll{(x:Int) => {
					val y = x * 2
					assert(parserResult((x, y)) )
				}}
			}
			it ("returns false when x * 2 == y") {
				forAll{(x:Int, y:Int) => whenever(! (x * 2 == y)) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x / 2 == 0\"") {
			val parserResult = parse("x / 2 == 0").right.get
			it ("returns true when x / 2 == 0") {
				forAll{(x:Int, y:Int) => whenever(x / 2 == 0) {
					assert(parserResult((x, y)) )
				}}
			}
			it ("returns false when x / 2 == 0") {
				forAll{(x:Int, y:Int) => whenever(! (x / 2 == 0)) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x % 2 == 0\"") {
			val parserResult = parse("x % 2 == 0").right.get
			it ("returns true when x is even") {
				forAll{(x:Int, y:Int) => whenever(x % 2 == 0) {
					assert(parserResult((x, y)) )
				}}
			}
			it ("returns false when x is odd") {
				forAll{(x:Int, y:Int) => whenever(! (x % 2 == 0)) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"(x + y) % 2 == 0\"") {
			val parserResult = parse("(x + y) % 2 == 0").right.get
			it ("returns true when x is even") {
				forAll{(x:Int, y:Int) => whenever((x + y) % 2 == 0) {
					assert(parserResult((x, y)) )
				}}
			}
			it ("returns false when y is odd") {
				forAll{(x:Int, y:Int) => whenever(! ((x + y) % 2 == 0)) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x == 0 || y == 0\"") {
			val parserResult = parse("x == 0 || y == 0").right.get
			it ("returns true when either x or y is zero") {
				forAll{(whichIsZero:Boolean, xy:Int) => {
					assert(parserResult((
						if(whichIsZero) {xy} else {0},
						if(whichIsZero) {0} else {xy}
					)))
				}}
			}
			it ("returns false when neither x nor y is zero") {
				forAll{(x:Int, y:Int) => whenever(! (x == 0 || y == 0)) {
					assert(! parserResult((x, y)) )
				}}
			}
		}
		describe ("when the specifier is \"x != 0 && y != 0\"") {
			val parserResult = parse("x != 0 && y != 0").right.get
			it ("returns true when neither x nor y is zero") {
				forAll{(x:Int, y:Int) => whenever(x != 0 && y != 0) {
					assert(parserResult((x, y)) )
				}}
			}
			it ("returns false when either x or y is zero") {
				forAll{(whichIsZero:Boolean, xy:Int) => {
					assert(! parserResult((
						if(whichIsZero) {xy} else {0},
						if(whichIsZero) {0} else {xy}
					)))
				}}
			}
		}
		describe ("parenthetical around a boolean part") {
			val parserResult = parse("(x < 0 && y < 0) || x == y").right.get
			it ("returns true when ???") {
				forAll{(x:Int, y:Int) =>
					if (x < 0 && y < 0) {
						assert( parserResult((x, y)))
					} else {
						assert( parserResult((x, x)))
					}
				}
			}
			it ("returns false when ???") {
				forAll{(x:Int, y:Int) => whenever(x != y) {
					if (!(x < 0 && y < 0)) {
						assert(! parserResult((x, y)))
					} else {
						whenever(x != java.lang.Integer.MIN_VALUE && y != java.lang.Integer.MIN_VALUE) {
							// java.lang.Integer.MIN_VALUE == - java.lang.Integer.MIN_VALUE
							assert(! parserResult((-x, -y)))
						}
					}}
				}
			}
		}
		
	}
	
	describe ("The function produced by AdjacentSpacesSpecifierParser.parser priorities") {
		it ("\"true\" has a priority of 0") {
			assertResult(0){parse("true").right.get.priority}
		}
		ignore ("\"false\" has a priority of ???") {
			assertResult(???){parse("false").right.get.priority}
		}
		it ("\"x % 2 == 0\" has a lower priority than \"x % 4 == 0\"") {
			val mod2 = parse("x % 2 == 0").right.get
			val mod4 = parse("x % 4 == 0").right.get
			
			assert(mod2.priority < mod4.priority)
		}
		it ("and operations increase priority") {
			val noAnd = parse("x == 0").right.get
			val oneAnd = parse("x == 0 && y == 0").right.get
			
			assert(noAnd.priority < oneAnd.priority)
		}
		it ("Even more and operations increases priority") {
			val oneAnd = parse("x == 0 && x == 1").right.get
			val twoAnd = parse("x == 0 && y == 0 && x == 1").right.get
			
			assert(oneAnd.priority < twoAnd.priority)
		}
		it ("\"x == 0\" has a higher priority than \"x % 2 == 0\"") {
			val noDiv = parse("x == 0").right.get
			val oneDiv = parse("x % 2 == 0").right.get
			
			assert(noDiv.priority > oneDiv.priority)
		}
	}
	
	describe ("AdjacentSpacesSpecifierParser.parser") {
		it ("does not accept an integer-returning expression") {
			parse("x + y").left.get
		}
		it ("does not accept a variable 'b'") {
			parse("b == 0").left.get
		}
		it ("does not accept a variable 'xy'") {
			parse("xy == 0").left.get
		}
	}
	
	describe ("CoordinateFunctionSpecifierParser varmaps") {
		describe ("rectangularVars") {
			it ("has keys of x and y") {
				assertResult(Set('x', 'y')){CoordinateFunctionSpecifierParser.rectangularVars.keySet}
			}
			describe ("x") {
				it ("equals itself") {
					assert(CoordinateFunctionSpecifierParser.rectangularVars('x') == CoordinateFunctionSpecifierParser.rectangularVars('x'))
				}
				it ("apply returns first coords") {
					val dut = CoordinateFunctionSpecifierParser.rectangularVars('x')
					forAll{(x:Int, y:Int) =>
						assertResult(x){ dut.apply((x,y)) }
					}
				}
			}
			describe ("y") {
				it ("equals itself") {
					assert(CoordinateFunctionSpecifierParser.rectangularVars('y') == CoordinateFunctionSpecifierParser.rectangularVars('y'))
				}
				it ("apply returns first coords") {
					val dut = CoordinateFunctionSpecifierParser.rectangularVars('y')
					forAll{(x:Int, y:Int) =>
						assertResult(y){ dut.apply((x,y)) }
					}
				}
			}
		}
		describe ("hexagonalVars") {
			it ("has keys of i and j") {
				assertResult(Set('i', 'j')){CoordinateFunctionSpecifierParser.hexagonalVars.keySet}
			}
			describe ("i") {
				it ("equals itself") {
					assert(CoordinateFunctionSpecifierParser.hexagonalVars('i') == CoordinateFunctionSpecifierParser.hexagonalVars('i'))
				}
				it ("apply returns first coords") {
					val dut = CoordinateFunctionSpecifierParser.hexagonalVars('i')
					forAll{(x:Int, y:Int) =>
						assertResult(x){ dut.apply((x,y)) }
					}
				}
			}
			describe ("j") {
				it ("equals itself") {
					assert(CoordinateFunctionSpecifierParser.hexagonalVars('j') == CoordinateFunctionSpecifierParser.hexagonalVars('j'))
				}
				it ("apply returns first coords") {
					val dut = CoordinateFunctionSpecifierParser.hexagonalVars('j')
					forAll{(x:Int, y:Int) =>
						assertResult(y){ dut.apply((x,y)) }
					}
				}
			}
		}
		describe ("elongatedTriangularVars") {
			it ("has keys of x and y and t") {
				assertResult(Set('x', 'y', 't')){CoordinateFunctionSpecifierParser.elongatedTriangularVars.keySet}
			}
			describe ("x") {
				it ("equals itself") {
					assert(CoordinateFunctionSpecifierParser.elongatedTriangularVars('x') == CoordinateFunctionSpecifierParser.elongatedTriangularVars('x'))
				}
			}
			describe ("y") {
				it ("equals itself") {
					assert(CoordinateFunctionSpecifierParser.elongatedTriangularVars('y') == CoordinateFunctionSpecifierParser.elongatedTriangularVars('y'))
				}
			}
			describe ("t") {
				it ("equals itself") {
					assert(CoordinateFunctionSpecifierParser.elongatedTriangularVars('t') == CoordinateFunctionSpecifierParser.elongatedTriangularVars('t'))
				}
			}
		}
	}
}
