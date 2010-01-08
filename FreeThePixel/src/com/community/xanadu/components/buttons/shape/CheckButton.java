package com.community.xanadu.components.buttons.shape;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class CheckButton extends AbstractShapeButton {
	private static final long serialVersionUID = 1L;

	public CheckButton() {
	}

	@Override
	protected Shape getShape() {
		float w = getWidth() / 6;
		float h = getHeight() / 6;

		GeneralPath shape = new GeneralPath();
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