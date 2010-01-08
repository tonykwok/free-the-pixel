package com.community.xanadu.components.windows;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;

import com.community.xanadu.utils.ThreadUtils;
import com.community.xanadu.utils.WindowsUtils;

public class WindowFadeInManager {
	private static int FADE_IN_DURATION = 1000;

	public static void fadeIn(final Window window, final int duration) {
		TimelineCallback tt = new TimelineCallback() {
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
		Timeline anim = new Timeline();
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
		TimelineCallback tt = new TimelineCallback() {
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
		Timeline anim = new Timeline();
		anim.setDuration(duration);
		anim.addCallback(tt);
		anim.play();
	}

	public static int fadeIn(final JOptionPane optionPane, final String title, final Component parent) {
		return fadeIn(optionPane, title, parent, FADE_IN_DURATION);
	}

	public static int fadeIn(final JOptionPane optionPane, final String title, final Component parent,
			final int duration) {
		JDialog dialog = optionPane.createDialog(parent, title);
		fadeIn(dialog, duration);
		return (Integer) optionPane.getValue();
	}
}
