package com.community.xanadu.listeners;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Draggable extends MouseAdapter {

	private Point lastPoint;
	private Component compGetEvent;
	private Component toMove;

	public Draggable(final Component compGetEvent, final Component toMove) {
		boolean alreadyDraggable = false;
		for (MouseListener ml : compGetEvent.getMouseListeners()) {
			if (ml instanceof Draggable) {
				alreadyDraggable = true;
			}
		}

		if (!alreadyDraggable) {
			compGetEvent.addMouseMotionListener(this);
			compGetEvent.addMouseListener(this);
			this.toMove = toMove;
			this.compGetEvent = compGetEvent;
		}
	}

	public Draggable(final Component c) {
		this(c, c);
	}

	public static void makeDraggable(final Component compGetEvent) {
		makeDraggable(compGetEvent, compGetEvent);
	}

	public static void makeDraggable(final Component compGetEvent, final Component toMove) {
		new Draggable(compGetEvent, toMove);
	}

	@Override
	public void mousePressed(final MouseEvent me) {
		// else if can t be rezize
		if (this.compGetEvent.getCursor().equals(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))) {
			this.lastPoint = me.getPoint();
		} else {
			this.lastPoint = null;
		}
	}

	@Override
	public void mouseReleased(final MouseEvent me) {
		this.lastPoint = null;
	}

	@Override
	public void mouseMoved(final MouseEvent me) {
	}

	@Override
	public void mouseDragged(final MouseEvent me) {
		int x, y;
		if (this.lastPoint != null) {
			x = this.toMove.getX() + (me.getX() - (int) this.lastPoint.getX());
			y = this.toMove.getY() + (me.getY() - (int) this.lastPoint.getY());
			this.toMove.setLocation(x, y);
		}
	}
}
