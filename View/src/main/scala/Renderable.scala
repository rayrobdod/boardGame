package com.rayrobdod.boardGame.view

/**
 * 
 * @group RendererTemplate
 */
trait Renderable[Index, Component] {
	
	/**
	 * Register a function to be called when the user clicks the tile
	 * with the specified index
	 */
	def addOnClickHandler(idx:Index, f:Function0[Unit]):Unit
	
	/**
	 * The component that 
	 */
	def component:Component
	
}
