package com.community.xanadu.components.transition.impl;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;

import com.community.xanadu.components.transition.ShapeTransition;

public class ShrinkTransition extends ShapeTransition {
	public ShrinkTransition(final Window frame) {
		super(frame);
	}

	@Override
	public Shape getShape() {
		float i = 1 - getAnimProgress();
		Rectangle shape = new Rectangle((int) (this.originalImage.getWidth() * getAnimProgress()) / 2,
				(int) (this.originalImage.getHeight() * getAnimProgress()) / 2,
				(int) (this.originalImage.getWidth() * i), (int) (this.originalImage.getHeight() * i));

		return shape;
	}
}
