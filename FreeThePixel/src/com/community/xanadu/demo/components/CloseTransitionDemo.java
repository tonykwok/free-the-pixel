package com.community.xanadu.demo.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;

import com.community.xanadu.components.transition.CloseTransition;
import com.community.xanadu.components.transition.impl.CircleTransition;
import com.community.xanadu.components.transition.impl.FadeOutTransition;
import com.community.xanadu.components.transition.impl.PinchTransition;
import com.community.xanadu.components.transition.impl.RectanglesTransition;
import com.community.xanadu.components.transition.impl.RotateTransition;
import com.community.xanadu.components.transition.impl.ShrinkTransition;
import com.community.xanadu.utils.ThreadUtils;

@SuppressWarnings("serial")
public class CloseTransitionDemo extends JFrame {

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (final UnsupportedLookAndFeelException e1) {
				}

				final CloseTransitionDemo inst = new CloseTransitionDemo();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JFrame frameTransition;

	public CloseTransitionDemo() {
		super();
		setTitle("close transition demo");
		initGUI();
	}

	private void initGUI() {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		getContentPane().setLayout(new MigLayout("fill,wrap 3"));
		JButton b = new JButton("RectanglesTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				startCloseTransition(new RectanglesTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");

		b = new JButton("CircleTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				startCloseTransition(new CircleTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");

		b = new JButton("FadeOutTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				startCloseTransition(new FadeOutTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");

		b = new JButton("PinchTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				startCloseTransition(new PinchTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");

		b = new JButton("RotateTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				startCloseTransition(new RotateTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");

		b = new JButton("ShrinkTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {

				startCloseTransition(new ShrinkTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");

		setSize(500, 300);

	}

	public JFrame getFrameTransition() {
		if (this.frameTransition == null) {
			this.frameTransition = new JFrame();
			this.frameTransition.getContentPane().setLayout(new MigLayout("fill,wrap 3"));
			for (int i = 0; i < 20; i++) {
				this.frameTransition.getContentPane().add(new JButton("" + i));
			}
			this.frameTransition.setSize(900, 600);
		}
		return this.frameTransition;
	}

	private void startCloseTransition(final CloseTransition transition) {
		transition.setAnimDuration(500);
		transition.setEndAction(new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
			}
		});
		this.frameTransition.setVisible(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				ThreadUtils.sleepQuietly(500);
				transition.startCloseTransition();
			}
		}).start();
	}

}
