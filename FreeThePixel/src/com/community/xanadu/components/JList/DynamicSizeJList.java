package com.community.xanadu.components.JList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;
import org.pushingpixels.trident.ease.Spline;

import com.community.xanadu.demo.components.JList.animated.Demo3AnimatedJList;
import com.community.xanadu.utils.JlistUtils;
import com.community.xanadu.utils.ThreadUtils;

public class DynamicSizeJList<T> extends JList {

	public static void main(final String[] args) {
		Demo3AnimatedJList.main(args);
	}

	private final DefaultListModel model;
	private int selectingIndex = -1;
	private int deselectingIndex = -1;
	private Timeline selectAnim;

	private int focusIndex;

	private final Map<Integer, Float> mapAnim = new HashMap<Integer, Float>();

	public DynamicSizeJList(final DynamicListCellRenderer<T> renderer) {
		super();
		this.model = new DefaultListModel();
		setModel(this.model);
		setCellRenderer(renderer);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				thisMouseReleased(e);
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(final MouseEvent e) {
				thisMouseMoved(e);
			}
		});
	}

	private void thisMouseMoved(final MouseEvent e) {
		final int newfocusindex = locationToIndex(e.getPoint());
		if (newfocusindex != this.focusIndex) {
			this.focusIndex = newfocusindex;
			repaint();
		}

	}

	private void thisMouseReleased(final MouseEvent e) {

		if (this.selectAnim.getState() == TimelineState.PLAYING_FORWARD) {
			return;
		}
		if (e.getClickCount() == 2 || e.getClickCount() == 1) {
			// if new selection
			if (this.selectingIndex != getSelectedIndex()) {
				this.deselectingIndex = this.selectingIndex;
				this.selectingIndex = getSelectedIndex();
				selectAnim(this.deselectingIndex, this.selectingIndex);
			}
		}
	}

	private void timingEvent(final int index, final float fraction) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				DynamicSizeJList.this.mapAnim.put(index, fraction);
				JlistUtils.computeListSize(DynamicSizeJList.this);
			}
		});
	}

	private void selectAnim(final int oldIndex, final int newIndex) {
		if (this.selectAnim != null) {
			this.selectAnim.cancel();
		}
		this.selectAnim = new Timeline();
		this.selectAnim.setDuration(500);
		this.selectAnim.addCallback(new TimelineCallback() {
			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				DynamicSizeJList.this.timingEvent(oldIndex, 1 - timelinePosition);
				DynamicSizeJList.this.timingEvent(newIndex, timelinePosition);
			}

			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					DynamicSizeJList.this.timingEvent(oldIndex, 0f);
					DynamicSizeJList.this.timingEvent(newIndex, 1f);
				}
			}
		});
		this.selectAnim.setEase(new Spline(0.7f));
		this.selectAnim.play();
		setSelectedIndex(newIndex);
	}

	public void addItem(final T t) {
		this.model.addElement(t);
		if (this.model.getSize() == 1) {
			this.selectingIndex = 0;
			selectAnim(-1, 0);
		}
	}

	

	public int getSelectingIndex() {
		return this.selectingIndex;
	}

	public int getDeselectingIndex() {
		return this.deselectingIndex;
	}

	public float getProgress(final int index) {

		Float res = this.mapAnim.get(index);
		if (res == null) {
			res = 0f;
		}
		return res;
	}

	public int getFocusIndex() {
		return this.focusIndex;
	}
}
