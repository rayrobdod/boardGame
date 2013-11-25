package com.rayrobdod.boardGame.swingView

import java.awt.Shape
import java.awt.event.MouseListener
import javax.swing.JPanel
import com.rayrobdod.boardGame.Space

/**
 * @since 2.1.0
 */
trait FieldViewer {
	
	/**
	 * A canvas that a process can use in whatever way they want.
	 * Will usually show the Tokens moving around this field's field
	 */
	val tokenLayer:JPanel
	
	/**
	 * Tells the location of a particular space. So a component
	 * can say "I want to be on top of this space" and it can move
	 * to the given rectangle to do so.
	 */
	def spaceLocation(space:Space):Shape
	
	/**
	 * 
	 */
	def addMouseListenerToSpace(space:Space, l:MouseListener):Any
	
	/**
	 * Do something such that the specified space is visible.
	 * 
	 * Whether that's switching maps, or moving a containing ScrollPane.
	 */
	def showSpace(space:Space):Any
}
