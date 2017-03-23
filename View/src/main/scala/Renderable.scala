package com.rayrobdod.boardGame.view

trait Renderable[Index, Component] {
	
	def addOnClickHandler(idx:Index, f:Function0[Unit]):Unit
	
	def component:Component
	
}
