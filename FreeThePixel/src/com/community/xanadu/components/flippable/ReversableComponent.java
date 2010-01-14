package com.community.xanadu.components.flippable;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ReversableComponent extends JLayeredPane {

	private final JPanel frontComp = new JPanel(new BorderLayout());
	private final JPanel backComp = new JPanel(new BorderLayout());
	private Dimension size;
	private boolean noAnimation;
	private int animDuration;

	public ReversableComponent() {
		add(this.frontComp);
		add(this.backComp);
		this.backComp.setVisible(false);

		this.backComp.setOpaque(false);
		this.frontComp.setOpaque(false);
		this.animDuration = 500;
	}

	public void setFront(final JComponent comp) {
		this.frontComp.removeAll();
		this.frontComp.add(comp);
	}

	public void setBack(final JComponent comp) {
		this.backComp.removeAll();
		this.backComp.add(comp);
	}

	public void flip(final JComponent nextComp) {
		if (this.frontComp.isShowing()) {
			setBack(nextComp);
		} else {
			setFront(nextComp);
		}

		revalidate();
		// start in a new thread so the next comp will got it layout done one
		// the flip start
		new Thread(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						flip();
					}
				});
			}
		}).start();
	}

	public void flip() {
		final ComponentFlipper flipper = new ComponentFlipper(this.animDuration);
		flipper.setNoAnimation(this.noAnimation);
		if (this.frontComp.isShowing()) {
			flipper.flip(this.frontComp, this.backComp, this);
		} else {
			flipper.flip(this.backComp, this.frontComp, this);
		}
	}

	@Override
	public Dimension getPreferredSize() {

		return this.size;
	}

	@Override
	public void doLayout() {
		// Rectangle r = getBounds();
		final Dimension d = this.size;// getPreferredSize();

		this.frontComp.setBounds(0, 0, (int) d.getWidth(), (int) d.getHeight());

		this.backComp.setBounds(0, 0, (int) d.getWidth(), (int) d.getHeight());
	}

	@Override
	public void setSize(final Dimension size) {
		this.size = size;
	}

	public boolean isFrontShowing() {
		return this.frontComp.isShowing();
	}

	public boolean isBackShowing() {
		return this.backComp.isShowing();
	}

	public void setNoAnimation(final boolean noAnimation) {
		this.noAnimation = noAnimation;
	}

	public void setAnimDuration(final int animDuration) {
		this.animDuration = animDuration;
	}
}
