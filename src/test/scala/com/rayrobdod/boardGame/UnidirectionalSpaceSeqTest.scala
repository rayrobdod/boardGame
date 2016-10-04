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
package com.rayrobdod.boardGame

import org.scalatest.FunSpec

class UnidirectionalSpaceSeqTest extends FunSpec {
	
	val aSpace = new UnidirectionalSpace("a", None)
	val bSpace = new UnidirectionalSpace("b", Some(aSpace))
	val cSpace = new UnidirectionalSpace("c", Some(bSpace))
	
	
	describe ("UnidirectionalSpaceSeq") {
		describe ("headOption returns the parameter value"){
			it ("none") {
				val tar = None
				val src = new UnidirectionalSpaceSeq(tar)
				val res = src.headOption
				
				assertResult(tar){res}
			}
			it ("Some(aSpace)") {
				val tar = Option(aSpace)
				val src = new UnidirectionalSpaceSeq(tar)
				val res = src.headOption
				
				assertResult(tar){res}
			}
			it ("bSpace") {
				val tar = Option(bSpace)
				val src = new UnidirectionalSpaceSeq(bSpace)
				val res = src.headOption
				
				assertResult(tar){res}
			}
		}
		describe ("head is headOption.get") {
			it ("None") {
				val tar = None
				val src = new UnidirectionalSpaceSeq(tar)
				
				intercept[NoSuchElementException] {
					src.head
				}
			}
			it ("Some(aSpace)") {
				val tar = aSpace
				val src = new UnidirectionalSpaceSeq(Option(tar))
				val res = src.head
				
				assertResult(tar){res}
			}
			it ("bSpace") {
				val tar = bSpace
				val src = new UnidirectionalSpaceSeq(tar)
				val res = src.head
				
				assertResult(tar){res}
			}
		}
		describe ("isEmpty") {
			it ("None") {
				val src = new UnidirectionalSpaceSeq(None)
				assert(src.isEmpty)
			}
			it ("Some(a)") {
				val src = new UnidirectionalSpaceSeq(Option(aSpace))
				assert(! src.isEmpty)
			}
		}
		describe ("length") {
			it ("None") {
				val src = new UnidirectionalSpaceSeq(None)
				assertResult(0){src.length}
			}
			it ("Some(a)") {
				val src = new UnidirectionalSpaceSeq(Option(aSpace))
				assertResult(1){src.length}
			}
			it ("Some(b)") {
				val src = new UnidirectionalSpaceSeq(Option(bSpace))
				assertResult(2){src.length}
			}
			it ("Some(c)") {
				val src = new UnidirectionalSpaceSeq(Option(cSpace))
				assertResult(3){src.length}
			}
		}
		describe ("tail") {
			it ("None") {
				val src = new UnidirectionalSpaceSeq(None)
				intercept[UnsupportedOperationException]{
					src.tail
				}
			}
			it ("Some(a)") {
				val src = new UnidirectionalSpaceSeq(Option(aSpace))
				assertResult(None){src.tail.headOption}
			}
			it ("Some(b)") {
				val src = new UnidirectionalSpaceSeq(Option(bSpace))
				assertResult(Option(aSpace)){src.tail.headOption}
			}
			it ("Some(c)") {
				val src = new UnidirectionalSpaceSeq(Option(cSpace))
				assertResult(Option(bSpace)){src.tail.headOption}
			}
		}
		describe ("apply") {
			it ("rejects negative values") {
				val src = new UnidirectionalSpaceSeq(Option(cSpace))
				intercept[IndexOutOfBoundsException]{
					src(-1)
				}
			}
			it ("accepts 0 (if non-empty)") {
				val src = new UnidirectionalSpaceSeq(Option(cSpace))
				assertResult(cSpace){src(0)}
			}
			it ("accepts length-1 (if non-empty)") {
				val src = new UnidirectionalSpaceSeq(Option(cSpace))
				assertResult(aSpace){src(src.length - 1)}
			}
			it ("rejects length") {
				val src = new UnidirectionalSpaceSeq(Option(cSpace))
				intercept[IndexOutOfBoundsException]{
					src(src.length)
				}
			}
			it ("rejects 0 (if empty)") {
				val src = new UnidirectionalSpaceSeq(None)
				intercept[IndexOutOfBoundsException]{
					src(0)
				}
			}
		}
	}
}
