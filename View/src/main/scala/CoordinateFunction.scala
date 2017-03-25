package com.rayrobdod.boardGame.view

/**
 * The return value of [[CoordinateFunctionSpecifierParser]]
 *
 * @group CoordinateFunction
 * 
 * @tparam Index the function input
 * @tparam A the function output
 */
trait CoordinateFunction[-Index, @specialized(Int, Boolean) A] {
	/** Do the thing */
	def apply(idx:Index):A
	
	/** The number of division operations in this function */
	private[view] def divCount:Int = 0
	/** The number of and operations in this function */
	private[view] def andCount:Int = 0
	/** The sum of integers contained in this function */
	private[view] def primitiveSum:Int = 0
	/** Whether this function trivially returns true */
	private[view] def isJustTrue:Boolean = false
	
	/** The priority with which this function should be used. Higher superscedes lower. */
	final def priority = if (isJustTrue) {0} else {(1000) / (divCount + 1) * (andCount + 1) + primitiveSum}
	
	/**
	 * Create a new CoordinateFunction which applies its input to both this and rhs, then combines the two results using mapping
	 * 
	 * @param rhs the other function
	 * @param mapping the output-combining function 
	 * @param name the returnValue's `toString`
	 * @param incrementDivCount magic related to priorities
	 * @param incrementAndCount magic related to priorities
	 */
	private[view] def zipWith[Index2 <: Index, @specialized(Int, Boolean) B, @specialized(Int, Boolean) C](
			rhs:CoordinateFunction[Index2, B],
			mapping:(A,B) => C,
			name:String,
			incrementDivCount:Int = 0,
			incrementAndCount:Int = 0
	) = {
		new CoordinateFunction[Index2, C]{
			override def apply(idx:Index2):C =
				mapping(CoordinateFunction.this.apply(idx), rhs.apply(idx))
			override def divCount:Int = CoordinateFunction.this.divCount + rhs.divCount + incrementDivCount
			override def andCount:Int = CoordinateFunction.this.andCount + rhs.andCount + incrementAndCount
			override def primitiveSum:Int = CoordinateFunction.this.primitiveSum + rhs.primitiveSum
			override def toString = name
		}
	}
}

/**
 * Factory methods for CoordinateFunctions
 *
 * @group CoordinateFunction
 */
private[view] object CoordinateFunction {
	/** A CoordinateFunction that always returns the specified value */
	def constant[@specialized(Int, Boolean) A](a:A):CoordinateFunction[Any, A] = new CoordinateFunction[Any, A]{
		override def apply(idx:Any):A = a
		override def primitiveSum:Int = {
			if (a.isInstanceOf[Int]) {a.asInstanceOf[Int]} else {0}
		}
		override def isJustTrue:Boolean = {
			if (a.isInstanceOf[Boolean]) {a.asInstanceOf[Boolean]} else {false}
		}
		override def toString:String = a.toString
	}
}
