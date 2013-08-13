package com.rayrobdod.boardGame

import scala.swing.Reactions.Reaction
import scala.swing.event.Event

/**
 * A Token extension that keeps track of the direction a token moves
 * 
 * @author Raymond Dodge
 * @version 2013 Jun 03 
 */
trait WithRectangularFacingAct extends Token {
	object Directions {
		sealed trait Direction {
			protected def inner(a:RectangularSpace):Option[Space]
			def apply(a:RectangularSpace):Option[Space] = this(Option(a))
			def apply(a:Option[RectangularSpace]):Option[Space] = a match {
				// TODO: figure out a way to do this idiomatically
				case None => None
				case Some(b:RectangularSpace) => inner(b)
			}
		}
		object Left  extends Direction {protected def inner(a:RectangularSpace) = a.left  }
		object Up    extends Direction {protected def inner(a:RectangularSpace) = a.up    }
		object Right extends Direction {protected def inner(a:RectangularSpace) = a.right }
		object Down  extends Direction {protected def inner(a:RectangularSpace) = a.down  }
		
		def values = Seq(Left, Up, Right, Down)
	}
	import Directions._
	
	private var _isFacing:Direction = Directions.Down
	/** the direction the token is facing */
	def isFacing:Direction = _isFacing
	def facing = currentSpace match {
		case rs:RectangularSpace => isFacing(rs)
		case _ => None
	}
	
	this.reactions += RectangularFacingAct
	/** A reaction that changes  */
	object RectangularFacingAct extends Reaction {
		private var previousSpace:RectangularSpace = null
		
		def apply(event:Event) {
			event match {
				case Moved(movedTo:RectangularSpace, landed:Boolean) => {
					WithRectangularFacingAct.this._isFacing = {
						Directions.values.filter{(dir:Direction) =>
							dir(previousSpace) == Some(movedTo)
						}.headOption.getOrElse{
							WithRectangularFacingAct.this._isFacing
						}
					}
					
					this.previousSpace = movedTo
				}
				case _ => {}
			}
		}
		
		def isDefinedAt(event:Event):Boolean = {
			event match {
				case Moved(movedTo:RectangularSpace, landed:Boolean) => true
				case _ => false
			}
		}
	}
}
