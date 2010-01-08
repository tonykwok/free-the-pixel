package com.community.xanadu.demo.components.AnimatedListCellRenderer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
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

public class AnimatedCellRendererDemo extends JPanel {
	private static final long serialVersionUID = -4633908084562959340L;
	private JScrollPane scroll;
	private DefaultListModel listModel;
	private JList jlist;
	private JButton buttonAdd;

	public AnimatedCellRendererDemo() {
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
			this.jlist.setCellRenderer(new AnimatedCellRenderer());
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

	private void jlistMouseClicked(final MouseEvent evt) {
		if (evt.getClickCount() != 2) {
			return;
		}

		final int i = AnimatedCellRendererDemo.this.jlist.getSelectedIndex();

		Timeline anim = new Timeline();
		anim.setDuration(300);
		anim.addCallback(new TimelineCallback() {

			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {

					((DefaultListModel) AnimatedCellRendererDemo.this.jlist.getModel()).remove(i);
					((AnimatedCellRenderer) AnimatedCellRendererDemo.this.jlist.getCellRenderer())
							.setAnimProgress(i, 1);
					AnimatedCellRendererDemo.this.jlist.repaint();
				}
			}

			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				((AnimatedCellRenderer) AnimatedCellRendererDemo.this.jlist.getCellRenderer()).setAnimProgress(i,
						1 - timelinePosition);
				AnimatedCellRendererDemo.this.jlist.repaint();
			}
		});

		anim.play();
	}

	final public void addElement(final String text, final String code) {
		((AnimatedCellRenderer) this.jlist.getCellRenderer()).setAnimProgress(this.listModel.getSize(), 0f);
		this.listModel.addElement(new AnimatedCellRenderer.Message(code, text));

		final int i = AnimatedCellRendererDemo.this.listModel.getSize() - 1;

		Timeline anim = new Timeline();
		anim.setDuration(2000);
		anim.addCallback(new TimelineCallback() {
			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					((AnimatedCellRenderer) AnimatedCellRendererDemo.this.jlist.getCellRenderer()).setAnimProgress(i,
							timelinePosition);
					AnimatedCellRendererDemo.this.jlist.repaint();
				}
			}

			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				((AnimatedCellRenderer) AnimatedCellRendererDemo.this.jlist.getCellRenderer()).setAnimProgress(i,
						timelinePosition);
				AnimatedCellRendererDemo.this.jlist.repaint();
			}
		});
		anim.play();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				AnimatedCellRendererDemo.this.scroll.getVerticalScrollBar().setValue(
						AnimatedCellRendererDemo.this.scroll.getVerticalScrollBar().getMaximum());
			}
		});
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
				} catch (UnsupportedLookAndFeelException e) {
				}
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new BorderLayout());
				f.getContentPane().add(new AnimatedCellRendererDemo());
				f.setSize(300, 500);
				f.setVisible(true);
			}
		});
	}
}
