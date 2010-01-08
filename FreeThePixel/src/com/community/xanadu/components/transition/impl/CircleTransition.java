package com.community.xanadu.components.transition.impl;

import java.awt.Shape;
import java.awt.Window;
import java.awt.geom.Ellipse2D;

import com.community.xanadu.components.transition.ShapeTransition;

public class CircleTransition extends ShapeTransition {
	public CircleTransition(final Window frame) {
		super(frame);
		shrinkImage = false;
	}

	@Override
	public Shape getShape() {
		float i = 1 - getAnimProgress();

		int w = this.originalImage.getWidth();
		int h = this.originalImage.getHeight();

		int d1 = (int) Math.sqrt(w * w + h * h);
		int d2 = originalImage.getHeight() * 3 / 2;

		int xoffset = d1 - originalImage.getWidth();

		int yoffset = d2 - originalImage.getHeight();

		Ellipse2D.Float shape = new Ellipse2D.Float((int) (d1 * getAnimProgress() - xoffset) / 2, (int) (d2
				* getAnimProgress() - yoffset) / 2, d1 * i, d2 * i);
		return shape;
	}
}
