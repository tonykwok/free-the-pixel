package com.community.xanadu.components.panels.layerSynchro;

import java.awt.Point;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;

public class HalfSizeSynchronizer extends SizeSynchronizer {

	public HalfSizeSynchronizer(final JComponent src, final JComponent dst) {
		super(src, dst);
	}

	@Override
	public void componentResized(final ComponentEvent e) {
		Point p = this.src.getLocation();
		this.dst.setBounds(p.x, p.y + this.src.getHeight() / 2, this.src.getWidth(), this.src.getHeight() / 2);
	}

	@Override
	public void componentShown(final ComponentEvent e) {
		componentResized(e);
	}
}