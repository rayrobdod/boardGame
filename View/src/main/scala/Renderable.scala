package com.rayrobdod.boardGame.view

import scala.collection.immutable.Map

trait Renderable[Index, Component] {
	
	def addOnClickHandler(idx:Index, f:Function0[Unit]):Unit
	
	def component:Component
	
}
