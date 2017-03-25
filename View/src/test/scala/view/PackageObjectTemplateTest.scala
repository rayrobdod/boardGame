package com.rayrobdod.boardGame.view

import org.scalatest.FunSpec
import java.awt.Color
import java.awt.{Dimension => AwtDimension}
import java.net.URL
import scala.collection.immutable.Seq
import scala.util.Random
import com.rayrobdod.boardGame._

final class PackageObjectTemplateTest extends FunSpec {
	import PackageObjectTemplateTest._
	
	describe ("NilTilesheet") {
		it ("uses blankIcon") {
			assertResult( ((FakeBlankIcon, FakeBlankIcon)) ){
				FakePackageObject.NilTilesheet(RectangularDimension(20, 20)).getIconFor(null, null, null)
			}
		}
		it ("uses the dimension from the parameter as `iconDimensions`") {
			assertResult( HorizontalHexagonalDimension(20, 20, 5) ){
				FakePackageObject.NilTilesheet(HorizontalHexagonalDimension(20, 20, 5)).iconDimensions
			}
		}
	}
	describe ("HashcodeColorTilesheet (Rectangular)") {
		val dim = RectangularDimension(20, 20)
		val field = RectangularField( Map(((0, 0)) -> 0) )
		
		it ("uses rgbToRectangularIcon") {
			assertResult( ((FakeSolidRectIcon(new Color(0), dim), FakeBlankIcon )) ){
				FakePackageObject.HashcodeColorTilesheet(dim).getIconFor(field, ((0,0)), null)
			}
		}
		it ("uses the dimension from the parameter as `iconDimensions`") {
			assertResult( dim ){
				FakePackageObject.HashcodeColorTilesheet(dim).iconDimensions
			}
		}
	}
	describe ("HashcodeColorTilesheet (HorizHex)") {
		val dim = HorizontalHexagonalDimension(20, 20, 5)
		val field = HorizontalHexagonalField( Map(((0, 0)) -> 0) )
		
		it ("uses rgbToHorizontalHexagonalIcon") {
			assertResult( ((FakeSolidHorizHexIcon(new Color(0), dim), FakeBlankIcon )) ){
				FakePackageObject.HashcodeColorTilesheet(dim).getIconFor(field, ((0,0)), null)
			}
		}
		it ("uses the dimension from the parameter as `iconDimensions`") {
			assertResult( dim ){
				FakePackageObject.HashcodeColorTilesheet(dim).iconDimensions
			}
		}
	}
	describe ("IndexiesTilesheet (Rectangular)") {
		val dim = RectangularDimension(20, 20)
		
		it ("do thing") {
			for (x <- -5 to 10; y <- -5 to 10) { if ((x + y) % 2 == 0) {
				assertResult( ((FakeSolidRectIcon(Color.cyan, dim), FakeStringIcon(s"($x,$y)", Color.black) )) ){
					FakePackageObject.IndexesTilesheet(dim).getIconFor(null, ((x,y)), null)
				}
			}}
		}
		it ("uses alternating colors") {
			for (x <- -5 to 10; y <- -5 to 10) { if ((x + y) % 2 == 1) {
				assertResult( ((FakeSolidRectIcon(Color.magenta, dim), FakeStringIcon(s"($x,$y)", Color.black) )) ){
					FakePackageObject.IndexesTilesheet(dim).getIconFor(null, ((x,y)), null)
				}
			}}
		}
		it ("uses the dimension from the parameter as `iconDimensions`") {
			assertResult( dim ){
				FakePackageObject.IndexesTilesheet(dim).iconDimensions
			}
		}
	}
	describe ("IndexiesTilesheet (HorizontalHexagonal)") {
		val dim = HorizontalHexagonalDimension(20, 20, 5)
		
		it ("do thing") {
			assertResult( ((FakeSolidHorizHexIcon(Color.cyan, dim), FakeStringIcon("(0,0)", Color.black) )) ){
				FakePackageObject.IndexesTilesheet(dim).getIconFor(null, ((0,0)), null)
			}
		}
		it ("uses alternating colors") {
			assertResult( ((FakeSolidHorizHexIcon(Color.magenta, dim), FakeStringIcon("(0,1)", Color.black) )) ){
				FakePackageObject.IndexesTilesheet(dim).getIconFor(null, ((0,1)), null)
			}
		}
		it ("uses alternating colors (2)") {
			assertResult( ((FakeSolidHorizHexIcon(new Color(0.5f, 1.0f, 0.5f), dim), FakeStringIcon("(1,0)", Color.black) )) ){
				FakePackageObject.IndexesTilesheet(dim).getIconFor(null, ((1,0)), null)
			}
		}
		it ("uses the dimension from the parameter as `iconDimensions`") {
			assertResult( dim ){
				FakePackageObject.IndexesTilesheet(dim).iconDimensions
			}
		}
	}
	describe ("RandomColorTilesheet (Rectangular)") {
		val dim = RectangularDimension(20, 20)
		def fixedRng(x:Int) = new Random(new java.util.Random(){override def next(bits:Int):Int = x})
		
		it ("do thing") {
			assertResult( ((FakeSolidRectIcon(new Color(0x123456), dim), FakeStringIcon("123456", Color.white) )) ){
				FakePackageObject.RandomColorTilesheet(dim).getIconFor(null, null, fixedRng(0x123456))
			}
		}
		it ("do thing (light)") {
			assertResult( ((FakeSolidRectIcon(new Color(0xC0FFEE), dim), FakeStringIcon("c0ffee", Color.black) )) ){
				FakePackageObject.RandomColorTilesheet(dim).getIconFor(null, null, fixedRng(0xC0FFEE))
			}
		}
		it ("uses the dimension from the parameter as `iconDimensions`") {
			assertResult( dim ){
				FakePackageObject.IndexesTilesheet(dim).iconDimensions
			}
		}
	}
	
}

