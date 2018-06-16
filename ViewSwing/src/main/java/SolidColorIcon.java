/*
	Honestly, this particular file being independently invented is a thousand
	times more likely than someone finding this. On that note:
	
	Copyright (C) 2013 Raymond Dodge
	
	This file is provided under the terms of the Creative Commons Public
	Domain Dedication. A copy of the licence can be found at
	<https://creativecommons.org/publicdomain/zero/1.0/>
*/

package com.rayrobdod.boardGame.view;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;

/**
 * An icon that fills a defined area with a defined color
 */
public final class SolidColorIcon implements Icon {
	private final int width;
	private final int height;
	private final Color color;
	
	public SolidColorIcon(Color color, int width, int height) {
		this.color = color;
		this.width = width;
		this.height = height;
	}
	
	public int getIconWidth() {return width;}
	public int getIconHeight() {return height;}
	public Color getIconColor() {return color;}
	
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(color);
		g.fillRect(x, y, width, height);
	}
	
	public String toString() {
		return this.getClass().getName() + "[" +
		        color + ", " +
				"w=" + width + ", " +
				"h=" + height + "]";
	}
}
