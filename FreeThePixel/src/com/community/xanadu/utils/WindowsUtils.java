package com.community.xanadu.utils;

import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;

public class WindowsUtils {
	/**
	 * add an action on window closing
	 * 
	 * @param window
	 * @param action
	 *            the exit action
	 */
	public static void addCloseAction(final Window window, final AbstractAction action) {
		if (action == null) {
			throw new IllegalArgumentException("The action cannot be null");
		}
		if (window == null) {
			throw new IllegalArgumentException("The window cannot be null");
		}

		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				action.actionPerformed(null);
			}
		});
	}

	// --------------------- Frame decoration
	/**
	 * Make the JFrame only have the close icon and to be not resizeable
	 * 
	 * @param frame
	 *            the JFrame to make NOT resizable/minimizable/maximizable
	 */
	public static void showOnlyCloseDecoration(final JFrame frame) {
		if (frame == null) {
			throw new IllegalArgumentException("The JFrame cannot be null");
		}
		frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		frame.setResizable(false);
	}

	/**
	 * Hide the decoration around a JFrame
	 * 
	 * @param frame
	 *            the JFrame to make NOT
	 *            resizable/minimizable/maximizable/closeable
	 */
	public static void hideDecoration(final JFrame frame) {
		if (frame == null) {
			throw new IllegalArgumentException("The JFrame cannot be null");
		}
		frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
	}

	/**
	 * Hide the decoration around a JDialog
	 * 
	 * @param frame
	 *            the JDialog to make NOT
	 *            resizable/minimizable/maximizable/closeable
	 */
	public static void hideDecoration(final JDialog frame) {
		if (frame == null) {
			throw new IllegalArgumentException("The JDialog cannot be null");
		}
		frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
	}

	/**
	 * make the given window fit the entire screen
	 * 
	 * @param window
	 */
	public static void setFullScreenWindow(final Window window) {
		if (window == null) {
			throw new IllegalArgumentException("The window cannot be null");
		}
		window.setSize(Toolkit.getDefaultToolkit().getScreenSize());
	}

	public static boolean setOpaque(final Window w, final boolean flag) {
		if (!flag) {
			if (w instanceof JFrame) {
				((JFrame) w).setUndecorated(true);
				WindowsUtils.hideDecoration((JFrame) w);
			} else if (w instanceof JDialog) {
				((JDialog) w).setUndecorated(true);
				WindowsUtils.hideDecoration((JDialog) w);
			}
		}
		try {
			// this feature is not present before J6U10 so use reflection to
			// acces it
			Class<?> c = Class.forName("com.sun.awt.AWTUtilities");
			Method m = c.getMethod("setWindowOpaque", Window.class, boolean.class);
			m.invoke(null, w, flag);
			return true;
		} catch (ClassNotFoundException e) {
			System.out.println("java 6 U 10 + not available");
			return false;
		} catch (Exception e) {
			// e.printStackTrace();
			return true;
		}
	}

	public static boolean setOpacity(final Window w, final float alpha) {
		try {
			// this feature is not present before J6U10 so use reflection to
			// acces it
			Class<?> c = Class.forName("com.sun.awt.AWTUtilities");
			Method m = c.getMethod("setWindowOpacity", Window.class, float.class);
			m.invoke(null, w, alpha);
			return true;
		} catch (ClassNotFoundException e) {
			System.out.println("java 6 U 10 + not available");
			return false;
		} catch (Exception e) {
			// e.printStackTrace();
			return true;
		}
	}

	public static boolean setShape(final Window w, final Shape shape) {
		try {
			// this feature is not present before J6U10 so use reflection to
			// acces it
			Class<?> c = Class.forName("com.sun.awt.AWTUtilities");
			Method m = c.getMethod("setWindowShape", Window.class, Shape.class);
			m.invoke(null, w, shape);
			return true;
		} catch (ClassNotFoundException e) {
			System.out.println("java 6 U 10 + not available");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}
}
