package com.rayrobdod.boardGame.swingView

import scala.util.Random
import java.awt.{Image, GridLayout, Point, Component}
import java.awt.event.{MouseListener}
import javax.swing.{JLabel, JComponent, Icon, JPanel}
import com.rayrobdod.boardGame.{RectangularField, RectangularSpace, SpaceClassConstructor => SpaceConstructor}
import com.rayrobdod.animation.{AnimationIcon, ImageFrameAnimation}
import com.rayrobdod.swing.layouts.LayeredLayout

/**
 * A component that displays a RectangularFiled using a certain Tilesheet
 * 
 * @author Raymond Dodge
 * @since 03 Aug 2011
 * @version 2.0
 *  
 * @param tilesheet the tilesheet that images to display are selected from
 * @param field the field that this tile will represent
 * @todo net.verizon.rayrobdod.swing.layouts.LayeredLayout
 */
class FieldComponent(tilesheet:RectangularTilesheet, field:RectangularField, rng:Random) extends JComponent with RectangularFieldComponent
{
	def this(tilesheet:RectangularTilesheet, field:RectangularField) = this(tilesheet, field, Random);
	
	private val transparent = new java.awt.Color(0,0,0,0);
	
	private val points:Seq[Seq[Point]] = field.spaces.indices.map{(y:Int) => field.spaces(y).indices.map{(x:Int) => new Point(x,y)}}
	private val flatPoints:Seq[Point] = points.flatten
	
	private val spaces:Seq[RectangularSpace] = flatPoints.map{(p:Point) => field.space(x = p.x, y = p.y)}
	private val (lowIcons:Seq[Icon], highIcons:Seq[Icon]) = flatPoints.map{(p:Point) => tilesheet.getIconFor(field, p.x, p.y, rng)}.unzip
	
	private val lowLayer:JPanel   = new FieldComponentLayer(lowIcons, field.spaces.size)
	val tokenLayer:JPanel = new JPanel(null)
	private val highLayer = new FieldComponentLayer(highIcons, field.spaces.size)
	
	tokenLayer.setBackground(transparent)
	tokenLayer.setOpaque(false)
	this.add(highLayer)
	this.add(tokenLayer)
	this.add(lowLayer)
	
	// adding animations
	(lowIcons ++ highIcons).filter{_.isInstanceOf[AnimationIcon]}.foreach{(x:Icon) => 
		val animIcon = x.asInstanceOf[AnimationIcon]
		animIcon.addRepaintOnNextFrameListener(FieldComponent.this)
	}
	// TODO: stop threads at some point; or threadpool
	
	private val threads:Seq[Thread] = (lowIcons ++ highIcons).distinct.map{_ match {
		case x:AnimationIcon => {
			val returnValue = new Thread(x.animation, "AnimationIcon animator")
			returnValue.setDaemon(true)
			returnValue.start()
			returnValue
		}
		case _ => {null}
	}}.filter{_ != null}
	
	/**
	 * A map of RectangularSpaces to the JLabel that represents that RectangularSpace
	 */
	private val spaceLabelMap:Map[RectangularSpace, Component] = spaces.zip(lowLayer.getComponents).toMap
	
	def spaceLocation(space:RectangularSpace) = spaceLabelMap(space).getBounds
	def addMouseListenerToSpace(space:RectangularSpace, l:MouseListener) = spaceLabelMap(space).addMouseListener(l)
	def showSpace(space:RectangularSpace) = {}
	
	// overlapping layout
	/* maybe will solve problems? this.setFocusable(true) */
	this.setLayout(new LayeredLayout)
	this.doLayout()
	
	
	private class FieldComponentLayer(icons:Seq[Icon], height:Int)
				extends JPanel(new GridLayout(height, -1))
	{
		val labels = icons.map{new JLabel(_)}
		labels.foreach{(x:JLabel) =>
			x.setBackground(transparent)
			x.setOpaque(false)
			this.add(x)
		}
		this.setBackground(transparent)
		this.setOpaque(false)
	}
}
