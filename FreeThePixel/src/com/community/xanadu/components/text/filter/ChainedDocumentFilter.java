package com.community.xanadu.components.text.filter;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

//http://tips4java.wordpress.com/2009/10/18/chaining-document-filters/
/**
 * A single DocumentFilter can be set on an AbstractDocument. There may be times
 * when you wish to perform multiple filtering of the data as it is added to a
 * Document. By extending the ChainedDocumentFilter instead of the
 * DocumentFilter you will receive added flexibility in that the filter can be
 * used stand alone or with other ChainedDocumentFilters.
 * 
 * Whenever one filter step fails, the chaining of the filters is also
 * terminated. In this case is it recommended you invoke the
 * provideErrorFeedback() method to provide user feedback.
 */
abstract class ChainedDocumentFilter extends DocumentFilter {
	private DocumentFilter filter;

	/**
	 * Standard constructor for standalone usage
	 */
	public ChainedDocumentFilter() {
		this(null);
	}

	/**
	 * Constructor used when further filtering is required after this filter has
	 * been applied.
	 */
	public ChainedDocumentFilter(final DocumentFilter filter) {
		setFilter(filter);
	}

	/**
	 * Get the next filter in the chain.
	 * 
	 * @return the next filter in the chain
	 */
	public DocumentFilter getFilter() {
		return this.filter;
	}

	/**
	 * Set the next filter in the chain
	 * 
	 * @param filter
	 */
	public void setFilter(final DocumentFilter filter) {
		this.filter = filter;
	}

	/**
	 * Install this filter on the AbstractDocument
	 * 
	 * @param components
	 *            the text components that will use this filter
	 */
	public void installFilter(final JTextComponent... components) {
		for (JTextComponent component : components) {
			Document doc = component.getDocument();

			if (doc instanceof AbstractDocument) {
				((AbstractDocument) doc).setDocumentFilter(this);
			}
		}
	}

	/**
	 * Remove this filter from the AbstractDocument
	 * 
	 * @param compoents
	 *            remove the filter from the specified text components
	 */
	public void uninstallFilter(final JTextComponent... components) {
		for (JTextComponent component : components) {
			Document doc = component.getDocument();

			if (doc instanceof AbstractDocument) {
				((AbstractDocument) doc).setDocumentFilter(null);
			}
		}
	}

	/**
	 * Provide appropriate LAF feedback when a filter error occurs.
	 */
	public void provideErrorFeedback() {
		LookAndFeel laf = UIManager.getLookAndFeel();

		if (laf == null) {
			Toolkit.getDefaultToolkit().beep();
		} else {
			KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			Component component = fm.getFocusOwner();
			laf.provideErrorFeedback(component);
		}
	}

	@Override
	public void insertString(final FilterBypass fb, final int offs, final String str, final AttributeSet a)
			throws BadLocationException {
		if (this.filter == null)
			super.insertString(fb, offs, str, a);
		else
			this.filter.insertString(fb, offs, str, a);
	}

	@Override
	public void replace(final FilterBypass fb, final int offs, final int length, final String str, final AttributeSet a)
			throws BadLocationException {
		if (this.filter == null)
			super.replace(fb, offs, length, str, a);
		else
			this.filter.replace(fb, offs, length, str, a);
	}

	@Override
	public void remove(final DocumentFilter.FilterBypass fb, final int offset, final int length)
			throws BadLocationException {
		if (this.filter == null)
			super.remove(fb, offset, length);
		else
			this.filter.remove(fb, offset, length);
	}
}
