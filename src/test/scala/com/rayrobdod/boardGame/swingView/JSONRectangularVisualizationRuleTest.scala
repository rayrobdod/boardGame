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
package com.rayrobdod.boardGame.swingView

import org.scalatest.{FunSuite, FunSpec}
import org.scalatest.prop.PropertyChecks
import java.awt.Image
import java.awt.image.ImageObserver
import scala.collection.immutable.Seq
import scala.util.Random
import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.RectangularField

class ParamaterizedRectangularVisualizationRuleTest extends FunSpec {
	
	// TODO: equivalance partition to improve speed
	describe ("Default Paramaterized visualization rule") {
		val dut:ParamaterizedRectangularVisualizationRule[String] = new ParamaterizedRectangularVisualizationRule()
		
		it ("indexies always match") {
			(0 to 10).foreach{x => (0 to 10).foreach{y =>
			(x to 10).foreach{width => (y to 10).foreach{height =>
				assert( dut.indexiesMatch(x, y, width, height) )
			}}
			}}
		}
		it ("rands always match") {
			val rng = new Random()
			
			(0 to 10).foreach{x =>
				assert( dut.randsMatch(rng) )
			}
		}
		it ("surroundingTilesMatch always match") {
			val rng = new Random()
			val map = RectangularField((0 to 10).map{x => (0 to 10).map{y => rng.nextString(rng.nextInt(10))}})
			
			(0 to 10).foreach{x => (0 to 10).foreach{y =>
				assert( dut.surroundingTilesMatch(map, x, y) )
			}}
		}
		it ("overall always match") {
			val rng = new Random()
			val map = RectangularField((0 to 10).map{x => (0 to 10).map{y => rng.nextString(rng.nextInt(10))}})
			
			(0 to 10).foreach{x => (0 to 10).foreach{y =>
				dut.matches(map, x, y, rng)
			}}
		}
		it ("has a priority of 1") {
			assertResult(1){dut.priority}
		}
	}
	describe ("tileRand = 2") {
		val dut = new ParamaterizedRectangularVisualizationRule(tileRand = 2)
		
		it ("indexies always match") {
			(0 to 10).foreach{x => (0 to 10).foreach{y =>
			(x to 10).foreach{width => (y to 10).foreach{height =>
				assert( dut.indexiesMatch(x, y, width, height) )
			}}
			}}
		}
		it ("rands") {
			val seed = Random.nextInt()
			val rng1 = new Random(seed)
			val rng2 = new Random(seed)
			val expected = (0 to 10).map{x => rng1.nextInt(2) == 0}
			val actual = (0 to 10).map{x => dut.randsMatch(rng2)}
			
			assertResult(expected){actual}
		}
		it ("surroundingTilesMatch always match") {
			val rng = new Random()
			val map = RectangularField((0 to 10).map{x => (0 to 10).map{y => rng.nextString(rng.nextInt(10))}})
			
			(0 to 10).foreach{x => (0 to 10).foreach{y =>
				assert( dut.surroundingTilesMatch(map, x, y) )
			}}
		}
		it ("overall always match") {
			val rng = new Random()
			val map = RectangularField((0 to 10).map{x => (0 to 10).map{y => rng.nextString(rng.nextInt(10))}})
			
			(0 to 10).foreach{x => (0 to 10).foreach{y =>
				dut.matches(map, x, y, rng)
			}}
		}
		it ("has a priority of 2") {
			assertResult(2){dut.priority}
		}
	}
	describe ("tileRand = 5") {
		val dut = new ParamaterizedRectangularVisualizationRule(tileRand = 5)
		
		it ("rands") {
			val seed = Random.nextInt()
			val rng1 = new Random(seed)
			val rng2 = new Random(seed)
			val expected = (0 to 20).map{x => rng1.nextInt(5) == 0}
			val actual = (0 to 20).map{x => dut.randsMatch(rng2)}
			
			assertResult(expected){actual}
		}
		it ("has a priority of 5") {
			assertResult(5){dut.priority}
		}
	}
	describe ("surroundingTiles = Map(identity -> \"a\")") {
		val surroundingTiles = Map(surroundingTilePart(0, 0, "a"))
		val dut = new ParamaterizedRectangularVisualizationRule(surroundingTiles = surroundingTiles)
		
		it ("surroundingTilesMatch match when current tile is 'a'") {
			val rng = new Random()
			val map = RectangularField((0 to 10).map{x => (0 to 10).map{y => if (rng.nextBoolean()) {"a"} else {"b"}}})
			
			(0 to 10).foreach{x => (0 to 10).foreach{y =>
				val expected = map((x, y)).typeOfSpace == "a"
				
				assertResult(expected){dut.surroundingTilesMatch(map, x, y)}
			}}
		}
		it ("has a priority of 10001") {
			assertResult(10001){dut.priority}
		}
	}
	describe ("surroundingTiles = Map(above -> \"a\")") {
		val surroundingTiles = Map(surroundingTilePart(0, -1, "a"))
		val dut = new ParamaterizedRectangularVisualizationRule(surroundingTiles = surroundingTiles)
		
		it ("surroundingTilesMatch match when above tile is 'a'") {
			val rng = new Random()
			val map = RectangularField((0 to 10).map{x => (0 to 10).map{y => if (rng.nextBoolean()) {"a"} else {"b"}}})
			
			(0 to 10).foreach{x => (0 to 10).foreach{y =>
				val expected = map.get((x, y - 1)).map{_.typeOfSpace}.getOrElse("a") == "a"
				
				assertResult(expected){dut.surroundingTilesMatch(map, x, y)}
			}}
		}
		it ("has a priority of 10001") {
			assertResult(10001){dut.priority}
		}
	}
	describe ("surroundingTiles = Map(below -> \"a\")") {
		val surroundingTiles = Map(surroundingTilePart(0, 1, "a"))
		val dut = new ParamaterizedRectangularVisualizationRule(surroundingTiles = surroundingTiles)
		
		it ("surroundingTilesMatch match when below tile is 'a'") {
			val rng = new Random()
			val map = RectangularField((0 to 10).map{x => (0 to 10).map{y => if (rng.nextBoolean()) {"a"} else {"b"}}})
			
			(0 to 10).foreach{x => (0 to 10).foreach{y =>
				val expected = map.get((x, y + 1)).map{_.typeOfSpace}.getOrElse("a") == "a"
				
				assertResult(expected){dut.surroundingTilesMatch(map, x, y)}
			}}
		}
		it ("has a priority of 10001") {
			assertResult(10001){dut.priority}
		}
	}
	describe ("surroundingTiles = Map(left -> \"a\")") {
		val surroundingTiles = Map(surroundingTilePart(-1, 0, "a"))
		val dut = new ParamaterizedRectangularVisualizationRule(surroundingTiles = surroundingTiles)
		
		it ("surroundingTilesMatch match when left tile is 'a'") {
			val rng = new Random()
			val map = RectangularField((0 to 10).map{x => (0 to 10).map{y => if (rng.nextBoolean()) {"a"} else {"b"}}})
			
			(0 to 10).foreach{x => (0 to 10).foreach{y =>
				val expected = map.get((x - 1, y)).map{_.typeOfSpace}.getOrElse("a") == "a"
				
				assertResult(expected){dut.surroundingTilesMatch(map, x, y)}
			}}
		}
		it ("has a priority of 10001") {
			assertResult(10001){dut.priority}
		}
	}
	describe ("surroundingTiles = Map(left -> \"a\", this -> 'a')") {
		val surroundingTiles = Map(surroundingTilePart(-1, 0, "a"), surroundingTilePart(0, 0, "a"))
		val dut = new ParamaterizedRectangularVisualizationRule(surroundingTiles = surroundingTiles)
		
		it ("surroundingTilesMatch match when current tile is 'a' and left tile is 'a'") {
			val rng = new Random()
			val map = RectangularField((0 to 10).map{x => (0 to 10).map{y => if (rng.nextBoolean()) {"a"} else {"b"}}})
			
			(0 to 10).foreach{x => (0 to 10).foreach{y =>
				val expected = map.get((x - 1, y)).map{_.typeOfSpace}.getOrElse("a") == "a" &&
				               map.get((x, y)).map{_.typeOfSpace}.getOrElse("a") == "a"
				
				assertResult(expected){dut.surroundingTilesMatch(map, x, y)}
			}}
		}
		it ("has a priority of 20001") {
			assertResult(20001){dut.priority}
		}
	}
	describe ("indexEquation = 'x == 0'") {
		val dut = new ParamaterizedRectangularVisualizationRule(indexEquation = "x == 0")
		
		it ("indexiesMatch is true when x is 0") {
			assert(dut.indexiesMatch(0, -1, -1, -1))
		}
		it ("indexiesMatch is true when x is 0 (2)") {
			assert(dut.indexiesMatch(0, 1, 1, 1))
		}
		it ("indexiesMatch is false when x is 1") {
			assert(! dut.indexiesMatch(1, -1, -1, -1))
		}
		it ("has a priority of 1001") {
			assertResult(1001){dut.priority}
		}
	}
	describe ("indexEquation = 'x % 2 == 0'") {
		val dut = new ParamaterizedRectangularVisualizationRule(indexEquation = "x % 2 == 0")
		
		it ("indexiesMatch is true when x is 0") {
			assert(dut.indexiesMatch(0, -1, -1, -1))
		}
		it ("indexiesMatch is false when x is 1") {
			assert(! dut.indexiesMatch(1, -1, -1, -1))
		}
		it ("indexiesMatch is true when x is 2") {
			assert(dut.indexiesMatch(2, -1, -1, -1))
		}
		it ("has a priority of 503") {
			assertResult(503){dut.priority}
		}
	}
	describe ("indexEquation = 'x == 2 && y == 4'") {
		val dut = new ParamaterizedRectangularVisualizationRule(indexEquation = "x == 2 && y == 4")
		
		it ("indexiesMatch is true when x is 2 and y is 4") {
			assert(dut.indexiesMatch(2, 4, -1, -1))
		}
		it ("indexiesMatch is false otherwise x is 1") {
			assert(! dut.indexiesMatch(-1, -1, -1, -1))
		}
		it ("has a priority of 2007") {
			assertResult(2007){dut.priority}
		}
	}
	describe ("indexEquation = 'w == 2 && h == 4'") {
		val dut = new ParamaterizedRectangularVisualizationRule(indexEquation = "w == 2 && h == 4")
		
		it ("indexiesMatch is true when w is 2 and h is 4") {
			assert(dut.indexiesMatch(-1, -1, 2, 4))
		}
		it ("indexiesMatch is false otherwise x is 1") {
			assert(! dut.indexiesMatch(-1, -1, -1, -1))
		}
		it ("has a priority of 2007") {
			assertResult(2007){dut.priority}
		}
	}
	
	
	
	
	final class EqualitySpaceClassMatcher[-SpaceClass](s:SpaceClass) extends SpaceClassMatcher[SpaceClass] {
		def unapply(sc:SpaceClass):Boolean = {s == sc}
	}
	
