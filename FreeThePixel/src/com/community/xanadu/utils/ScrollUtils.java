package com.community.xanadu.utils;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class ScrollUtils {
	public static enum ScrollDirection {
		UP, DOWN
	}

	public static void VScroll(final ScrollDirection direction, final JScrollPane scroll) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				int d = 1;
				switch (direction) {
				case DOWN:
					d = -1;
					break;
				case UP:
					d = 1;
					break;
				}
				int oldValue = scroll.getVerticalScrollBar().getValue();
				int blockIncrement = scroll.getVerticalScrollBar().getBlockIncrement(d);
				int delta = blockIncrement * ((d > 0) ? +1 : -1);
				int newValue = oldValue + delta;

				// Check for overflow.
				if (delta > 0 && newValue < oldValue) {
					newValue = scroll.getVerticalScrollBar().getMaximum();
				} else if (delta < 0 && newValue > oldValue) {
					newValue = scroll.getVerticalScrollBar().getMinimum();
				}
				scroll.getVerticalScrollBar().setValue(newValue);
			}
		});
	}

	public static void VScroll(final ScrollDirection direction, final int offset, final JScrollPane scroll) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				int d = 1;
				switch (direction) {
				case DOWN:
					d = -1;
					break;
				case UP:
					d = 1;
					break;
				}
				int oldValue = scroll.getVerticalScrollBar().getValue();
				int newValue = oldValue + d * offset;
				scroll.getVerticalScrollBar().setValue(newValue);
			}
		});
	}

	public static boolean isScrollToMax(final JScrollBar bar) {
		return (bar.getVisibleAmount() + bar.getValue() == bar.getMaximum());
	}

}
