package com.community.xanadu.components.buttons.morphing;

import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class AddButton extends AbstractMorphingButton {
	private static final long serialVersionUID = 1L;

	public AddButton() {
		super(" ");
	}

	@Override
	protected void createDestinationShape() {
		float w = getWidth() / 6;
		float h = getHeight() / 6;

		GeneralPath shape = new GeneralPath();
		shape.moveTo(2f * w, 0);
		shape.lineTo(4f * w, 0);
		shape.lineTo(4f * w, 2f * h);
		shape.lineTo(6 * w, 2f * h);
		shape.lineTo(6 * w, 4f * h);
		shape.lineTo(4f * w, 4f * h);
		shape.lineTo(4f * w, 6 * h);
		shape.lineTo(2f * w, 6 * h);
		shape.lineTo(2f * w, 4f * h);
		shape.lineTo(0, 4f * h);
		shape.lineTo(0, 2f * h);
		shape.lineTo(2f * w, 2f * h);

		shape.closePath();
		this.destinationShape = shape;
	}

	@Override
	protected void createSourceShape() {
		int w = getWidth();
		int h = getHeight();

		GeneralPath shape = new GeneralPath();

		shape.moveTo(0, 0);
		shape.lineTo(w, h / 2);
		shape.lineTo(0, h);

		shape.closePath();
		this.sourceShape = shape;
	}

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.getContentPane().add(new AddButton());
				f.setSize(150, 150);
				f.setVisible(true);
			}
		});
	}
}