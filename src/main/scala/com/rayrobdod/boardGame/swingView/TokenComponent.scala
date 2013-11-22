package com.rayrobdod.boardGame.swingView

import javax.swing.{JLabel, Icon}
import com.rayrobdod.swing.layouts.MoveToLayout
import com.rayrobdod.boardGame.{Token, Space, RectangularSpace}
import scala.runtime.{AbstractFunction1, AbstractFunction2}

/**
 * A component that shows a label and moves around a FieldComponent as a token moves around a field.
 * 
 * @author Raymond Dodge
 * @since 04 Aug 2011
 * @version 2.1.0
 */
class TokenComponent(token:Token, fieldComp:FieldViewer, layout:MoveToLayout, icon:Icon) extends JLabel(icon)
{
	token.moveReactions_+=(ComponentMovementUpdateAct)
	object ComponentMovementUpdateAct extends AbstractFunction2[Space, Boolean, Unit] {
		def apply(movedTo:Space, landed:Boolean) = {
			val location = new java.awt.Point(
					(fieldComp.spaceLocation(movedTo).getBounds2D().getCenterX() - TokenComponent.this.getWidth()  / 2).toInt,
					(fieldComp.spaceLocation(movedTo).getBounds2D().getCenterY() - TokenComponent.this.getHeight() / 2).toInt
			);
			layout.moveTo(TokenComponent.this, location)
		}
	}
	
	token.selectedReactions_+=(BeSelectedAct)
	object BeSelectedAct extends AbstractFunction1[Boolean, Unit] {
		def apply(b:Boolean) = {
			TokenComponent.this.setOpaque(b)
			TokenComponent.this.repaint()
		}
	}
	
	this.setBackground(java.awt.Color.green)
}
