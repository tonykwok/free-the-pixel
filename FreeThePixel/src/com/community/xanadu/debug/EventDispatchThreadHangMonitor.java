package com.community.xanadu.debug;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.community.xanadu.utils.ThreadUtils;

/**
 * Monitors the AWT event dispatch thread for events that take longer than a
 * certain time to be dispatched.
 * <p/>
 * The principle is to record the time at which we start processing an event,
 * and have another thread check frequently to see if we're still processing. If
 * the other thread notices that we've been processing a single event for too
 * long, it prints a stack trace showing what the event dispatch thread is
 * doing, and continues to time it until it finally finishes.
 * <p/>
 * This is useful in determining what code is causing your Java application's
 * GUI to be unresponsive.
 * 
 * <p>
 * The original blog can be found here<br>
 * <a href=
 * "http://elliotth.blogspot.com/2005/05/automatically-detecting-awt-event.html"
 * > Automatically detecting AWT event dispatch thread hangs</a>
 * </p>
 * 
 * @author Elliott Hughes <enh@jessies.org>
 * 
 *         Advice, bug fixes, and test cases from Alexander Potochkin and Oleg
 *         Sukhodolsky.
 * 
 *         https://swinghelper.dev.java.net/
 */
public final class EventDispatchThreadHangMonitor extends EventQueue {
	private static final EventDispatchThreadHangMonitor INSTANCE = new EventDispatchThreadHangMonitor();

	// Time to wait between checks that the event dispatch thread isn't hung.
	private static final long CHECK_INTERVAL_MS = 100;

	// Maximum time we won't warn about. This used to be 500 ms, but 1.5 on
	// late-2004 hardware isn't really up to it; there are too many parts of
	// the JDK that can go away for that long (often code that has to be
	// called on the event dispatch thread, like font loading).
	private static final long UNREASONABLE_DISPATCH_DURATION_MS = 500;

	// Help distinguish multiple hangs in the log, and match start and end too.
	// Only access this via getNewHangNumber.
	private static int hangCount = 0;

	// Prevents us complaining about hangs during start-up, which are probably
	// the JVM vendor's fault.
	private boolean haveShownSomeComponent = false;

	// The currently outstanding event dispatches. The implementation of
	// modal dialogs is a common cause for multiple outstanding dispatches.
	private final LinkedList<DispatchInfo> dispatches = new LinkedList<DispatchInfo>();

	private static class DispatchInfo {
		// The last-dumped hung stack trace for this dispatch.
		private StackTraceElement[] lastReportedStack;
		// If so; what was the identifying hang number?
		private int hangNumber;

		// The EDT for this dispatch (for the purpose of getting stack traces).
		// I don't know of any API for getting the event dispatch thread,
		// but we can assume that it's the current thread if we're in the
		// middle of dispatching an AWT event...
		// We can't cache this because the EDT can die and be replaced by a
		// new EDT if there's an uncaught exception.
		private final Thread eventDispatchThread = Thread.currentThread();

		// The last time in milliseconds at which we saw a dispatch on the above
		// thread.
		private long lastDispatchTimeMillis = System.currentTimeMillis();

		public DispatchInfo() {
			// All initialization is done by the field initializers.
		}

		public void checkForHang() {
			if (timeSoFar() > UNREASONABLE_DISPATCH_DURATION_MS) {
				examineHang();
			}
		}

		// We can't use StackTraceElement.equals because that insists on
		// checking the filename and line number.
		// That would be version-specific.
		private static boolean stackTraceElementIs(final StackTraceElement e, final String className,
				final String methodName, final boolean isNative) {
			return e.getClassName().equals(className) && e.getMethodName().equals(methodName)
					&& e.isNativeMethod() == isNative;
		}

		// Checks whether the given stack looks like it's waiting for another
		// event.
		// This relies on JDK implementation details.
		private boolean isWaitingForNextEvent(final StackTraceElement[] currentStack) {
			return stackTraceElementIs(currentStack[0], "java.lang.Object", "wait", true)
					&& stackTraceElementIs(currentStack[1], "java.lang.Object", "wait", false)
					&& stackTraceElementIs(currentStack[2], "java.awt.EventQueue", "getNextEvent", false);
		}

		private void examineHang() {
			StackTraceElement[] currentStack = this.eventDispatchThread.getStackTrace();

			if (isWaitingForNextEvent(currentStack)) {
				// Don't be fooled by a modal dialog if it's waiting for its
				// next event.
				// As long as the modal dialog's event pump doesn't get stuck,
				// it's okay for the outer pump to be
				// suspended.
				return;
			}

			if (stacksEqual(this.lastReportedStack, currentStack)) {
				// Don't keep reporting the same hang every time the timer goes
				// off.
				return;
			}

			this.hangNumber = getNewHangNumber();
			String stackTrace = stackTraceToString(currentStack);
			this.lastReportedStack = currentStack;
			Log.warn("(hang #" + this.hangNumber + ") event dispatch thread stuck processing event for " + timeSoFar()
					+ " ms:" + stackTrace);
			checkForDeadlock();
		}

