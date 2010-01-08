package com.community.xanadu.components.panels.layerSynchro;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;

public class SizeSynchronizer extends ComponentAdapter {
	protected JComponent src;
	protected JComponent dst;

	public SizeSynchronizer(final JComponent src, final JComponent dst) {
		this.src = src;
		this.dst = dst;
	}

	@Override
	public void componentResized(final ComponentEvent e) {
		Point p = this.src.getLocation();
		this.dst.setBounds(p.x, p.y, this.src.getWidth(), this.src.getHeight());
		if (this.dst.getParent() != null) {
			this.dst.getParent().validate();
		}
	}

	@Override
	public void componentShown(final ComponentEvent e) {
		componentResized(e);
	}
}