package com.community.xanadu.components.transition.impl;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;

import com.community.xanadu.components.transition.ShapeTransition;

public class ShrinkTransition extends ShapeTransition {
	public ShrinkTransition(final Window frame) {
		super(frame);
		this.shrinkImage=true;
	}

	@Override
	public Shape getShape(final float animFraction) {
		final float i = 1 - animFraction;
		final Rectangle shape = new Rectangle((int) (this.originalImage.getWidth() * getAnimFraction()) / 2,
				(int) (this.originalImage.getHeight() * getAnimFraction()) / 2,
				(int) (this.originalImage.getWidth() * i), (int) (this.originalImage.getHeight() * i));

		return shape;
	}
}
