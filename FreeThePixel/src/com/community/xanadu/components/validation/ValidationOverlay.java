package com.community.xanadu.components.validation;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.jdesktop.jxlayer.JXLayer;

import com.jidesoft.swing.DefaultOverlayable;

public class ValidationOverlay extends DefaultOverlayable {

	private final ValidationLayer<JComponent> layer;

	public ValidationOverlay(final JXLayer<JComponent> comp, final ValidationLayer<JComponent> layer,
			final ValidationPanelIcon panelicon, final int location, final int xoffset, final int yoffset) {
		super(comp, panelicon, location);
		setOverlayLocationInsets(new Insets(yoffset, 0, 0, xoffset));
		this.layer = layer;
	}

	public void setIconLocation(final int xoffset, final int yoffset, final int location) {
		setOverlayLocationInsets(new Insets(yoffset, 0, 0, xoffset));
		setOverlayLocation(getOverlayComponents()[0], location);
	}

	public void switchIcon(final BufferedImage newicon) {
		if (getOverlayComponents()[0] instanceof ValidationPanelIcon) {
			((ValidationPanelIcon) getOverlayComponents()[0]).setImage(newicon);
			((ValidationPanelIcon) getOverlayComponents()[0]).setPreferredSize(new Dimension(newicon.getWidth(),
					newicon.getHeight()));
			repaint();
		}
	}

	public void setShape(final Shape shape) {
		this.layer.setShape(shape);
	}

	public void setAnimationDuration(final long duration) {
		this.layer.setAnimationDuration(duration);
	}

	public void setPaint(final ValidationPaint paint) {
		this.layer.setPaint(paint);
	}
}