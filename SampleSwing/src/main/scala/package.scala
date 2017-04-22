/*
	Deduction Tactics
	Copyright (C) 2012-2017  Raymond Dodge

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
package com.rayrobdod

import java.awt.{Insets, GridBagConstraints}
import java.nio.charset.StandardCharsets.UTF_8
import scala.collection.immutable.Seq
import scala.util.Random
import com.rayrobdod.json.parser.JsonParser
import com.rayrobdod.boardGame._
import com.rayrobdod.boardGame.view._

/**
 * 
 */
package object jsonTilesheetViewer {
	type SpaceClass = String
	
	/** @group tagUri */
	val TAG_MAP_ROTATE:String = "tag:rayrobdod.name,2013-08:map-rotate"
	
	/** @group tagUri */
	val TAG_SHEET_NIL:String = "tag:rayrobdod.name,2013-08:tilesheet-nil"
	/** @group tagUri */
	val TAG_SHEET_INDEX:String = "tag:rayrobdod.name,2013-08:tilesheet-indexies"
	/** @group tagUri */
	val TAG_SHEET_RAND:String = "tag:rayrobdod.name,2013-08:tilesheet-randcolor"
	/** @group tagUri */
	val TAG_SHEET_HASH:String = "tag:rayrobdod.name,2015-06-12:tilesheet-hashcolor"
	/** @group tagUri */
	val TAG_SHEET_CHECKER:String = "tag:rayrobdod.name,2013-08:tilesheet-checker"
	
	
	
	private def urlOrFileStringToUrl(s:String):java.net.URL = {
		try {
			new java.net.URL(s)
		} catch {
			case e:java.net.MalformedURLException =>
						new java.io.File(s).toURI.toURL
		}
	}
	
	def allClassesInTilesheet(f:com.rayrobdod.boardGame.view.Tilesheet[SpaceClass, _, _, _]):Seq[SpaceClass] = {
		import com.rayrobdod.boardGame.SpaceClassMatcher
		import com.rayrobdod.boardGame.view.ParamaterizedVisualizationRule
		import com.rayrobdod.boardGame.view.VisualizationRuleBasedTilesheet
		import com.rayrobdod.boardGame.view.HashcodeColorTilesheet
		import StringSpaceClassMatcherFactory.EqualsMatcher
		
		val a = f match {
			case x:VisualizationRuleBasedTilesheet[SpaceClass, _, _, _, _] => {
				val a:Seq[ParamaterizedVisualizationRule[SpaceClass, _, _]] = x.visualizationRules.map{_.asInstanceOf[ParamaterizedVisualizationRule[SpaceClass, _, _]]}
				val b:Seq[Map[_, SpaceClassMatcher[SpaceClass]]] = a.map{_.surroundingTiles}
				val c:Seq[Seq[SpaceClassMatcher[SpaceClass]]] = b.map{(a) => (Seq.empty ++ a.toSeq).map{_._2}}
				val d:Seq[SpaceClassMatcher[SpaceClass]] = c.flatten
				
				val e:Seq[Option[SpaceClass]] = d.map{_ match {
					case EqualsMatcher(ref) => Option(ref)
					case _ => None
				}}
				val f:Seq[SpaceClass] = e.flatten.distinct
				
				f
			}
			// designed to be one of each color // green, blue, red, white
			//case x:HashcodeColorTilesheet[SpaceClass] => Seq("AWv", "Ahf", "\u43c8\u0473\u044b", "")
			case x:HashcodeColorTilesheet[_, _, _] => Seq("a", "b", "c", "d")
			case _ => Seq("")
		}
		
		a
	}
	
	
	def nameToTilesheet[IconPart, Icon](
		  url:String
		, dimProps:NameToTilesheetDemensionType[IconPart, Icon]
	):Tilesheet[SpaceClass, dimProps.templateProps.Index, dimProps.Dimension, Icon] = {
		url match {
			case TAG_SHEET_NIL => dimProps.template.NilTilesheet(dimProps.nilTilesheetDimension)(dimProps.templateProps)
			case TAG_SHEET_INDEX => dimProps.template.IndexesTilesheet(dimProps.textIncludingDimension)(dimProps.templateProps)
			case TAG_SHEET_RAND => dimProps.template.RandomColorTilesheet(dimProps.textIncludingDimension)(dimProps.templateProps)
			case TAG_SHEET_HASH => dimProps.template.HashcodeColorTilesheet(dimProps.hashTilesheetDimension)(dimProps.templateProps)
			case CheckerboardURIMatcher(x) => dimProps.checkerboardTilesheet(x)
			case x:String => {
				val url = urlOrFileStringToUrl(x)
				var r:java.io.Reader = new java.io.StringReader("{}");
				try {
					r = new java.io.InputStreamReader(url.openStream(), UTF_8);
					val parser = new JsonParser()
					dimProps.parseVisualizationRuleTilesheet(parser, r, url)
				} finally {
					r.close();
				}
			}
		}
	}
	
	def nameToRandom(s:String):Random = s match{
		case "" => Random
		case "a" => new Random(new java.util.Random(){override def next(bits:Int):Int = 1})
		case "b" => new Random(new java.util.Random(){override def next(bits:Int):Int = 0})
		case s => try {
			new Random(s.toLong)
		} catch {
			case e:NumberFormatException => {
				throw new IllegalStateException(
						"Seed must be '', 'a', 'b' or an integer",
						e
				)
			}
		}
	}
	
	def nameToField(
		  url:String
		, props:NameToTilesheetDemensionType[_, _]
	):Tiling[SpaceClass, props.templateProps.Index, props.SpaceType] = {
		import java.io.InputStreamReader
		import com.opencsv.CSVReader
		
		val layoutReader = new InputStreamReader(urlOrFileStringToUrl(url).openStream(), UTF_8)
		val layoutTable:Seq[Seq[String]] = {
			import scala.collection.JavaConverters.collectionAsScalaIterableConverter
			
			val reader = new CSVReader(layoutReader);
			val letterTable3 = reader.readAll();
			val letterTable = letterTable3.asScala.map{_.to[Seq]}.to[Seq]
			
			letterTable
		}
		
		props.arbitraryField( layoutTable )
	}
	
	/**
	 * A factory for [[java.awt.GridBagConstraints]].
	 * 
	 * This uses the new method of GridBagConstraints; the main difference is that this has default parameters.
	 */
	def gridBagConstraints(
			gridx:Int = GridBagConstraints.RELATIVE,
			gridy:Int = GridBagConstraints.RELATIVE,
			gridwidth:Int = 1,
			gridheight:Int = 1,
			weightx:Double = 0,
			weighty:Double = 0,
			anchor:Int = GridBagConstraints.CENTER,
			fill:Int = GridBagConstraints.NONE,
			insets:Insets = new Insets(0,0,0,0),
			ipadx:Int = 0,
			ipady:Int = 0
	) = new GridBagConstraints(
			gridx, gridy, gridwidth, gridheight,
			weightx, weighty, anchor, fill, insets, ipadx, ipady
	)
}
