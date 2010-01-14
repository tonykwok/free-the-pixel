package com.community.xanadu.components.transition;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Window;

public abstract class ShapeTransition extends CloseTransition {
	protected boolean shrinkImage;
	protected boolean fadeOut;

	public ShapeTransition(final Window frame) {
		super(frame);
		this.shrinkImage = false;
		this.fadeOut = true;
	}

	@Override
	protected void paintImage(final Graphics g, final float animFraction) {
		final Graphics2D g2 = (Graphics2D) g.create();
		final float i = 1 - animFraction;

		final Shape shape = getShape(animFraction);
		g2.clip(shape);
		if (this.fadeOut) {
			g2.setComposite(AlphaComposite.SrcOver.derive(i));
		}
		if (this.shrinkImage) {
			g2.drawImage(this.originalImage, (int) (this.originalImage.getWidth() * animFraction) / 2,
					(int) (this.originalImage.getHeight() * animFraction) / 2,
					(int) (this.originalImage.getWidth() * i), (int) (this.originalImage.getHeight() * i), null);
		} else {
			g2.drawImage(this.originalImage, 0, 0, (this.originalImage.getWidth()), (this.originalImage.getHeight()),
					null);
		}

		g2.dispose();
	}

	public abstract Shape getShape(float animFraction);
	
	public void setFadeOut(final boolean fadeOut) {
		this.fadeOut = fadeOut;
	}
}
