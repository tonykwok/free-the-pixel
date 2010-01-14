package com.community.xanadu.components.transition.impl;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;

import com.community.xanadu.components.transition.ShapeTransition;

public class FadeOutTransition extends ShapeTransition {

	public FadeOutTransition(final Window frame) {
		super(frame);
	}

	@Override
	public Shape getShape(final float animFraction) {
		final Rectangle shape = new Rectangle(0, 0, (this.originalImage.getWidth()), (this.originalImage
				.getHeight()));
		return shape;
	}

}
