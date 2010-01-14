package com.community.xanadu.components.transition.impl;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.community.xanadu.components.transition.CloseTransition;
import com.jhlabs.image.PinchFilter;

public class PinchTransition extends CloseTransition {
	private final PinchFilter filter;
	private BufferedImage buff;

	public PinchTransition(final Window frame) {
		super(frame);
		this.filter = new PinchFilter();
		this.filter.setCentreX(0f);
		this.filter.setCentreY(0f);
		this.filter.setAmount(1f);
		this.baseradius = Math.min(frame.getWidth(), frame.getHeight());
	}

	private final int baseradius;

	@Override
	protected void paintImage(final Graphics g, final float animFraction) {
		if (this.buff == null) {
			if (this.renderingComp.getWidth() * this.renderingComp.getHeight() > (300 * 300)) {

				this.originalImage = GraphicsUtilities.createThumbnail(this.originalImage, this.renderingComp
						.getWidth() / 2, this.renderingComp.getHeight() / 2);
			}
			this.buff = GraphicsUtilities.createCompatibleTranslucentImage(this.originalImage.getWidth(),
					this.originalImage.getHeight());
		}
		this.filter.setRadius(1.5f * this.baseradius * animFraction);
		this.filter.filter(this.originalImage, this.buff);

		final Graphics2D g2 = (Graphics2D) g.create();
		g2.setComposite(AlphaComposite.SrcOver.derive(1f - animFraction));
		g2.drawImage(this.buff, 0, 0, this.renderingComp.getWidth(), this.renderingComp.getHeight(), null);
		g2.dispose();
	}

}
