package com.community.xanadu.components.transition.impl;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.geom.Area;

import com.community.xanadu.components.transition.ShapeTransition;

public class RectanglesTransition extends ShapeTransition {
	public RectanglesTransition(final Window frame) {
		super(frame);
		this.shrinkImage = false;
	}

	@Override
	public Shape getShape() {
		final int stepSize = 50;
		final int numRect = 1 + this.renderingComp.getHeight() / stepSize;

		final Area toHide = new Area();

		final Rectangle[] rects = new Rectangle[numRect];
		for (int n = 0; n < numRect; n++) {
			rects[n] = new Rectangle(0, (int) (stepSize * n - stepSize / 2 * getAnimProgress()), this.renderingComp
					.getWidth(), (int) (stepSize * getAnimProgress()));
			toHide.add(new Area(rects[n]));
		}

		final Area all = new Area(new Rectangle(0, 0, this.renderingComp.getWidth(), this.renderingComp.getHeight()));
		all.subtract(toHide);

		return all;
	}

}
