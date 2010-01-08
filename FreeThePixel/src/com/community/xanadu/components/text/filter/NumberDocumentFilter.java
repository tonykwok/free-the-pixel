package com.community.xanadu.components.text.filter;

import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class NumberDocumentFilter extends ChainedDocumentFilter {
	private static final long serialVersionUID = 1L;
	private static Pattern pattern;

	public NumberDocumentFilter() {
	}

	private static Pattern getPattern() {
		if (pattern == null) {
			pattern = Pattern.compile("[0-9]");
		}
		return pattern;
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				JTextField text = new JTextField();
				new NumberDocumentFilter().installFilter(text);
				f.getContentPane().add(text);
				f.setSize(200, 200);
				f.setVisible(true);
			}
		});
	}

	public boolean acceptChange(final String str) {
		return getPattern().matcher(str).matches();
	}

	@Override
	public void insertString(final FilterBypass fb, final int offs, final String str, final AttributeSet a)
			throws BadLocationException {
		if (acceptChange(str)) {
			super.insertString(fb, offs, str, a);
		} else {
			provideErrorFeedback();
		}
	}

	@Override
	public void replace(final FilterBypass fb, final int offs, final int length, final String str, final AttributeSet a)
			throws BadLocationException {
		if (acceptChange(str)) {
			super.replace(fb, offs, length, str, a);
		} else {
			provideErrorFeedback();
		}
	}
}
