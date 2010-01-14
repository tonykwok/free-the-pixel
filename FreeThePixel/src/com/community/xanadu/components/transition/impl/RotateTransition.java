package com.community.xanadu.components.transition.impl;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;

import com.community.xanadu.components.transition.CloseTransition;

public class RotateTransition extends CloseTransition {
	public RotateTransition(final Window frame) {
		super(frame);
	}

	@Override
	protected void paintImage(final Graphics g,final float animFraction) {
		final Graphics2D g2 = (Graphics2D) g.create();
		final float i = 1 - getAnimFraction();
		g2.setComposite(AlphaComposite.SrcOver.derive(i));

		g2.rotate(Math.PI / 4 * animFraction);

		g2.drawImage(this.originalImage, (int) (this.originalImage.getWidth() * animFraction) / 2, 0,
				(int) (this.originalImage.getWidth() * i), (int) (this.originalImage.getHeight() * i), null);
		g2.dispose();
	}
}
