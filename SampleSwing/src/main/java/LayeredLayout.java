package com.rayrobdod.jsonTilesheetViewer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * A layout that causes every inner component to take the entire space of the
 * container, one atop another
 */
public final class LayeredLayout implements LayoutManager2 {
	/** Creates a layered layout. */
	public LayeredLayout() {}
	
	public void addLayoutComponent(Component comp, Object obj) {}
	
	public void addLayoutComponent(String name, Component comp) {}
	
	public void removeLayoutComponent(Component comp) {}
	
	public float getLayoutAlignmentX(Container container) {
		return 0.5f;
	}
	
	public float getLayoutAlignmentY(Container container) {
		return 0.5f;
	}
	
	public void invalidateLayout(Container container) {}
	
	public void layoutContainer(Container parent) {
		for (Component child : parent.getComponents()) {
				child.setLocation(0,0);
				child.setSize(parent.getWidth(), parent.getHeight());
		}
	}
	
	public Dimension maximumLayoutSize(Container container) {
		if (container.getComponentCount() == 0) {
			return new Dimension(0,0);
		} else {
			double width = 1d/0;
			double height = 1d/0;
			
			for (Component c : container.getComponents()) {
				width = Math.min(c.getMaximumSize().getWidth(), width);
				height = Math.min(c.getMaximumSize().getHeight(), height);
			}
			
			return new Dimension((int) width, (int) height);
		}
	}
	
	public Dimension minimumLayoutSize(Container container) {
		double width = 0;
		double height = 0;
		
		for (Component c : container.getComponents()) {
			width = Math.max(c.getMinimumSize().getWidth(), width);
			height = Math.max(c.getMinimumSize().getHeight(), height);
		}
		
		return new Dimension((int) width, (int) height);
	}
	
	public Dimension preferredLayoutSize(Container container) {
		double width = 0;
		double height = 0;
		
		for (Component c : container.getComponents()) {
			width = Math.max(c.getPreferredSize().getWidth(), width);
			height = Math.max(c.getPreferredSize().getHeight(), height);
		}
		
		return new Dimension((int) width, (int) height);
	}
}
