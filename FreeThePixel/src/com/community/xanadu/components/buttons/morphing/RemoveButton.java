package com.community.xanadu.components.buttons.morphing;

import java.awt.geom.GeneralPath;

public class RemoveButton extends AbstractMorphingButton {
	private static final long serialVersionUID = 1L;

	public RemoveButton() {
		super(" ");

	}

	@Override
	protected void createDestinationShape() {
		float w = getWidth() / 6;
		float h = getHeight() / 6;

		GeneralPath shape = new GeneralPath();
		shape.moveTo(0, 2 * h);
		shape.lineTo(6 * w, 2 * h);
		shape.lineTo(6 * w, 4 * h);
		shape.lineTo(0, 4 * h);
		shape.closePath();
		this.destinationShape = shape;
	}

	@Override
	protected void createSourceShape() {
		int w = getWidth();
		int h = getHeight();

		GeneralPath shape = new GeneralPath();

		shape.moveTo(w, 0);
		shape.lineTo(w, h);
		shape.lineTo(0, h / 2);

		shape.closePath();
		this.sourceShape = shape;
	}
}