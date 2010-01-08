package com.community.xanadu.listeners;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class CoalescentChangeListener implements ChangeListener {

	private long time = -1l;

	@SuppressWarnings("serial")
	private Timer timer = new Timer(500, new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			CoalescentChangeListener.this.timer.stop();
			doAction();
		}
	});

	public CoalescentChangeListener() {
	}

	@Override
	public synchronized void stateChanged(final ChangeEvent e) {
		long newTime = System.currentTimeMillis();
		if (newTime - this.time < 500) {
			this.timer.stop();
		}
		this.time = System.currentTimeMillis();
		this.timer.start();
	}

	public abstract void doAction();
}
