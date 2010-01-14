package com.community.xanadu.components.validation;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;

public class ValidationLayer<T extends JComponent> extends AbstractLayerUI<JComponent> {
	private final List<Validator<T>> checkers;
	private final T trackedComp;
	private List<String> msg;
	private final ValidationPanelIcon label;

	private float animProgress;
	private Shape shape;
	private final Timeline animatorRect;
	private boolean valid;
	private ValidationPaint paint;;

	public ValidationLayer(final T originalTrackedComp, final List<Validator<T>> checkers,
			final ValidationPanelIcon info) {
		super();
		this.checkers = checkers;
		this.trackedComp = originalTrackedComp;
		this.label = info;

		this.animatorRect = new Timeline(this);
		this.animatorRect.setDuration(2000);
		this.animatorRect.addPropertyToInterpolate("animProgress", 0f, 1f);
		this.animatorRect.addCallback(new TimelineCallbackAdapter() {
			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {

				if (newState == TimelineState.CANCELLED || newState == TimelineState.DONE) {
					setAnimProgress(0);
				}
			}
		});

		this.trackedComp.addPropertyChangeListener(ValidationOverlayFactory.CHECK_NEEDED, new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				check();
			}
		});
	}

	public void check() {
		this.msg = getErrors(this.checkers, this.trackedComp);
		if (this.msg != null && !this.msg.isEmpty()) {
			this.valid = false;
			startShowAnim();
			if (this.label != null) {
				this.label.setToolTipText(formatErrorMessage());
			}
		} else {
			startHideAnim();
			this.valid = true;
		}
	}

	private String formatErrorMessage() {
		final StringBuilder sb = new StringBuilder();

		if (!this.msg.isEmpty()) {
			for (final String s : this.msg) {
				sb.append(s).append("<br>");
			}
			sb.insert(0, "<html>");
		}

		return sb.toString();
	}

	private void startShowAnim() {
		if (this.label != null) {
			if (!this.label.isVisible()) {
				this.label.startShowAnim();
			}
		}
		if (this.animatorRect.getState() != TimelineState.PLAYING_FORWARD
				&& this.animatorRect.getState() != TimelineState.PLAYING_REVERSE) {

			if (this.paint != null) {
				this.animatorRect.playLoop(RepeatBehavior.REVERSE);
			}
		}
	}

	private void startHideAnim() {
		if (this.label != null) {
			if (this.label.isVisible()) {
				this.label.startHideAnim();
			}
		}
		this.animatorRect.cancel();
	}

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		this.trackedComp.getParent().repaint();
	}

	@Override
	protected void paintLayer(final Graphics2D g2, final JXLayer<JComponent> l) {
		super.paintLayer(g2, l);

		if (!this.valid) {
			if (this.shape != null) {
				g2.setClip(this.shape);
			}
			if (this.paint != null) {
				final Graphics2D gtmp = (Graphics2D) g2.create();
				this.paint.paint(gtmp, this.msg, l, this.animProgress);
				gtmp.dispose();
			}
		}

		if (this.label != null) {
			if (this.label.isVisible()) {

				if (this.label.getParent() instanceof ValidationOverlay) {
					final ValidationOverlay overlayable = (ValidationOverlay) this.label.getParent();
					g2.translate(this.label.getX(), this.label.getY() - overlayable.getOverlayLocationInsets().top);
				}
				this.label.paintComponent(g2);
			}
		}
	}

	public void setPaint(final ValidationPaint paint) {
		this.paint = paint;
	}

	public void setAnimationDuration(final long duration) {
		this.animatorRect.setDuration(duration);
	}

	public void setShape(final Shape shape) {
		this.shape = shape;
	}

	protected List<String> getErrors(final List<Validator<T>> checkers, final T compToTracked) {
		final List<String> why = new ArrayList<String>();
		String text;
		for (final Validator<T> checker : checkers) {
			text = checker.validate(compToTracked);
			if (text != null && !text.isEmpty()) {
				why.add(text);
			}
		}
		return why;
	}
}