package com.rayrobdod.junit.boardGame.swingView

import java.awt.Color
import java.awt.Dimension
import scala.util.Random
import com.rayrobdod.swing.SolidColorIcon
import com.rayrobdod.boardGame.RectangularField
import scala.collection.immutable.Seq
import com.rayrobdod.jsonTilesheetViewer.{SpaceClass1 => ArbitrarySpace}


/**
 *
 * @version 25 Aug 2012 (maybe)
 * @version 22 Sept 2012 - moving from com.rayrobdod.boardGame.swingView.testing to com.rayrobdod.junit.boardGame.swingView
 */
object CheckerboardTilesheetTest extends App {
	
	val field = RectangularField.applySCC(Seq.fill(8,8){ArbitrarySpace})
	
	new javax.swing.JFrame("CheckerboardTest") {
		add(new FieldComponent(CheckerboardTilesheet(dim = new Dimension(32,32)), field))
		pack()
		setVisible(true)
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
	}
}