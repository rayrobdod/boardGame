package com.rayrobdod.boardGame

import org.scalatest.FunSpec
import com.rayrobdod.boardGame.{StrictRectangularSpace => SRS}
import com.rayrobdod.boardGame.{StrictRectangularSpaceViaFutures => SRSVF}

final class PrebuiltRectangularFieldTest extends FunSpec {
	object ThreeByThree {
		val ne:SRS[String] = new SRSVF("ne", () => Option(n), () => None, () => None, () => Option(e))
		val n:SRS[String]  = new SRSVF("n" , () => Option(nw), () => None, () => Option(ne), () => Option(z))
		val nw:SRS[String] = new SRSVF("nw", () => None, () => None, () => Option(n), () => Option(w))
		val e:SRS[String]  = new SRSVF("e", () => Option(z), () => Option(ne), () => None, () => Option(se))
		val z:SRS[String]  = new SRSVF("z", () => Option(w), () => Option(n), () => Option(e), () => Option(s))
		val w:SRS[String]  = new SRSVF("w", () => None, () => Option(nw), () => Option(z), () => Option(sw))
		val se:SRS[String] = new SRSVF("se", () => Option(s), () => Option(e), () => None, () => None)
		val s:SRS[String]  = new SRSVF("s", () => Option(sw), () => Option(z), () => Option(se), () => None)
		val sw:SRS[String] = new SRSVF("sw", () => None, () => Option(w), () => Option(s), () => None)
	}
	
	
	describe ("ThreeByThree") {
		describe ("field(zeroZero = _.z)") {
			val field = new PrebuiltRectangularField(ThreeByThree.z)
			
			def doThing( x:Int, y:Int, space:Option[StrictRectangularSpace[String]] ) {
				val spaceStr = space.map{_.typeOfSpace}.getOrElse{"None"}
				it (s"space at $x,$y is _.$spaceStr") {
					assertResult(space){field.space((x, y))}
				}
				it (s"spaceClass at $x,$y is _.$spaceStr") {
					assertResult(space.map{_.typeOfSpace}){field.spaceClass((x, y))}
				}
				space.foreach{s =>
					it (s"containsIndex ($x,$y)") {
						assert(field.contains(x, y))
					}
				}
			}
			
			doThing(-1,-1, Option(ThreeByThree.ne))
			doThing(-1,0, Option(ThreeByThree.e))
			doThing(-1,1, Option(ThreeByThree.se))
			doThing(0,-1, Option(ThreeByThree.n))
			doThing(0,0, Option(ThreeByThree.z))
			doThing(0,1, Option(ThreeByThree.s))
			doThing(1,-1, Option(ThreeByThree.nw))
			doThing(1,0, Option(ThreeByThree.w))
			doThing(1,1, Option(ThreeByThree.sw))
			doThing(-2,0, None)
			doThing(2,0, None)
			doThing(0,-2, None)
			doThing(0,2, None)
			
			it ("has 9 indexies") {
				assertResult(9){field.mapIndex{x => x}.size}
			}
			it ("All indexies fit in the expected range") {
				field.foreachIndex{xy =>
					val (x, y) = xy
					assert(-1 <= x && x <= 1)
					assert(-1 <= y && y <= 1)
				}
			}
		}
		describe ("zeroZero = _.ne") {
			val field = new PrebuiltRectangularField(ThreeByThree.ne)
			
			def doThing( x:Int, y:Int, space:Option[StrictRectangularSpace[String]] ) {
				val spaceStr = space.map{_.typeOfSpace}.getOrElse{"None"}
				it (s"$x,$y is _.$spaceStr") {
					assertResult(space){field.space((x, y))}
				}
			}
			
			doThing(0,0, Option(ThreeByThree.ne))
			doThing(0,1, Option(ThreeByThree.e))
			doThing(0,2, Option(ThreeByThree.se))
			doThing(1,0, Option(ThreeByThree.n))
			doThing(1,1, Option(ThreeByThree.z))
			doThing(1,2, Option(ThreeByThree.s))
			doThing(2,0, Option(ThreeByThree.nw))
			doThing(2,1, Option(ThreeByThree.w))
			doThing(2,2, Option(ThreeByThree.sw))
			doThing(-1,0, None)
			doThing(3,0, None)
			doThing(0,-1, None)
			doThing(0,3, None)
			
			it ("has 9 indexies") {
				assertResult(9){field.mapIndex{x => x}.size}
			}
			it ("All indexies fit in the expected range") {
				field.foreachIndex{xy =>
					val (x, y) = xy
					assert(0 <= x && x <= 2)
					assert(0 <= y && y <= 2)
				}
			}
		}
	}
}
