package com.community.xanadu.components.validation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JComponent;

import org.jdesktop.jxlayer.JXLayer;

public abstract class ValidationPaint {

	public static ValidationPaint getColorValidationPaint(final Color color) {
		return new ValidationPaint() {
			@Override
			public void paint(final Graphics2D g, final List<String> msg, final JXLayer<JComponent> layer,
					final float animProgress) {
				g.setComposite(AlphaComposite.SrcOver.derive(0.1f+animProgress*0.4f));
				g.setColor(color);
				g.fillRect(0, 0, layer.getWidth(), layer.getHeight());
			}
		};
	}
/**
 * 
 * @param g
 * @param msg the list of error message
 * @param width width of the layer
 * @param height height of the layer
 * @param animProgress goes from 0 to 1 and then from 1 to 0, the duration of the animation can be set using ValidationOverlay.setAnimationDuration
 */
	public abstract void paint(Graphics2D g, List<String> msg, JXLayer<JComponent> layer, float animProgress);

}