		private static boolean stacksEqual(final StackTraceElement[] a, final StackTraceElement[] b) {
			if (a == null) {
				return false;
			}
			if (a.length != b.length) {
				return false;
			}
			for (int i = 0; i < a.length; ++i) {
				if (a[i].equals(b[i]) == false) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Returns how long this dispatch has been going on (in milliseconds).
		 */
		private long timeSoFar() {
			return (System.currentTimeMillis() - this.lastDispatchTimeMillis);
		}

		public void dispose() {
			if (this.lastReportedStack != null) {
				Log.warn("(hang #" + this.hangNumber + ") event dispatch thread unstuck after " + timeSoFar() + " ms.");
			}
		}
	}

	private EventDispatchThreadHangMonitor() {
		initTimer();
	}

	/**
	 * Sets up a timer to check for hangs frequently.
	 */
	private void initTimer() {
		final long initialDelayMs = 0;
		final boolean isDaemon = true;
		Timer timer = new Timer("EventDispatchThreadHangMonitor", isDaemon);
		timer.schedule(new HangChecker(), initialDelayMs, CHECK_INTERVAL_MS);
	}

	private class HangChecker extends TimerTask {
		@Override
		public void run() {
			synchronized (EventDispatchThreadHangMonitor.this.dispatches) {
				if (EventDispatchThreadHangMonitor.this.dispatches.isEmpty()
						|| !EventDispatchThreadHangMonitor.this.haveShownSomeComponent) {
					// Nothing to do.
					// We don't destroy the timer when there's nothing happening
					// because it would mean a lot more work on every single AWT
					// event that gets dispatched.
					return;
				}
				// Only the most recent dispatch can be hung; nested dispatches
				// by their nature cause the outer dispatch pump to be
				// suspended.
				EventDispatchThreadHangMonitor.this.dispatches.getLast().checkForHang();
			}
		}
	}

	/**
	 * Sets up hang detection for the event dispatch thread.
	 */
	public static void install() {
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(INSTANCE);
	}

	/**
	 * Overrides EventQueue.dispatchEvent to call our pre and post hooks either
	 * side of the system's event dispatch code.
	 */
	@Override
	protected void dispatchEvent(final AWTEvent event) {
		try {
			preDispatchEvent();
			super.dispatchEvent(event);
		} finally {
			postDispatchEvent();
			if (!this.haveShownSomeComponent && event instanceof WindowEvent
					&& event.getID() == WindowEvent.WINDOW_OPENED) {
				this.haveShownSomeComponent = true;
			}
		}
	}

	/**
	 * Starts tracking a dispatch.
	 */
	private synchronized void preDispatchEvent() {
		synchronized (this.dispatches) {
			this.dispatches.addLast(new DispatchInfo());
		}
	}

	/**
	 * Stops tracking a dispatch.
	 */
	private synchronized void postDispatchEvent() {
		synchronized (this.dispatches) {
			// We've finished the most nested dispatch, and don't need it any
			// longer.
			DispatchInfo justFinishedDispatch = this.dispatches.removeLast();
			justFinishedDispatch.dispose();

			// The other dispatches, which have been waiting, need to be
			// credited extra time.
			// We do this rather simplistically by pretending they've just been
			// redispatched.
			Thread currentEventDispatchThread = Thread.currentThread();
			for (DispatchInfo dispatchInfo : this.dispatches) {
				if (dispatchInfo.eventDispatchThread == currentEventDispatchThread) {
					dispatchInfo.lastDispatchTimeMillis = System.currentTimeMillis();
				}
			}
		}
	}

	private static void checkForDeadlock() {
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		long[] threadIds = threadBean.findMonitorDeadlockedThreads();
		if (threadIds == null) {
			return;
		}
		Log.warn("deadlock detected involving the following threads:");
		ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadIds, Integer.MAX_VALUE);
		for (ThreadInfo info : threadInfos) {
			Log.warn("Thread #" + info.getThreadId() + " " + info.getThreadName() + " (" + info.getThreadState()
					+ ") waiting on " + info.getLockName() + " held by " + info.getLockOwnerName()
					+ stackTraceToString(info.getStackTrace()));
		}
	}

	private static String stackTraceToString(final StackTraceElement[] stackTrace) {
		StringBuilder result = new StringBuilder();
		// We used to avoid showing any code above where this class gets
		// involved in event dispatch, but that hides potentially useful
		// information when dealing with modal dialogs. Maybe we should
		// reinstate that, but search from the other end of the stack?
		for (StackTraceElement stackTraceElement : stackTrace) {
			String indentation = "    ";
			result.append("\n" + indentation + stackTraceElement);
		}
		return result.toString();
	}

	private synchronized static int getNewHangNumber() {
		return ++hangCount;
	}

	private static class Log {
		public static void warn(final String str) {
			System.out.println(str);
		}
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
				JButton b = new JButton("Do something long in EDT");
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent arg0) {
						ThreadUtils.sleepQuietly(2000);
					}
				});
				f.getContentPane().add(b);
				f.setVisible(true);
			}
		});
	}
}
