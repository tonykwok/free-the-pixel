package com.community.xanadu.components.validation;

import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class TextValidationOverlayFactory {

	public static ValidationOverlay createBlinkingAndIconComponent(final JTextComponent t,
			final List<Validator<JTextComponent>> checkers) {
		installListener(t);
		return ValidationOverlayFactory.createBlinkingAndIcon(t, checkers);
	}

	public static ValidationOverlay createBlinkingAndIconComponent(final JTextComponent t,
			final Validator<JTextComponent> checker) {
		installListener(t);
		return ValidationOverlayFactory.createBlinkingAndIcon(t, checker);
	}

	// -------------

	public static ValidationOverlay createBlinking(final JTextComponent textComp,
			final List<Validator<JTextComponent>> checkers) {
		installListener(textComp);
		return ValidationOverlayFactory.createBlinking(textComp, checkers);
	}

	public static ValidationOverlay createBlinking(final JTextComponent textComp,
			final Validator<JTextComponent> checker) {
		installListener(textComp);
		return ValidationOverlayFactory.createBlinking(textComp, checker);
	}

	// --------------
	public static ValidationOverlay createErrorOverlayedIcon(final JTextComponent textComp,
			final List<Validator<JTextComponent>> checkers) {
		installListener(textComp);
		return ValidationOverlayFactory.createErrorOverlayedIcon(textComp, checkers);
	}

	public static ValidationOverlay createErrorOverlayedIcon(final JTextComponent textComp,
			final Validator<JTextComponent> checkers) {
		installListener(textComp);
		return ValidationOverlayFactory.createErrorOverlayedIcon(textComp, checkers);
	}

	// ---------------

	private static void installListener(final JTextComponent textComp) {
		textComp.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(final DocumentEvent e) {
				ValidationOverlayFactory.check(textComp);
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				ValidationOverlayFactory.check(textComp);
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				ValidationOverlayFactory.check(textComp);
			}
		});
	}
}
