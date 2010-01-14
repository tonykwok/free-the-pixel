package com.community.xanadu.components.windows;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;

import com.community.xanadu.utils.WindowsUtils;

public class WindowFadeInManager {
	private static int FADE_IN_DURATION = 500;

	public static void fadeIn(final Window window, final int duration) {
		final TimelineCallback tt = new TimelineCallback() {
			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				WindowsUtils.setOpacity(window, timelinePosition);
			}

			public void onTimelineStateChanged(final Timeline.TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					WindowsUtils.setOpacity(window, timelinePosition);
				}
			}
		};
		WindowsUtils.setOpacity(window, 0);
		final Timeline anim = new Timeline();
		anim.setDuration(duration);
		anim.addCallback(tt);

		anim.play();
		window.setVisible(true);
	}

	public static void fadeIn(final Window window) {
		fadeIn(window, FADE_IN_DURATION);
	}

	public static void fadeOut(final Window window) {
		fadeOut(window, FADE_IN_DURATION);
	}

	public static void fadeOut(final Window window, final int duration) {
		final TimelineCallback tt = new TimelineCallback() {
			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				WindowsUtils.setOpacity(window, 1 - timelinePosition);
			}

			public void onTimelineStateChanged(final Timeline.TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					WindowsUtils.setOpacity(window, 0);
					window.setVisible(false);
					window.dispose();
				}
			}
		};
		final Timeline anim = new Timeline();
		anim.setDuration(duration);
		anim.addCallback(tt);
		anim.play();
	}

	public static int fadeIn(final JOptionPane optionPane, final String title, final Component parent) {
		return fadeIn(optionPane, title, parent, FADE_IN_DURATION);
	}

	public static int fadeIn(final JOptionPane optionPane, final String title, final Component parent,
			final int duration) {
		final JDialog dialog = optionPane.createDialog(parent, title);
		fadeIn(dialog, duration);
		return (Integer) optionPane.getValue();
	}
}
