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

import org.scalatest.FunSpec

class PackageTest extends FunSpec {
	
	describe("gcd") {
		it ("1,1")  { assertResult(1) {gcd(1,1)} }
		it ("1,2")  { assertResult(1) {gcd(1,2)} }
		it ("2,1")  { assertResult(1) {gcd(2,1)} }
		it ("2,2")  { assertResult(2) {gcd(2,2)} }
		it ("2,3")  { assertResult(1) {gcd(2,3)} }
		it ("6,9")  { assertResult(3) {gcd(6,9)} }
		it ("8,12") { assertResult(4) {gcd(8,12)} }
		it ("12,8") { assertResult(4) {gcd(12,8)} }
	}
	
	describe("lcm") {
		it ("1,1") { assertResult(1) {lcm(1,1)} }
		it ("2,1") { assertResult(2) {lcm(2,1)} }
		it ("1,2") { assertResult(2) {lcm(1,2)} }
		it ("2,2") { assertResult(2) {lcm(2,2)} }
		it ("2,3") { assertResult(6) {lcm(2,3)} }
		it ("6,9") { assertResult(18){lcm(6,9)} }
	}
}
