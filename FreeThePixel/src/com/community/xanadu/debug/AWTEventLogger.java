package com.community.xanadu.debug;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.io.PrintStream;

public class AWTEventLogger {
	public static PrintStream out = System.out;

	/**
	 * show all the mouse events
	 */
	public static void turnOnMouseEventDebug() {
		turnOnAWTEventDebug(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
				| AWTEvent.MOUSE_WHEEL_EVENT_MASK);
	}

	/**
	 * show all the keyboard event
	 */
	public static void turnOnKeyboardEventDebug() {
		turnOnAWTEventDebug(AWTEvent.KEY_EVENT_MASK);
	}

	/**
	 * 
	 * @param mask
	 *            the mask of AWTEvent <br>
	 *            for example
	 *            AWTEvent.MOUSE_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK
	 */
	public static void turnOnAWTEventDebug(final long mask) {
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(final AWTEvent event) {
				out.println(event);
			}
		}, mask);
	}

	/**
	 * set the output stream<br>
	 * by default it's System.out
	 * 
	 * @param out
	 *            the PrintStream
	 */
	public static void setOutputStream(final PrintStream out) {
		AWTEventLogger.out = out;
	}
}