object PackageObjectTemplateTest {
	
	final case class FakeIconPart(url:URL, dim:AwtDimension, idx:Int)
	
	sealed trait FakeIcon
	object FakeBlankIcon extends FakeIcon
	final case class FakeSolidRectIcon(rgb:Color, size:RectangularDimension) extends FakeIcon
	final case class FakeSolidHorizHexIcon(rgb:Color, size:HorizontalHexagonalDimension) extends FakeIcon
	final case class FakeStringIcon(text:String, rgb:Color) extends FakeIcon
	final case class ComposedIcon(parts:Seq[Seq[FakeIconPart]]) extends FakeIcon
	
	
	object FakePackageObject extends PackageObjectTemplate[FakeIconPart, FakeIcon] {
		override val blankIcon = FakeBlankIcon
		override def rgbToRectangularIcon(rgb:Color, size:RectangularDimension):FakeIcon = FakeSolidRectIcon(rgb, size)
		override def rgbToHorizontalHexagonalIcon(rgb:Color, size:HorizontalHexagonalDimension):FakeIcon = FakeSolidHorizHexIcon(rgb, size)
		override def stringIcon(text:String, rgb:Color, size:RectangularDimension):FakeIcon = FakeStringIcon(text, rgb)
		override def compostLayers(parts:Seq[Seq[FakeIconPart]]):FakeIcon = ComposedIcon(parts)
		
		override def sheeturl2images(sheetUrl:URL, tileDimension:AwtDimension):Seq[FakeIconPart] = {
			(0 to 63).map{idx => FakeIconPart(sheetUrl, tileDimension, idx)}
		}
		override def renderable[Index, Dimension](tiles:Map[Index, FakeIcon], dimension:Dimension)(implicit iconLocation:IconLocation[Index, Dimension]):Renderable[Index, RenderableComponentType] = ???
	}
}
