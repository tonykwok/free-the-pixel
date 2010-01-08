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
		this.shrinkImage = true;
		this.fadeOut = true;
	}

	@Override
	protected void paintImage(final Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		float i = 1 - getAnimProgress();

		Shape shape = getShape();
		g2.clip(shape);
		if (this.fadeOut) {
			g2.setComposite(AlphaComposite.SrcOver.derive(i));
		}
		if (this.shrinkImage) {
			g2.drawImage(this.originalImage, (int) (this.originalImage.getWidth() * getAnimProgress()) / 2,
					(int) (this.originalImage.getHeight() * getAnimProgress()) / 2, (int) (this.originalImage
							.getWidth() * i), (int) (this.originalImage.getHeight() * i), null);
		} else {
			g2.drawImage(this.originalImage, 0, 0, (int) (this.originalImage.getWidth()), (int) (this.originalImage
					.getHeight()), null);
		}

		g2.dispose();
	}

	public abstract Shape getShape();
}
