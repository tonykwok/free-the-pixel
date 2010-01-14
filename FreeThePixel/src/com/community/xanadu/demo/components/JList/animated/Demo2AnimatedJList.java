package com.community.xanadu.demo.components.JList.animated;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;


public class Demo2AnimatedJList extends JPanel {

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
				f.getContentPane().setLayout(new BorderLayout());
				f.getContentPane().add(new Demo2AnimatedJList());
				f.getContentPane().add(new JLabel("Double click an item to remove it"),BorderLayout.SOUTH);
				f.setSize(300, 500);
				f.setVisible(true);
			}
		});
	}

	private JScrollPane scroll;
	private DefaultListModel listModel;
	private JList jlist;
	private JButton buttonAdd;

	public Demo2AnimatedJList() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill, inset 0 0 0 0"));
		add(getScroll(), "grow,push,wrap");
		add(getButtonAdd(), "growx");
	}

	private JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane(getJlist());
			this.scroll.setBorder(BorderFactory.createEmptyBorder());

		}
		return this.scroll;
	}

	private JList getJlist() {
		if (this.jlist == null) {
			this.listModel = new DefaultListModel();
			this.jlist = new JList(this.listModel);
			this.jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.jlist.setCellRenderer(new Demo2AnimatedCellRenderer());
			this.jlist.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent evt) {
					jlistMouseClicked(evt);
				}
			});
		}
		return this.jlist;
	}

	public JButton getButtonAdd() {
		if (this.buttonAdd == null) {
			this.buttonAdd = new JButton("ADD");
			this.buttonAdd.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonAddActionPerformed();
				}
			});
		}
		return this.buttonAdd;
	}

	private void buttonAddActionPerformed() {
		addElement("some more text", "some text");
	}

	//remove an element
	private void jlistMouseClicked(final MouseEvent evt) {
		if (evt.getClickCount() != 2) {
			return;
		}

		final int i = Demo2AnimatedJList.this.jlist.getSelectedIndex();

		final Timeline anim = new Timeline();
		anim.setDuration(300);
		anim.addCallback(new TimelineCallback() {

			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
						((DefaultListModel) Demo2AnimatedJList.this.jlist.getModel()).remove(i);
					((Demo2AnimatedCellRenderer) Demo2AnimatedJList.this.jlist.getCellRenderer())
							.setAnimProgress(i, 1);
					Demo2AnimatedJList.this.jlist.repaint();
				}
			}

			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				((Demo2AnimatedCellRenderer) Demo2AnimatedJList.this.jlist.getCellRenderer()).setAnimProgress(i,
						1 - timelinePosition);
				Demo2AnimatedJList.this.jlist.repaint();
			}
		});

		anim.play();
	}

	final public void addElement(final String text, final String code) {
		((Demo2AnimatedCellRenderer) this.jlist.getCellRenderer()).setAnimProgress(this.listModel.getSize(), 0f);
		this.listModel.addElement(new MessageItem(code, text));

		final int i = Demo2AnimatedJList.this.listModel.getSize() - 1;

		final Timeline anim = new Timeline();
		anim.addCallback(new TimelineCallback() {
			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					((Demo2AnimatedCellRenderer) Demo2AnimatedJList.this.jlist.getCellRenderer()).setAnimProgress(i,
							timelinePosition);
					Demo2AnimatedJList.this.jlist.repaint();
				}
			}

			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				((Demo2AnimatedCellRenderer) Demo2AnimatedJList.this.jlist.getCellRenderer()).setAnimProgress(i,
						timelinePosition);
				Demo2AnimatedJList.this.jlist.repaint();
			}
		});
		anim.play();

		// need to call invokeLater even if we are in the EDT else the scroll
		// bar will only scroll to the one before
		// last item in the list
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Demo2AnimatedJList.this.scroll.getVerticalScrollBar().setValue(
						Demo2AnimatedJList.this.scroll.getVerticalScrollBar().getMaximum());
			}
		});
	}
}
