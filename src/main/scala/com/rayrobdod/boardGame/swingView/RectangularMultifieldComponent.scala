package com.rayrobdod.boardGame.swingView

import java.awt.{Image, GridLayout, Point, Shape, Rectangle}
import java.awt.event.MouseListener
import javax.swing.{JLabel, JComponent, Icon, JPanel}
import com.rayrobdod.boardGame.Space
import scala.util.Random

import com.rayrobdod.boardGame.{RectangularField, RectangularSpace,
		SpaceClassConstructor => SpaceConstructor, Space}
import com.rayrobdod.animation.{AnimationIcon, ImageFrameAnimation}
import com.rayrobdod.swing.layouts.LayeredLayout

import com.rayrobdod.boardGame.LoggerInitializer.{rectangularMultifieldLogger => logger}

/**
 * A component that can handle switching between multiple RectangularFields
 * @since 2.1.0
 */
final class RectangularMultifieldComponent extends JComponent with FieldViewer {
	val tokenLayer:JPanel = new JPanel
	private val rng:Random = new Random // TODO: betterize this
	private val lowLayer:JPanel = new JPanel
	private val highLayer:JPanel = new JPanel
	private val transparent = new java.awt.Color(0,0,0,0);
	
	lowLayer.setBackground(transparent)
	lowLayer.setOpaque(false)
	highLayer.setBackground(transparent)
	highLayer.setOpaque(false)
	tokenLayer.setBackground(transparent)
	tokenLayer.setOpaque(false)
	this.setLayout(new LayeredLayout)
	this.add(highLayer)
	this.add(tokenLayer)
	this.add(lowLayer)
	this.doLayout()
	
	
	private var currentField:RectangularField = null
	private var spaceToListeners:Map[Space, Seq[MouseListener]] = Map.empty
	private var spaceToField:Map[Space, RectangularField] = Map.empty
	private var spaceToComponent:Map[Space, JLabel] = Map.empty
	private var fieldToTilesheet:Map[RectangularField, RectangularTilesheet] = Map.empty
	
	def spaceLocation(space:Space):Shape = {
		if (spaceToComponent.contains(space)) {
			logger.finer("Space found: " + spaceToComponent(space).getBounds().toString);
			return spaceToComponent(space).getBounds;
		} else {
			logger.fine("Space not found");
			return new Rectangle(-1, -1, 0, 0);
		}
	}
	
	// TODO: make this matter
	def addMouseListenerToSpace(space:Space, l:MouseListener):Any = {
		spaceToListeners = spaceToListeners + ((space, spaceToListeners.getOrElse(space, Nil) :+ l))
	}
	
	def showSpace(space:Space) = {
		if (spaceToField contains space) {
			logger.finer("Space found ");
			val field = spaceToField(space);
			
			showField(fieldToTilesheet(field), field)
		} else {
			logger.warning("Space not found ");
			throw new NoSuchElementException("Space's field has not been loaded: " + space)
		}
	}
	def showField(tilesheet:RectangularTilesheet, field:RectangularField) = {
		if (currentField != field) {
			currentField = field;
			
			
			val points:Seq[Seq[Point]] = field.spaces.indices.map{(y:Int) => field.spaces(y).indices.map{(x:Int) => new Point(x,y)}}
			val flatPoints:Seq[Point] = points.flatten
			
			val spaces:Seq[RectangularSpace] = flatPoints.map{(p:Point) => field.space(x = p.x, y = p.y)}
			val (lowIcons:Seq[Icon], highIcons:Seq[Icon]) = flatPoints.map{(p:Point) => tilesheet.getIconFor(field, p.x, p.y, rng)}.unzip
			
			lowLayer.removeAll()
			lowLayer.setLayout(new GridLayout(field.spaces.size, -1))
			val lowLabels = lowIcons.map{new JLabel(_)}
			lowLabels.foreach{(x:JLabel) =>
				x.setBackground(transparent)
				x.setOpaque(false)
				lowLayer.add(x)
			}
			highLayer.removeAll()
			highLayer.setLayout(new GridLayout(field.spaces.size, -1))
			highIcons.foreach{(y:Icon) =>
				val x = new JLabel(y)
				x.setBackground(transparent)
				x.setOpaque(false)
				highLayer.add(x)
			}
			
			spaceToComponent = spaces.zip(lowLabels).toMap;
			this.invalidate();
			this.revalidate();
		}
	}
	
	
	def isMapLoaded(field:RectangularField):Boolean = {
		fieldToTilesheet.contains(field)
	}
	
	def loadMap(tilesheet:RectangularTilesheet, field:RectangularField) {
		// memory leaks!
		spaceToField = spaceToField ++ field.spaces.flatten.map{((_, field))}
		fieldToTilesheet = fieldToTilesheet + ((field, tilesheet))
	}
}
