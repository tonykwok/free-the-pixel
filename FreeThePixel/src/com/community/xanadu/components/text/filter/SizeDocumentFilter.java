package com.community.xanadu.components.text.filter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class SizeDocumentFilter extends ChainedDocumentFilter {
	private static final long serialVersionUID = 1L;
	private int maxChar = -1;

	public SizeDocumentFilter(final int len) {
		this.maxChar = len;
	}

	@Override
	public void insertString(final FilterBypass fb, final int offs, final String str, final AttributeSet a)
			throws BadLocationException {
		if (str != null && this.maxChar > 0 && fb.getDocument().getLength() + str.length() > this.maxChar) {
			provideErrorFeedback();
		} else {
			super.insertString(fb, offs, str, a);
		}
	}

	@Override
	public void replace(final FilterBypass fb, final int offs, final int length, final String str, final AttributeSet a)
			throws BadLocationException {
		if (str != null && this.maxChar > 0 && fb.getDocument().getLength() + str.length() - length > this.maxChar) {
			provideErrorFeedback();
		} else {
			super.replace(fb, offs, length, str, a);
		}
	}
}
