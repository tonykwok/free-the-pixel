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
	private PinchFilter filter;
	private BufferedImage buff;

	public PinchTransition(final Window frame) {
		super(frame);
		this.filter = new PinchFilter();
		this.filter.setCentreX(0f);
		this.filter.setCentreY(0f);
		this.filter.setRadius(Math.min(frame.getWidth(), frame.getHeight()));

	}

	@Override
	protected void paintImage(final Graphics g) {
		if (this.buff == null) {
			if (this.renderingComp.getWidth() * this.renderingComp.getHeight() > (250 * 250)) {
				this.buff = GraphicsUtilities.createThumbnail(this.originalImage, this.renderingComp.getWidth() / 2,
						this.renderingComp.getHeight() / 2);
			} else {
				this.buff = this.originalImage;
			}
		}
		float i = 1 - getAnimProgress();
		Graphics2D g2 = (Graphics2D) g.create();
		this.filter.setRadius(this.filter.getRadius() * 1.02f);
		this.filter.setAmount(this.filter.getAmount() * 1.02f);
		this.filter.filter(this.buff, this.buff);
		g2.setComposite(AlphaComposite.SrcOver.derive(i));
		g2.drawImage(this.buff, 0, 0, this.renderingComp.getWidth(), this.renderingComp.getHeight(), null);
		g2.dispose();
	}

}
