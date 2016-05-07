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
package com.rayrobdod.jsonTilesheetViewer

import java.awt.{Color, Dimension}
import com.rayrobdod.boardGame.view.CheckerboardTilesheet

object CheckerboardURIMatcher {
	
	def unapply(ssp:String):Option[CheckerboardTilesheetDelay] = {
		val split = ssp.split("[\\?\\&]");
		
		if (TAG_SHEET_CHECKER == split.head) {
			build(split.tail)
		} else {
			None;
		}
	}
	
	final case class CheckerboardTilesheetDelay(
		tileWidth:Int = 24,
		tileHeight:Int = 24,
		light:Int = 0xFFFFFF,
		dark:Int = 0
	) {
		def apply[Icon](
			transparentIcon:Function2[Int, Int, Icon],
			rgbToIcon:Function3[Int, Int, Int, Icon]
		):CheckerboardTilesheet[Icon] = {
			CheckerboardTilesheet(
				transparentIcon(tileWidth, tileHeight),
				rgbToIcon(light, tileWidth, tileHeight),
				rgbToIcon(dark, tileWidth, tileHeight)
			)
		}
	}
	
	
	private def build(params:Seq[String]) = {
		var returnValue = new CheckerboardTilesheetDelay()
		
		params.foreach{(param:String) =>
			val splitParam = param.split("=");
			splitParam(0) match {
				case "size" => {
					returnValue = returnValue.copy(
						tileWidth = splitParam(1).toInt,
						tileHeight = splitParam(1).toInt
					)
				}
				case "light" => {
					returnValue = returnValue.copy(
						light = splitParam(1).toInt
					)
				}
				case "dark" => {
					returnValue = returnValue.copy(
						dark = splitParam(1).toInt
					)
				}
				case _ => {}
			}
		}
		
		Some(returnValue)
	}
}
