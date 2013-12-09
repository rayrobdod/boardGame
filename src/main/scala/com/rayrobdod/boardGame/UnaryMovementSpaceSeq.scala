/*
	Deduction Tactics
	Copyright (C) 2012-2013  Raymond Dodge

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

import scala.collection.immutable.Set
import com.rayrobdod.boardGame.{Token => BoardGameToken}
import scala.collection.immutable.LinearSeq
import scala.collection.LinearSeqOptimized

/**
 * Noticed that the UnaryMovement Spaces look like linked list nodes
 * So, I made a linked list around the UnaryMovement Spaces.
 * 
 * I haven't grown out of my habit of making useless collections yet…
 * 
 * @author Raymond Dodge
 * @version 2.1.0 rename from UnaryMovementSpaceSeq to UnidirectionalSpaceSeq
 */
class UnidirectionalSpaceSeq[A <: UnidirectionalSpace](override val headOption:Option[A])
			extends LinearSeq[A] 
{
	import UnidirectionalSpaceSeqLoggerInitializer.{unaryLogger => logger}
	logger.entering("UnidirectionalSpaceSeq", "this(Option[A])", headOption)
	
	def this(head:A) = this(Option(head))
	
	override def head = headOption.get
	override def isEmpty = (headOption == None) // assume no nulls, right?
	override def tail:UnidirectionalSpaceSeq[A] = // TRYTHIS test to see if it is worth making this a lazy val: Infinite Seq is probably possible (e.g. Monopoly)
	{
		logger.entering("UnidirectionalSpaceSeq", "tail()")
		logger.finer(this.head.toString)
		
		val next:Option[Space] = headOption.flatMap(_.nextSpace)
		val nextAsUnary:Option[A] = next.filter(_.isInstanceOf[A]).asInstanceOf[Option[A]]
		
		new UnidirectionalSpaceSeq[A](nextAsUnary)
	}
	
	override def apply(i:Int):A = 
	{
		if (this.isEmpty) throw new IndexOutOfBoundsException("Too high an index called")
		else if (i < 0) throw new IndexOutOfBoundsException("index less than zero called: " + i)
		else if (i == 0) return head
		else return tail.apply(i - 1)
	}
	
	override def length:Int =
	{
		if (this.isEmpty) return 0
		else return (tail.length + 1)
	}
}

private[boardGame] object UnidirectionalSpaceSeqLoggerInitializer
{
	import java.util.logging.{Logger, Level, ConsoleHandler}
	
	val warningConsoleHander = new ConsoleHandler()
	warningConsoleHander.setLevel(Level.WARNING)
	
	val finerConsoleHander = new ConsoleHandler()
	finerConsoleHander.setLevel(Level.FINER)
	
	val unaryLogger = Logger.getLogger("net.verizon.rayrobdod.boardGame.UnidirectionalSpaceSeq")
	unaryLogger.addHandler(finerConsoleHander)
	unaryLogger.setLevel(Level.WARNING)
}
