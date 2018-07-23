package com.rayrobdod.boardGame.view

import org.scalatest.FunSpec
import java.awt.Rectangle
import com.rayrobdod.boardGame.ElongatedTriangularType
import com.rayrobdod.boardGame.ElongatedTriangularType.{NorthTri, Square, SouthTri}
import com.rayrobdod.boardGame.ElongatedTriangularIndex

final class ElongatedTriangularIconLocationTest extends FunSpec {
	
	describe("ElongatedTriangularIconLocation") {
		describe("bounds") {
			def bounds(idxX:Int, idxY:Int, typ:ElongatedTriangularType)(width:Int, squHeight:Int, triHeight:Int):Rectangle = {
				ElongatedTriangularIconLocation.bounds(
					ElongatedTriangularIndex(idxX, idxY, typ), 
					ElongatedTriangularDimension(width, squHeight, triHeight)
				)
			}
			val (width, squHeight, triHeight) = (30, 20, 15)
			
			
			val pairHeight = squHeight + triHeight
			it ("0, 0, NorthTri") {
				assertResult(new Rectangle(0, 0, width, triHeight)){
					bounds(0, 0, NorthTri)(width, squHeight, triHeight)
				}
			}
			it ("0, 0, Square") {
				assertResult(new Rectangle(0, triHeight, width, squHeight)){
					bounds(0, 0, Square)(width, squHeight, triHeight)
				}
			}
			it ("0, 0, SouthTri") {
				assertResult(new Rectangle(0, pairHeight, width, triHeight)){
					bounds(0, 0, SouthTri)(width, squHeight, triHeight)
				}
			}
			
			it ("1, 0, NorthTri") {
				assertResult(new Rectangle(width, 0, width, triHeight)){
					bounds(1, 0, NorthTri)(width, squHeight, triHeight)
				}
			}
			it ("1, 0, Square") {
				assertResult(new Rectangle(width, triHeight, width, squHeight)){
					bounds(1, 0, Square)(width, squHeight, triHeight)
				}
			}
			it ("1, 0, SouthTri") {
				assertResult(new Rectangle(width, pairHeight, width, triHeight)){
					bounds(1, 0, SouthTri)(width, squHeight, triHeight)
				}
			}
			
			it ("0, 1, NorthTri") {
				assertResult(new Rectangle(width / 2, pairHeight, width, triHeight)){
					bounds(0, 1, NorthTri)(width, squHeight, triHeight)
				}
			}
			it ("0, 1, Square") {
				assertResult(new Rectangle(width / 2, pairHeight + triHeight, width, squHeight)){
					bounds(0, 1, Square)(width, squHeight, triHeight)
				}
			}
			it ("0, 1, SouthTri") {
				assertResult(new Rectangle(width / 2, pairHeight + pairHeight, width, triHeight)){
					bounds(0, 1, SouthTri)(width, squHeight, triHeight)
				}
			}
		}
		describe("hit") {
			import ElongatedTriangularIconLocation.hit
			val dims = ElongatedTriangularDimension(30, 20, 15)
			
			it ("15,14   30,20,15") {
				assertResult( ElongatedTriangularIndex(0,0,NorthTri) ){
					hit( ((15, 14)), dims)
				}
			}
			it ("15,16   30,20,15") {
				assertResult( ElongatedTriangularIndex(0,0,Square) ){
					hit( ((15, 16)), dims)
				}
			}
			
			it ("15,34   30,20,15") {
				assertResult( ElongatedTriangularIndex(0,0,Square) ){
					hit( ((15, 34)), dims)
				}
			}
			it ("15,36   30,20,15") {
				assertResult( ElongatedTriangularIndex(0,0,SouthTri) ){
					hit( ((15, 36)), dims)
				}
			}
			
			it ("29,25   30,20,15") {
				assertResult( ElongatedTriangularIndex(0,0,Square) ){
					hit( ((29, 25)), dims)
				}
			}
			it ("31,25   30,20,15") {
				assertResult( ElongatedTriangularIndex(1,0,Square) ){
					hit( ((31, 25)), dims)
				}
			}
			
			it ("-1,25   30,20,15") {
				assertResult( ElongatedTriangularIndex(-1,0,Square) ){
					hit( ((-1, 25)), dims)
				}
			}
			it (" 1,25   30,20,15") {
				assertResult( ElongatedTriangularIndex(0,0,Square) ){
					hit( (( 1, 25)), dims)
				}
			}
			
			it ("4,40    30,20,15") {
				assertResult( ElongatedTriangularIndex(-1,1,NorthTri) ){
					hit( (( 4, 40)), dims)
				}
			}
			it ("6,40    30,20,15") {
				assertResult( ElongatedTriangularIndex(0,0,SouthTri) ){
					hit( (( 6, 40)), dims)
				}
			}
			it ("24,40   30,20,15") {
				assertResult( ElongatedTriangularIndex(0,0,SouthTri) ){
					hit( ((24, 40)), dims)
				}
			}
			it ("26,40   30,20,15") {
				assertResult( ElongatedTriangularIndex(0,1,NorthTri) ){
					hit( ((26, 40)), dims)
				}
			}
			
			it ("9,45    30,20,15") {
				assertResult( ElongatedTriangularIndex(-1,1,NorthTri) ){
					hit( (( 9, 45)), dims)
				}
			}
			it ("11,45   30,20,15") {
				assertResult( ElongatedTriangularIndex(0,0,SouthTri) ){
					hit( ((11, 45)), dims)
				}
			}
			it ("19,45   30,20,15") {
				assertResult( ElongatedTriangularIndex(0,0,SouthTri) ){
					hit( ((19, 45)), dims)
				}
			}
			it ("21,45   30,20,15") {
				assertResult( ElongatedTriangularIndex(0,1,NorthTri) ){
					hit( ((21, 45)), dims)
				}
			}
		}
	}
}