	final class MockImage(val id:Int) extends Image {
		override def getGraphics() = {throw new UnsupportedOperationException}
		override def getHeight(observer:ImageObserver) = {throw new UnsupportedOperationException}
		override def getProperty(name:String, observer:ImageObserver)  = {throw new UnsupportedOperationException}
		override def getSource() = {throw new UnsupportedOperationException}
		override def getWidth(observer:ImageObserver) = {throw new UnsupportedOperationException}
		
		def canEquals(other:Any) = {other.isInstanceOf[MockImage]}
		override def equals(other:Any):Boolean = {
			if (this.canEquals(other)) {
				val other2 = other.asInstanceOf[MockImage]
				if (other2.canEquals(this)) {
					other2.id == this.id
				} else {
					false
				}
			} else {
				false
			}
		} 
	}
	
	def mockImageSeq:Seq[Image] = (0 to 63).map{new MockImage(_)}
	
	def surroundingTilePart(deltaX:Int, deltaY:Int, spaceClass:String):Tuple2[IndexConverter, SpaceClassMatcher[String]] = {
		val f = new Function1[(Int, Int), (Int, Int)] {
			def apply(xy:(Int, Int)) = ((xy._1 + deltaX), (xy._2 + deltaY))
		}
		val m = new EqualitySpaceClassMatcher(spaceClass)
		(f -> m)
	}
}
