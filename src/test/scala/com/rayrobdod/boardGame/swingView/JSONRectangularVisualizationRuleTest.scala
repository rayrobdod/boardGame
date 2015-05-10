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

class JsonRectangularVisualizationRuleTest extends FunSpec {
	
	// TODO: equivalance partition to improve speed
	describe ("Default Json visualization rule") {
		// json = "{}"
		val jsonMap = Map.empty[String, Any]
		val dut:RectangularVisualizationRule[String] = new JSONRectangularVisualizationRule(jsonMap, mockImageSeq, new MySCMF)
		
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
	
	
	
	
	final class MySCMF extends SpaceClassMatcherFactory[String] {
		def apply(reference:String):SpaceClassMatcher[String] = {
			new EqualitySpaceClassMatcher[String](reference)
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
	
	val mockImageSeq:Seq[Image] = (0 to 63).map{new MockImage(_)}
}
