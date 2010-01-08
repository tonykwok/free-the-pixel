package com.community.xanadu.components.text;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

public class UndoableTextManager {
	private UndoableTextManager() {
	}

	public static void makeUndoable(final JTextComponent comp) {
		if (comp == null) {
			throw new IllegalArgumentException("The JTextComponent can not be null");
		}
		final UndoManager undoManager = new UndoManager();

		comp.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(final UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
			}
		});

		comp.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.isControlDown()) {
					if (e.getKeyCode() == KeyEvent.VK_Z) {// undo
						if (undoManager.canUndo()) {
							undoManager.undo();
						}
					} else if (e.getKeyCode() == KeyEvent.VK_Y) {// redo
						if (undoManager.canRedo()) {
							undoManager.redo();
						}
					}
				}
			}
		});
	}

	public static void main(final String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(300, 300);
		JTextArea jta = new JTextArea();
		makeUndoable(jta);
		f.getContentPane().add(jta);
		f.setVisible(true);
	}
}
