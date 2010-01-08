package com.community.xanadu.components.text.filter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * The LowerCaseDocumentFilter converts all characters to lower case before the
 * characters are inserted into the Document.
 */
public class LowerCaseDocumentFilter extends ChainedDocumentFilter {
	/**
	 * Standard constructor for stand alone usage
	 */
	public LowerCaseDocumentFilter() {
	}

	/**
	 * Constructor used when further filtering is required after this filter has
	 * been applied.
	 */
	public LowerCaseDocumentFilter(final DocumentFilter filter) {
		super(filter);
	}

	@Override
	public void insertString(final FilterBypass fb, final int offs, final String str, final AttributeSet a)
			throws BadLocationException {
		if (str != null) {
			String converted = convertString(str);
			super.insertString(fb, offs, converted, a);
		} else {
			super.insertString(fb, offs, str, a);
		}
	}

	@Override
	public void replace(final FilterBypass fb, final int offs, final int length, final String str, final AttributeSet a)
			throws BadLocationException {
		if (str != null) {
			String converted = convertString(str);
			super.replace(fb, offs, length, converted, a);
		} else {
			super.replace(fb, offs, length, str, a);
		}
	}

	/**
	 * Convert each character in the input string to lower case
	 * 
	 * @param mixedCase
	 *            a String of mixed case characters
	 * @return an upper cased String
	 */
	private String convertString(final String mixedCase) {
		char[] lowerCase = mixedCase.toCharArray();

		for (int i = 0; i < lowerCase.length; i++) {
			lowerCase[i] = Character.toLowerCase(lowerCase[i]);
		}

		return new String(lowerCase);
	}
}
