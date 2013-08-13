package com.rayrobdod.boardGame

import scala.collection.immutable.{Seq, Map}
import java.util.concurrent.{Future => JavaFuture, TimeUnit, TimeoutException}
import scala.parallel.{Future => ScalaPFuture}

/**
 * @version 28 Feb 2013
 */
case class RPGMapSpaceLocation(areaName:String, x:Int, y:Int) {
 	 val left  = RPGMapSpaceLocation(areaName, x-1, y)
 	 val up    = RPGMapSpaceLocation(areaName, x, y-1)
 	 val right = RPGMapSpaceLocation(areaName, x+1, y)
 	 val down  = RPGMapSpaceLocation(areaName, x, y+1)
}

/**
 * @version 28 Feb 2013
 */
abstract trait RPGMap {
	def spaces:Map[RPGMapSpaceLocation, RectangularSpace]
	
	final def areaNames:Set[String] = spaces.keySet.map{_.areaName}
	
	
	// ???
}

/**
 * @version 28 Feb 2013
 */
object RPGMap {
	
	def apply(locationToSpaceClass:Map[RPGMapSpaceLocation, SpaceClass],
			warps:Map[RPGMapSpaceLocation, RPGMapSpaceLocation]) =
	{
		class MapWithIsDefinedAt[A,B](pf:Map[A,B]) {
			def applyOrElse[A1 <: A, B1 >: B](x: A1, default: (A1) ⇒ B1): B1 = {
				if(pf isDefinedAt x) pf(x) else default(x)
			}
		}
		
		// The method is defined properly in Scala 2.10
		implicit def mapWithIsDefinedAt[A,B](pf:Map[A,B]) = {
			new MapWithIsDefinedAt(pf)
		}
		
		class Ident[A] extends scala.runtime.AbstractFunction1[A,A] {
			def apply(a:A) = a;
			
			override def andThen[B](g: (A)⇒B): (A)⇒B = {g}
			override def compose[B](g: (B)⇒A): (B)⇒A = {g}
		}
			
		
		class myRPGMap extends RPGMap {
			
			val spaces = locationToSpaceClass.map({(loc:RPGMapSpaceLocation, spCl:SpaceClass) => 
				val space = new RectangularSpace(spCl,
					new SpaceFuture(this, warps.applyOrElse(loc, new Ident).left ),
					new SpaceFuture(this, warps.applyOrElse(loc, new Ident).up   ),
					new SpaceFuture(this, warps.applyOrElse(loc, new Ident).right),
					new SpaceFuture(this, warps.applyOrElse(loc, new Ident).down )
				)
				
				((loc, space))
			}.tupled)
		}
		
	}
	
	
	
	private final class SpaceFuture(map:RPGMap, l:RPGMapSpaceLocation) extends JavaFuture[Option[RectangularSpace]]
				with ScalaPFuture[Option[RectangularSpace]]
	{
		override def get = apply
		override def apply = {
			while (!isDone) {Thread.sleep(100L)}
			
			map.spaces.get(l)
		}
	
		override def get(timeout:Long, timeUnit:TimeUnit) = {
			Thread.sleep(timeUnit.toMillis(timeout))
		
			if (this.isDone) get() else {
				throw new TimeoutException("RectangularFieldSpaceFuture.get(" + timeout + ":long, " + timeUnit +
						":java.util.concurrent.TimeUnit)")
			}
		}
	
		// in theory, this should never be called in a state when isDone is false.
		override def isDone:Boolean = {map.spaces != null}
		
		override def cancel(ignored:Boolean):Boolean = false
		override def isCancelled:Boolean = false
		
		override def toString = "SpaceFuture []" 
	}
	
}
