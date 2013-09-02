package com.rayrobdod.jsonTilesheetViewer

import scala.collection.immutable.Seq
import scala.util.Random

import java.net.{URL, URI}
import java.awt.{BorderLayout, GridLayout, GridBagLayout, GridBagConstraints}
import java.awt.event.{ActionListener, ActionEvent, MouseAdapter, MouseEvent}
import javax.swing.{JFrame, JPanel, JTextField, JLabel, JButton, JOptionPane}
import com.rayrobdod.swing.GridBagConstraintsFactory

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Path, Paths, Files}

import com.rayrobdod.boardGame.swingView.{CheckerboardTilesheet,
		FieldComponent, NilTilesheet, IndexesTilesheet, RandomColorTilesheet,
		JSONRectangularTilesheet => JSONTilesheet,
		RectangularTilesheet => Tilesheet
}
import com.rayrobdod.boardGame.{
		SpaceClassConstructor, RectangularField => Field
}
import com.rayrobdod.javaScriptObjectNotation.parser.JSONParser
import com.rayrobdod.javaScriptObjectNotation.parser.listeners.ToScalaCollection
import com.rayrobdod.commaSeparatedValues.parser.{CSVParser, ToSeqSeqCSVParseListener, CSVPatterns}
//import com.rayrobdod.deductionTactics.swingView.FieldChessTilesheet


/**
 * @author Raymond Dodge
 * @todo I'd love to be able to add an ability to seed the RNG, but the tilesheets are apparently too nondeterministic.
 */
object JSONTilesheetViewer extends App
{
	val frame = new JFrame("JSON Tilesheet Viewer")
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
	
	val tileUrl:String = if (args.size > 0) args(0) else "tag:rayrobdod.name,2013-08:tilesheet-nil"
	val mapUrl:String  = if (args.size > 1) args(1) else "tag:rayrobdod.name,2013-08:map-rotate"
	val rand:String    = if (args.size > 2) args(2) else ""
	
	val tileUrlBox = new JTextField(tileUrl)
	val mapUrlBox = new JTextField(mapUrl)
	val randBox = new JTextField(rand,5)
	val goButton = new JButton("Go")
	goButton.addActionListener(new ActionListener() {
		override def actionPerformed(e:ActionEvent) {
			loadNewTilesheet()
		}
	})
	
	val label = GridBagConstraintsFactory(insets = new java.awt.Insets(0,5,0,5))
	val endOfLine = GridBagConstraintsFactory(gridwidth = GridBagConstraints.REMAINDER, weightx = 1, fill = GridBagConstraints.BOTH)
	
	val navPanel = new JPanel(new GridBagLayout)
	navPanel.add(new JLabel("tilesheet: "), label)
	navPanel.add(tileUrlBox, endOfLine)
	navPanel.add(new JLabel("map: "), label)
	navPanel.add(mapUrlBox, endOfLine)
	navPanel.add(new JLabel("seed: "), label)
	navPanel.add(randBox, endOfLine)
	navPanel.add(goButton, endOfLine)
	
	var tilesheet:Tilesheet = null
	var field:Field = null
	var fieldComp:FieldComponent = null
	
	loadNewTilesheet()
	frame.setVisible(true)
	
	def loadNewTilesheet() = {
		val tilesheetURI = try {
			new URI(tileUrlBox.getText)
		} catch {
			case e:java.net.URISyntaxException =>
						new File(tileUrlBox.getText).toURI
		}
		val mapURI = try {
			new URI(mapUrlBox.getText)
		} catch {
			case e:java.net.URISyntaxException =>
						new File(mapUrlBox.getText).toURI
		}
		
		tilesheet = tileMatcher(tilesheetURI)
		
		field = mapMatcher(
				mapURI,
				rotation(tilesheet, tilesheetURI)
		)
		
		fieldComp = new FieldComponent(tilesheet, field,
			randBox.getText match {
				case "" => Random
				case "a" => new Random(new java.util.Random(){override def next(bits:Int) = 1})
				case "b" => new Random(new java.util.Random(){override def next(bits:Int) = 0})
				case s => try {
						new Random(s.toLong)
					} catch {
						case e:NumberFormatException => {
							JOptionPane.showMessageDialog(frame,
								"Seed must be '', 'a', 'b' or an integer",
								"Invalid seed",
								JOptionPane.WARNING_MESSAGE
							)
							Random
						}
					}
			}
		);
		
		field match {
			case x:RotateSpaceRectangularField => {
				fieldComp.lowLayer.labels.zipWithIndex.foreach({(label:JLabel, index:Int) =>
					label.addMouseListener(new RotateListener(index))
				}.tupled)
			}
			case _ => {}
		}
		
		frame.getContentPane.removeAll()
		
		val fieldCompPane = new JPanel()
		fieldCompPane.add(fieldComp)
		
		frame.getContentPane.add(navPanel, BorderLayout.NORTH)
		frame.getContentPane.add(fieldComp)
		frame.pack()
	}
	
	class RotateListener(index:Int) extends MouseAdapter
	{
		override def mouseClicked(e:MouseEvent) =
		{
			field match {
				case x:RotateSpaceRectangularField => {
				
					field = x.rotate(index)
					
					frame.getContentPane.remove(fieldComp)
					
					fieldComp = new FieldComponent(tilesheet, field)
					fieldComp.lowLayer.labels.zipWithIndex.foreach({(label:JLabel, index:Int) =>
						label.addMouseListener(new RotateListener(index))
					}.tupled)
					frame.getContentPane.add(fieldComp)
					frame.getContentPane.validate()
				}
			}
		}
	}
	
	
	
	
	
	
	
