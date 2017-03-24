package com.rayrobdod.boardGame.view

import org.scalatest.FunSpec

final class HorizontalHexagonalIconLocationTest extends FunSpec {
	
	describe("HorizontalHexagonalIconLocation") {
		describe("bounds") {
			it ("0,0,  24,24,8") {
				assertResult(
					new java.awt.Rectangle(0, 0, 24, 24)
				){
					HorizontalHexagonalIconLocation.bounds( ((0, 0)), HorizontalHexagonalDimension(24, 24, 8) )
				}
			}
			it ("0,1,  24,24,8") {
				assertResult(
					new java.awt.Rectangle(12, 16, 24, 24)
				){
					HorizontalHexagonalIconLocation.bounds( ((0, 1)), HorizontalHexagonalDimension(24, 24, 8) )
				}
			}
			it ("1,0,  24,24,8") {
				assertResult(
					new java.awt.Rectangle(24, 0, 24, 24)
				){
					HorizontalHexagonalIconLocation.bounds( ((1, 0)), HorizontalHexagonalDimension(24, 24, 8) )
				}
			}
		}
		describe("hit") {
			it ("0,0,  24,24,8") {
				assertResult( ((0, -1)) ){
					HorizontalHexagonalIconLocation.hit( ((0,0)), HorizontalHexagonalDimension(24, 24, 8) )
				}
			}
			it ("12,12,  24,24,8") {
				assertResult( ((0, 0)) ){
					HorizontalHexagonalIconLocation.hit( ((12,12)), HorizontalHexagonalDimension(24, 24, 8) )
				}
			}
			it ("23,3  24,24,8") {
				assertResult( ((1, -1)) ){
					HorizontalHexagonalIconLocation.hit( ((23,3)), HorizontalHexagonalDimension(24, 24, 8) )
				}
			}
			
			it ("36,16,  24,24,8") {
				assertResult( ((1, 0)) ){
					HorizontalHexagonalIconLocation.hit( ((0+36,0+16)), HorizontalHexagonalDimension(24, 24, 8) )
				}
			}
			it ("44,48,  24,24,8") {
				assertResult( ((1, 1)) ){
					HorizontalHexagonalIconLocation.hit( ((8+36,12+16)), HorizontalHexagonalDimension(24, 24, 8) )
				}
			}
			it ("52,48,  24,24,8") {
				assertResult( ((1, 1)) ){
					HorizontalHexagonalIconLocation.hit( ((16+36,12+16)), HorizontalHexagonalDimension(24, 24, 8) )
				}
			}
			it ("69,19  24,24,8") {
				assertResult( ((2, 0)) ){
					HorizontalHexagonalIconLocation.hit( ((23+36,3+16)), HorizontalHexagonalDimension(24, 24, 8) )
				}
			}
		}
	}
}
