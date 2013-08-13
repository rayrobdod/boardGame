package com.rayrobdod.boardGame.swingView

import java.awt.Color
import java.awt.Dimension
import scala.util.Random
import com.rayrobdod.swing.SolidColorIcon
import com.rayrobdod.boardGame.RectangularField
import scala.collection.immutable.Seq
import com.rayrobdod.jsonTilesheetViewer.{SpaceClass1 => ArbitrarySpace}


/**
 * @author Raymond Dodge
 * @version 25 Aug 2012 (maybe)
 * @version 22 Sept 2012 - moving from com.rayrobdod.boardGame.swingView.testing to com.rayrobdod.junit.boardGame.swingView
 */
object IndexesTilesheetTest extends App {
	
	val field = RectangularField.applySCC(Seq.fill(4,8){ArbitrarySpace})
	
	new javax.swing.JFrame("IndexesTilesheetTest") {
		add(new FieldComponent(IndexesTilesheet, field))
		pack()
		setVisible(true)
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
	}
}