	def rotation(tilesheet:Tilesheet, tilesheetURI:URI):Seq[SpaceClassConstructor] = {
		Seq.empty ++ (tilesheet match {
			case x:JSONTilesheet => {
				val jsonMap = {
					val reader = Files.newBufferedReader(Paths.get(tilesheetURI), UTF_8)
					
					val listener = ToScalaCollection()
					JSONParser.parse(listener, reader)
					reader.close()
					
					listener.resultMap
				}
				
				
				val classesKey = if (jsonMap.contains("TilesheetViewer::classes"))
					{"TilesheetViewer::classes"} else {"classMap"}
				
				val classesURL = new URL(tilesheetURI.toURL, jsonMap(classesKey).toString)
				val classesReader = Files.newBufferedReader(Paths.get(classesURL.toURI), UTF_8)
				
				val classNames = {
					val listener = ToScalaCollection()
					JSONParser.parse(listener, classesReader)
					classesReader.close()
					val result = listener.resultSeq
					
					result.map{_ match {
						case x:Tuple2[_,_] => x._2.toString
						case x:Any => x.toString
					}}
				}
				
				classNames.map{(objectName:String) =>
					val clazz = Class.forName(objectName + "$")
					val field = clazz.getField("MODULE$")
					
					field.get(null).asInstanceOf[SpaceClassConstructor]
				}
			}
		/*	case FieldChessTilesheet => {
				import com.rayrobdod.deductionTactics._
				Seq(PassibleSpaceClass, ImpassibleSpaceClass,
						AttackableOnlySpaceClass, NoStandOnSpaceClass)
			}
		*/	case _ => Seq(AnySpaceClass)
		})
	}
	
	
	
	object tileMatcher {
		def apply(tilesheetURI:URI):Tilesheet = {
			tilesheetURI.getScheme match
			{
				case "tag" => {
					tilesheetURI.getSchemeSpecificPart match
					{
						case "rayrobdod.name,2013-08:tilesheet-nil" => NilTilesheet
						case CheckerboardURIMatcher(checker) => checker;
						case "rayrobdod.name,2013-08:tilesheet-indexies" => IndexesTilesheet
						case "rayrobdod.name,2013-08:tilesheet-randcolor" => new RandomColorTilesheet
						// case "rayrobdod.name,2013-08:tilesheet-field" => FieldChessTilesheet
						case _ => {
							JOptionPane.showMessageDialog(frame,
									"Tilesheet URI contains an unknown tag",
									"Unkown URI",
									JOptionPane.WARNING_MESSAGE
							)
							NilTilesheet
						}
					}
				}
				case _ => JSONTilesheet( tilesheetURI.toURL )
			}
		}
		
		
	}
	
	
	def mapMatcher(mapURI:URI, rotation:Seq[SpaceClassConstructor]):Field = {
		object RotateFieldURIMatcher {
			case class Builder(width:Int, height:Int);
			
			def unapply(ssp:String):Option[RotateSpaceRectangularField] = {
				val split = ssp.split("[\\?\\&]");
				
				if ("rayrobdod.name,2013-08:map-rotate" == split.head)
				{
					var builder = new Builder(10,12)
					
					split.tail.foreach{(param:String) =>
						val splitParam = param.split("=");
						splitParam(0) match {
							case "width" => {
								builder = builder.copy(
									width = splitParam(1).toInt
								)
							}
							case "height" => {
								builder = builder.copy(
									height = splitParam(1).toInt
								)
							}
							case _ => {}
						}
					}
					
					return Some(new RotateSpaceRectangularField(
						rotation, builder.width, builder.height
					))
				} else {
					return None;
				}
			}
		}
		
		mapURI.getScheme match
		{
			case "tag" => {
				mapURI.getSchemeSpecificPart match
				{
					case RotateFieldURIMatcher(rotate) => rotate
					case _ => {
						JOptionPane.showMessageDialog(frame,
								"Map URI contains an unknown tag",
								"Unkown URI",
								JOptionPane.WARNING_MESSAGE
						)
						new RotateSpaceRectangularField(rotation, 10, 12)
					}
				}
			}
			case _ => {
				
				val metadataPath = Paths.get(mapURI)
				val metadataReader = Files.newBufferedReader(metadataPath, UTF_8);
				val metadataMap:Map[String,String] = {
					val listener = ToScalaCollection()
					JSONParser.parse(listener, metadataReader)
					listener.resultMap.mapValues{_.toString}
				}
		
				val letterToSpaceClassConsPath = metadataPath.getParent.resolve(metadataMap("classMap"))
				val letterToSpaceClassConsReader = Files.newBufferedReader(letterToSpaceClassConsPath, UTF_8)
				val letterToSpaceClassConsMap:Map[String,SpaceClassConstructor] = {
					val listener = ToScalaCollection()
					JSONParser.parse(listener, letterToSpaceClassConsReader)
					val letterToClassNameMap = listener.resultMap.mapValues{_.toString}
					
					letterToClassNameMap.mapValues{(objectName:String) => 
						val clazz = Class.forName(objectName + "$")
						val field = clazz.getField("MODULE$")
						
						field.get(null).asInstanceOf[SpaceClassConstructor]
					}
				}
				
				val layoutPath = metadataPath.getParent.resolve(metadataMap("layout"))
				val layoutReader = Files.newBufferedReader(layoutPath, UTF_8)
				val layoutTable:Seq[Seq[SpaceClassConstructor]] = {
					val listener = new ToSeqSeqCSVParseListener()
					new CSVParser(CSVPatterns.commaDelimeted).parse(listener, layoutReader)
					val letterTable = listener.result
					
					letterTable.map{_.map{letterToSpaceClassConsMap}}
				}
				
				Field.applySCC( layoutTable )
			}
		}
		
	}
}
