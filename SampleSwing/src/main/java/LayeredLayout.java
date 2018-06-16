/*
	Copyright (c) 2009-2013, Raymond Dodge
	All rights reserved.
	
	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions are met:
		* Redistributions of source code must retain the above copyright
		  notice, this list of conditions and the following disclaimer.
		* Redistributions in binary form must reproduce the above copyright
		  notice, this list of conditions and the following disclaimer in the
		  documentation and/or other materials provided with the distribution.
		* Neither the name "Image Manipulator" nor the names of its contributors
		  may be used to endorse or promote products derived from this software
		  without specific prior written permission.
	
	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
	ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
	DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.rayrobdod.jsonTilesheetViewer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * A layout that makes every component on atop another
 */
public class LayeredLayout implements LayoutManager2
{
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
