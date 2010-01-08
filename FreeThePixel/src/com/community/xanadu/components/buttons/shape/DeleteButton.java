package com.community.xanadu.components.buttons.shape;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DeleteButton extends AbstractShapeButton {
	public DeleteButton() {
		setDefaultColor(Color.RED.darker());
		setArmedColor(Color.RED);
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

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().add(new DeleteButton());
				f.setSize(200, 200);
				f.setVisible(true);
			}
		});
	}
}
