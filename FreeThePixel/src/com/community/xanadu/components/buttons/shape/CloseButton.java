package com.community.xanadu.components.buttons.shape;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class CloseButton extends AbstractShapeButton {
	private static final long serialVersionUID = 1L;

	public CloseButton() {
		super();
	}

	@Override
	protected Shape getShape() {
		float w = getWidth() / 6;
		float h = getHeight() / 6;

		GeneralPath shape = new GeneralPath();
		shape.moveTo(0, h);
		shape.lineTo(w, 0);
		shape.lineTo(3 * w, 2 * h);
		shape.lineTo(5 * w, 0);
		shape.lineTo(6 * w, h);
		shape.lineTo(4 * w, 3 * h);
		shape.lineTo(6 * w, 5 * h);
		shape.lineTo(5 * w, 6 * h);
		shape.lineTo(3 * w, 4 * h);
		shape.lineTo(w, 6 * h);
		shape.lineTo(0, 5 * h);
		shape.lineTo(2 * w, 3 * h);
		shape.closePath();
		return shape;
	}
}