package com.rayrobdod.jsonTilesheetViewer

import java.awt.{Color, Dimension}
import com.rayrobdod.boardGame.swingView.CheckerboardTilesheet

object CheckerboardURIMatcher {
	
	def unapply(ssp:String):Option[CheckerboardTilesheet] = {
		val split = ssp.split("[\\?\\&]");
		
		if ("rayrobdod.name,2013-08:tilesheet-checker" == split.head)
		{
			build(split.tail)
		} else {
			None;
		}
	}
	
	def unapply(ssp:java.net.URI):Option[CheckerboardTilesheet] = {
		this.unapply(ssp.getSchemeSpecificPart())
	}
	
	def unapply(ssp:java.net.URL):Option[CheckerboardTilesheet] = {
		if (ssp.getProtocol == "tag" &&
				ssp.getAuthority == "rayrobdod.name,2013-08" &&
				ssp.getPath == "tilesheet-checker") {
			
			val split = ssp.getQuery.split("\\&");
			
			build(split);
		} else {
			None
		}
	}
	
	
	private def build(params:Seq[String]) = {
		var returnValue = new CheckerboardTilesheet()
		
		params.foreach{(param:String) =>
			val splitParam = param.split("=");
			splitParam(0) match {
				case "size" => {
					returnValue = returnValue.copy(
						dim = new Dimension(splitParam(1).toInt,
								splitParam(1).toInt)
					)
				}
				case "light" => {
					returnValue = returnValue.copy(
						light = new Color(splitParam(1).toInt)
					)
				}
				case "dark" => {
					returnValue = returnValue.copy(
						dark = new Color(splitParam(1).toInt)
					)
				}
				case _ => {}
			}
		}
		
		Some(returnValue)
	}
}
