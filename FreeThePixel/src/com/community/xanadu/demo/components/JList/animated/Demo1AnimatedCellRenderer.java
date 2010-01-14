package com.community.xanadu.demo.components.JList.animated;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;

import com.community.xanadu.utils.JlistUtils;

public class Demo1AnimatedCellRenderer {

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (final UnsupportedLookAndFeelException e) {
				}
				final JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(300, 300);

				final DefaultListModel model = new DefaultListModel();
				final JList list = new JList(model);
				for (int i = 0; i < 10; i++) {
					model.addElement("element:" + i);
				}
				f.getContentPane().add(new JScrollPane(list));
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

				// store the animation values
				final Map<Integer, Float> mapAnimation = new HashMap<Integer, Float>();

				final ListCellRenderer renderer = new SubstanceDefaultListCellRenderer() {
					@Override
					public Component getListCellRendererComponent(final JList list, final Object value,
							final int index, final boolean isSelected, final boolean cellHasFocus) {

						final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index,
								isSelected, cellHasFocus);

						Float progress = mapAnimation.get(index);
						if (progress == null) {
							progress = 0f;
						}
						label.setFont(label.getFont().deriveFont(10 + 20 * progress));
						label.setPreferredSize(new Dimension(50, (int) (15 + 35 * progress)));
						return label;
					}
				};
				list.setCellRenderer(renderer);

				final int[] oldSelected = new int[] { -1 };

				list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(final ListSelectionEvent e) {

						if (e.getValueIsAdjusting()) {
							return;
						}
						final Timeline timeline = new Timeline();
						timeline.setDuration(200);
						timeline.addCallback(new TimelineCallback() {

							@Override
							public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
								// set the progress for the selected index
								mapAnimation.put(list.getSelectedIndex(), durationFraction);
								if (oldSelected[0] != -1) {
									mapAnimation.put(oldSelected[0], 1 - durationFraction);
								}
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										JlistUtils.computeListSize(list);
									}
								});
							}

							@Override
							public void onTimelineStateChanged(final TimelineState oldState,
									final TimelineState newState, final float durationFraction,
									final float timelinePosition) {
								onTimelinePulse(durationFraction, timelinePosition);
								if (newState == TimelineState.IDLE) {
									oldSelected[0] = list.getSelectedIndex();
								}
							}
						});
						timeline.play();
					}
				});
				f.setVisible(true);
			}
		});
	}
}
