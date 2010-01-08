package com.community.xanadu.debug;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.UiThreadingViolationException;

public class EventDispatchThreadViolationRepaintManager extends RepaintManager {
	@Override
	public synchronized void addInvalidComponent(final JComponent component) {
		checkThreadViolations(component);
		super.addInvalidComponent(component);
	}

	@Override
	public void addDirtyRegion(final JComponent component, final int x, final int y, final int w, final int h) {
		checkThreadViolations(component);
		super.addDirtyRegion(component, x, y, w, h);
	}

	private void checkThreadViolations(final JComponent c) {
		if (!SwingUtilities.isEventDispatchThread() && c.isShowing()) {
			Exception exception = new UiThreadingViolationException(
					"Component methods call must be done on the Event Dispatch Thread");
			boolean repaint = false;
			boolean fromSwing = false;
			boolean threadSafeMethod = false;
			StackTraceElement[] stackTrace = exception.getStackTrace();
			for (StackTraceElement st : stackTrace) {
				if (repaint && st.getClassName().startsWith("javax.swing.")) {
					fromSwing = true;
				}
				if ("repaint".equals(st.getMethodName())) {
					repaint = true;
				}

				if (st.getClassName().startsWith("javax.swing.text.JTextComponent")
						&& st.getMethodName().equals("setText")) {
					threadSafeMethod = true;
				}
			}
			if ((repaint && !fromSwing) || threadSafeMethod) {
				// no problems here, since repaint() is thread safe
				return;
			}
			exception.printStackTrace();
		}
	}

	public static void install() {
		RepaintManager.setCurrentManager(new EventDispatchThreadViolationRepaintManager());
	}

	public static void main(final String[] args) {
		install();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(800, 600);
				f.getContentPane().setLayout(new MigLayout());
				JButton b = new JButton("switch foreground color");
				final JLabel label = new JLabel("EDT TEST");
				b.addActionListener(new ActionListener() {
					int cpt = 0;

					@Override
					public void actionPerformed(final ActionEvent arg0) {
						// we are in the EDT
						// Start a new thread to run the code outside the EDT
						// it could simulate a signal from a machine
						new Thread(new Runnable() {
							@Override
							public void run() {
								if (cpt % 2 == 0) {
									label.setForeground(Color.red);

								} else {
									label.setForeground(Color.green);
								}
								cpt++;
							}
						}).start();

					}
				});
				f.getContentPane().add(label);
				f.getContentPane().add(b);
				f.setVisible(true);
			}
		});
	}
}