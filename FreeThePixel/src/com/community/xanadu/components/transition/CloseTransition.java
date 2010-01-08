package com.community.xanadu.components.transition;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.ease.Spline;

import com.community.xanadu.utils.ThreadUtils;
import com.community.xanadu.utils.WindowsUtils;

public abstract class CloseTransition {
	// the window to animate
	private JDialog window;

	// the image of the window
	protected BufferedImage originalImage;

	private float animProgress;

	// window where is displayed the animation
	private Window comp;

	private int animDuration;

	// the action performed at the end of the animation
	private AbstractAction endAction;

	// the component where the image is rendered
	protected TransitionRenderer renderingComp;

	private final boolean j6u10;

	public CloseTransition(final Window frame) {
		this.window = new JDialog();
		this.window.setResizable(false);

		this.j6u10 = WindowsUtils.setOpaque(this.window, false);

		this.window.setLayout(new BorderLayout());
		this.renderingComp = new TransitionRenderer();
		this.window.add(this.renderingComp, BorderLayout.CENTER);
		this.comp = frame;
		if (this.comp instanceof JDialog) {
			((JDialog) this.comp).setModal(false);
		}
		this.animDuration = 300;
		this.endAction = new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				System.exit(0);
			}
		};
	}

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		this.window.repaint();
	}

	protected class TransitionRenderer extends JComponent {
		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			paintImage(g);
		}
	}

	protected abstract void paintImage(final Graphics g);

	public void startCloseTransition() {
		this.window.setSize(this.comp.getSize());
		this.window.setLocation(this.comp.getLocationOnScreen().x, this.comp.getLocationOnScreen().y);

		if (!this.j6u10) {
			CloseTransition.this.endAction.actionPerformed(null);
			if (this.comp instanceof JDialog) {
				((JDialog) this.comp).setModal(true);
			}
			return;
		}
		this.originalImage = GraphicsUtilities.createCompatibleTranslucentImage(CloseTransition.this.window.getWidth(),
				CloseTransition.this.window.getHeight());
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				final Graphics g = CloseTransition.this.originalImage.createGraphics();
				CloseTransition.this.comp.paint(g);
				g.dispose();

				CloseTransition.this.window.setVisible(true);
				CloseTransition.this.comp.setVisible(false);

				final Timeline timeline = new Timeline(CloseTransition.this);
				timeline.addPropertyToInterpolate("animProgress", 0f, 1f);
				timeline.setDuration(CloseTransition.this.animDuration);
				timeline.setEase(new Spline(.7f));
				timeline.addCallback(new TimelineCallbackAdapter() {
					@Override
					public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
							final float durationFraction, final float timelinePosition) {
						if (newState == TimelineState.DONE) {
							CloseTransition.this.window.setVisible(false);
							CloseTransition.this.originalImage = null;
							CloseTransition.this.endAction.actionPerformed(null);
							clean();
						}
					}
				});

				timeline.play();
			}
		});
	}

	private void clean() {
		this.comp = null;
		this.window.removeAll();
		this.window.dispose();
		this.window = null;
		this.endAction = null;
		this.originalImage = null;
		this.renderingComp = null;
	}

	public float getAnimProgress() {
		return this.animProgress;
	}

	public void setAnimDuration(final int animDuration) {
		this.animDuration = animDuration;
	}

	public void setEndAction(final AbstractAction endAction) {
		this.endAction = endAction;
	}
}
