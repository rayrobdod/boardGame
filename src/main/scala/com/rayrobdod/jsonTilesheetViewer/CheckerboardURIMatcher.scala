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
		tileDimension:java.awt.Dimension = new java.awt.Dimension(24, 24),
		light:java.awt.Color = java.awt.Color.white,
		dark:java.awt.Color = java.awt.Color.black
	) {
		def apply[Icon](
			transparentIcon:Function1[java.awt.Dimension, Icon],
			rgbToIcon:Function2[java.awt.Color, java.awt.Dimension, Icon]
		):CheckerboardTilesheet[Icon] = {
			CheckerboardTilesheet(
				transparentIcon(tileDimension),
				rgbToIcon(light, tileDimension),
				rgbToIcon(dark, tileDimension)
			)
		}
	}
	
	
	private def build(params:Seq[String]) = {
		
		params.foldLeft[Option[CheckerboardTilesheetDelay]](Option(new CheckerboardTilesheetDelay())){(foldingOpt, param:String) => foldingOpt.flatMap{folding =>
			val splitParam = param.split("=");
			splitParam(0) match {
				case "size" => {
					scala.util.Try( folding.copy(
						tileDimension = new java.awt.Dimension(
							splitParam(1).toInt,
							splitParam(1).toInt
						)
					)).toOption
				}
				case "light" => {
					scala.util.Try( folding.copy( light = new java.awt.Color(splitParam(1).toInt) ) ).toOption
				}
				case "dark" => {
					scala.util.Try( folding.copy( dark = new java.awt.Color(splitParam(1).toInt) ) ).toOption
				}
				case _ => { foldingOpt }
			}
		}}
	}
}
