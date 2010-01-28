package com.community.xanadu.components.buttons.shape;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class CheckButton extends AbstractShapeButton {
	private static final long serialVersionUID = 1L;

	public CheckButton() {
		setDefaultColor(Color.green.darker());
		setArmedColor(Color.green);
	}

	@Override
	protected Shape getShape() {
		final float w = getWidth() / 6;
		final float h = getHeight() / 6;

		final GeneralPath shape = new GeneralPath();
		shape.moveTo(0, 4 * h);
		shape.lineTo(0.5f * w, 3.5f * h);
		shape.lineTo(2 * w, 5 * h);
		shape.lineTo(5 * w, 1 * h);
		shape.lineTo(6 * w, 1.5f * h);
		shape.lineTo(2 * w, 6 * h);
		shape.closePath();

		return shape;
	}
}