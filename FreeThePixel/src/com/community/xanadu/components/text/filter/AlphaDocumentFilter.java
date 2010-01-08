package com.community.xanadu.components.text.filter;

import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.community.xanadu.utils.StringOperation;

public class AlphaDocumentFilter extends ChainedDocumentFilter {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				JTextField text = new JTextField();
				new AlphaDocumentFilter().installFilter(text);
				f.getContentPane().add(text);
				f.setSize(200, 200);
				f.setVisible(true);
			}
		});
	}

	public AlphaDocumentFilter() {
	}

	public AlphaDocumentFilter(final DocumentFilter filter) {
		super(filter);
	}

	Pattern pattern;

	public Pattern getPattern() {
		if (this.pattern == null) {
			this.pattern = Pattern.compile("[[a-z]|[A-Z]|'| ]*");
		}
		return this.pattern;
	}

	public boolean acceptChange(final String str) {
		return getPattern().matcher(StringOperation.noAccent(str)).matches();
	}

	@Override
	public void insertString(final FilterBypass fb, final int offs, final String str, final AttributeSet a)
			throws BadLocationException {
		if (acceptChange(fb.getDocument().getText(0, fb.getDocument().getLength()) + str)) {
			super.insertString(fb, offs, str, a);
		}
	}

	@Override
	public void replace(final FilterBypass fb, final int offs, final int length, final String str, final AttributeSet a)
			throws BadLocationException {
		if (acceptChange(str)) {
			super.replace(fb, offs, length, str, a);
		}
	}
}
