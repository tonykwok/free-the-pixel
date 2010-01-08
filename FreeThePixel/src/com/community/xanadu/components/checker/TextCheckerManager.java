package com.community.xanadu.components.checker;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.community.xanadu.components.checker.CheckerManager.eViewError;

public class TextCheckerManager {
	private TextCheckerManager() {
	}

	/**
	 * return a component that give visual feedback if the text component does
	 * not pass the checkers tests
	 * 
	 * @param textComp
	 *            the JTextComponent to be wrapped
	 * @param checkers
	 *            the checkers to control the input of the JTextComponent
	 * @param type
	 *            the type of visual feedback
	 * @return JComponent the wrapping JComponent around the JTextComponent
	 */
	public static JComponent getTextComponentWithCheckers(final JTextComponent textComp,
			final List<Checker<JTextComponent>> checkers, final eViewError type, final Shape shape) {
		textComp.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(final DocumentEvent e) {
				CheckerManager.check(textComp);
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				CheckerManager.check(textComp);
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				CheckerManager.check(textComp);
			}
		});

		return CheckerManager.getComponentWithCheckers(textComp, checkers, type, shape);
	}

	public static JComponent getTextComponentWithCheckers(final JTextComponent textComp,
			final Checker<JTextComponent> checker, final eViewError type, final Shape shape) {
		ArrayList<Checker<JTextComponent>> checkers = new ArrayList<Checker<JTextComponent>>();
		checkers.add(checker);
		return getTextComponentWithCheckers(textComp, checkers, type, shape);

	}

	public static JComponent getTextComponentWithCheckers(final JTextComponent textComp,
			final Checker<JTextComponent> checker, final eViewError type) {
		return getTextComponentWithCheckers(textComp, checker, type, null);
	}

	public static JComponent getTextComponentWithCheckers(final JTextComponent textComp,
			final List<Checker<JTextComponent>> checkers, final eViewError type) {
		return getTextComponentWithCheckers(textComp, checkers, type, null);
	}
}
