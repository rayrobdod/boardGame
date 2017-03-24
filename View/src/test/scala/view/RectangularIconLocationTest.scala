package com.rayrobdod.boardGame.view

import org.scalatest.FunSpec

final class RectangularIconLocationTest extends FunSpec {
	
	describe("RectangularIconLocation") {
		describe("bounds") {
			it ("0,0,15,15") {
				assertResult(
					new java.awt.Rectangle(0, 0, 15, 15)
				){
					RectangularIconLocation.bounds( ((0, 0)), RectangularDimension(15, 15) )
				}
			}
			it ("1,2,3,4") {
				assertResult(
					new java.awt.Rectangle(3, 8, 3, 4)
				){
					RectangularIconLocation.bounds( ((1, 2)), RectangularDimension(3, 4) )
				}
			}
			it ("do thing") {
				for (
					x <- (-5 to 5);
					y <- (-5 to 5);
					w <- (1 to 20);
					h <- (1 to 20)
				) {
					assertResult(
						new java.awt.Rectangle(x * w, y * h, w, h)
					){
						RectangularIconLocation.bounds( ((x,y)), RectangularDimension(w,h) )
					}
				}
			}
		}
		describe("hit") {
			it ("55,75,20,10") {
				assertResult(
					(( 2, 7 ))
				){
					RectangularIconLocation.hit( ((55, 75)), RectangularDimension(20, 10) )
				}
			}
			it ("do thing") {
				for (
					x <- (-10 to 10);
					y <- (-10 to 10);
					w <- (1 to 5);
					h <- (1 to 5)
				) {
					assertResult(
						(( x / w, y / h ))
					){
						RectangularIconLocation.hit( ((x,y)), RectangularDimension(w,h) )
					}
				}
			}
		}
	}
}
