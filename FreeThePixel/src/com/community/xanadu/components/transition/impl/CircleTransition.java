package com.community.xanadu.components.transition.impl;

import java.awt.Shape;
import java.awt.Window;
import java.awt.geom.Ellipse2D;

import com.community.xanadu.components.transition.ShapeTransition;

public class CircleTransition extends ShapeTransition {
	public CircleTransition(final Window frame) {
		super(frame);
	}

	@Override
	public Shape getShape(final float animFraction) {
		final float i = 1 - getAnimFraction();

		final int w = this.originalImage.getWidth();
		final int h = this.originalImage.getHeight();

		final int d1 = (int) Math.sqrt(w * w + h * h);
		final int d2 = this.originalImage.getHeight() * 3 / 2;

		final int xoffset = d1 - this.originalImage.getWidth();

		final int yoffset = d2 - this.originalImage.getHeight();

		final Ellipse2D.Float shape = new Ellipse2D.Float((int) (d1 * animFraction - xoffset) / 2, (int) (d2
				* animFraction - yoffset) / 2, d1 * i, d2 * i);
		return shape;
	}
}
