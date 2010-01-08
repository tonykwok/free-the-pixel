package com.community.xanadu.components.buttons.shape;

import java.awt.BorderLayout;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DirectionButton extends AbstractShapeButton {
	private static final long serialVersionUID = 1L;

	public enum Direction {
		DOWN, UP, LEFT, RIGHT
	};

	private Direction direction;

	public DirectionButton(final Direction direction) {
		super();
		this.direction = direction;
		this.shape = getShape();
	}

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.getContentPane().setLayout(new BorderLayout());
				f.getContentPane().add(new DirectionButton(Direction.LEFT));
				f.setSize(400, 300);
				f.setVisible(true);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}

	@Override
	protected Shape getShape() {
		if (this.direction == null)
			return null;
		GeneralPath shape = new GeneralPath();
		float w = getWidth() / 6;
		float h = getHeight() / 6;
		switch (this.direction) {
		case DOWN:
			shape.moveTo(3f * w, 6f * h);
			shape.lineTo(6f * w, 4f * h);
			shape.lineTo(5f * w, 4f * h);
			shape.lineTo(5f * w, 0f * h);
			shape.lineTo(1f * w, 0f * h);
			shape.lineTo(1f * w, 4f * h);
			shape.lineTo(0f * w, 4f * h);
			break;
		case UP:
			shape.moveTo(3f * w, 0f * h);
			shape.lineTo(6f * w, 2f * h);
			shape.lineTo(5f * w, 2f * h);
			shape.lineTo(5f * w, 6f * h);
			shape.lineTo(1f * w, 6f * h);
			shape.lineTo(1f * w, 2f * h);
			shape.lineTo(0f * w, 2f * h);
			break;
		case LEFT:
			shape.moveTo(6f * w, 1.5f * h);
			shape.lineTo(2f * w, 1.5f * h);
			shape.lineTo(2f * w, 0f * h);
			shape.lineTo(0f * w, 3f * h);
			shape.lineTo(2f * w, 6f * h);
			shape.lineTo(2f * w, 4.5 * h);
			shape.lineTo(6f * w, 4.5 * h);
			break;
		case RIGHT:
			shape.moveTo(0f * w, 1.5f * h);
			shape.lineTo(4f * w, 1.5f * h);
			shape.lineTo(4f * w, 0f * h);
			shape.lineTo(6f * w, 3f * h);
			shape.lineTo(4f * w, 6f * h);
			shape.lineTo(4f * w, 4.5 * h);
			shape.lineTo(0f * w, 4.5 * h);
			break;
		}

		shape.closePath();

		return shape;
	}
}