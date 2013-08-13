package com.rayrobdod.boardGame

import scala.collection.immutable.Seq

/**
 * An implmentation of a RectangularField
 * 
 * @author Raymond Dodge
 * @version 30 Jul 2011
 * @version 02 Aug 2011 - renamed to RPGField from TileRegionMap
 * @version 04 Aug 2011 - made tiles load in correct order (was loading south into east, west into south, etc.)
 * @version 06 Aug 2011 - renamed from RPGField to RectangularSpaceConstructedField
 * @version 06 Aug 2011 - moved from net.verizon.rayrobdod.rpgTest.model to net.verizon.rayrobdod.boardGame
 * @version 15 Dec 2011 - moved from {@code net.verizon.rayrobdod.boardGame} to {@code com.rayrobdod.boardGame}
 * @version 2013 Aug 06 - Apparently 'Future' in scala means 'there's a thing that I will want in the future', not 
 			'there's a thing that will become availiable in the futrue'. Either way, scala.parellel.Future no longer exists,
 			as of Scala 2.11. Using `scala.Function0` instead.
 * @deprecated 29 Sept 2011 - RectangularSpaceConstructor was depreciated
 */
class RectangularSpaceConstructedField(consIndexSeqIndexSeq:Seq[(Seq[(RectangularSpaceConstructor,Int)],Int)]) extends RectangularField
{
	override val spaces:Seq[Seq[RectangularSpace]] = consIndexSeqIndexSeq.map{
				(consIndexSeqi:(Seq[(RectangularSpaceConstructor,Int)], Int)) =>
		val (consIndexSeq,i) = consIndexSeqi
		consIndexSeq.map{(scj:(RectangularSpaceConstructor, Int)) =>
			val (sc, j) = scj
			sc(spaceFuture(i,j-1), spaceFuture(i-1,j), spaceFuture(i,j+1), spaceFuture(i+1,j))
		}
	}
}

/**
 * A factory for {@link RectangularSpaceConstructedField}s, as well as a set of methods used by those constructors
 * 
 * @author Raymond Dodge
 * @version 30 Jul 2011
 * @version 02 Aug 2011 - renamed to RPGField from TileRegionMap
 * @version 06 Aug 2011 - renamed from RPGField to RectangularSpaceConstructedField
 * @version 06 Aug 2011 - moved from net.verizon.rayrobdod.rpgTest.model to net.verizon.rayrobdod.boardGame
 * @version 15 Dec 2011 - moved from {@code net.verizon.rayrobdod.boardGame} to {@code com.rayrobdod.boardGame}
 * @deprecated 29 Sept 2011 - RectangularSpaceConstructor was depreciated
 */
object RectangularSpaceConstructedField
{
	def applyIndexed(consIndexSeqIndexSeq:Seq[(Seq[(RectangularSpaceConstructor,Int)],Int)]):RectangularSpaceConstructedField = 
			new RectangularSpaceConstructedField(consIndexSeqIndexSeq)
	
	def apply(consIndexSeqIndexSeq:Seq[Seq[RectangularSpaceConstructor]]):RectangularSpaceConstructedField = 
			new RectangularSpaceConstructedField(layeredZipWithIndex(consIndexSeqIndexSeq)) 
	
	def apply(strStrMap:Map[String,String], strSeqSeq:Seq[Seq[String]]):RectangularSpaceConstructedField = 
			new RectangularSpaceConstructedField(turnIntoConsIndexSeqIndexSeq(strStrMap, strSeqSeq))
	
	def getObjectAsSpaceConstructor(objectName:String):RectangularSpaceConstructor =
	{
		val clazz = Class.forName(objectName + "$")
		val field = clazz.getField("MODULE$")
		
		field.get(null).asInstanceOf[RectangularSpaceConstructor]
	}
	
	def desterilizeObjectNames(strStrMap:Map[String,String]):Map[String, RectangularSpaceConstructor] =
	{
		strStrMap.mapValues{getObjectAsSpaceConstructor(_)}
	}
	
	def layeredZipWithIndex[A](aSeqSeq:Seq[Seq[A]]):Seq[(Seq[(A,Int)],Int)] =
	{
		aSeqSeq.map{_.zipWithIndex}.zipWithIndex
	}
	
	
	def turnIntoConsIndexSeqIndexSeq(strStrMap:Map[String, String], strSeqSeq:Seq[Seq[String]]) =
	{
		val strConsMap = desterilizeObjectNames(strStrMap)
		val consSeqSeq = strSeqSeq.map{_.map{strConsMap}}
		val consIndexSeqIndexSeq = layeredZipWithIndex(consSeqSeq)
		
		consIndexSeqIndexSeq
	}
}
