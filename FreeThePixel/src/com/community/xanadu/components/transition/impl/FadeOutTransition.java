package com.community.xanadu.components.transition.impl;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;

import com.community.xanadu.components.transition.ShapeTransition;

public class FadeOutTransition extends ShapeTransition {

	public FadeOutTransition(final Window frame) {
		super(frame);
		this.shrinkImage = false;
	}

	@Override
	public Shape getShape() {
		Rectangle shape = new Rectangle(0, 0, (int) (this.originalImage.getWidth()), (int) (this.originalImage
				.getHeight()));
		return shape;
	}

}
