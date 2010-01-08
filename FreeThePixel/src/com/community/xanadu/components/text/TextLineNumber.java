package com.community.xanadu.components.text;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

//http://tips4java.wordpress.com/2009/05/23/text-component-line-number/
/**
 * This class will display line numbers for a related text component. The text
 * component must use the same line height for each line. TextLineNumber
 * supports wrapped lines and will highlight the line number of the current line
 * in the text component.
 * 
 * This class was designed to be used as a component added to the row header of
 * a JScrollPane.
 */
public class TextLineNumber extends JPanel implements CaretListener, DocumentListener {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JTextArea jta = new JTextArea();
				jta.setWrapStyleWord(true);
				jta.setLineWrap(true);
				jta
						.setText("mmmmmmmmmmmmmm mmmmmmmmmmmmm mmmmmmmmmmmmm mmmmmmmmmm mmmmmmmmmm\n\nmmmmmmmmmmmmm mmmmmmmmmmmmmmmmmmm mmmmmmmmmmmmmmmmmmmmmmmmmm\nmmmmmm ");
				JScrollPane scroll = new JScrollPane(jta);
				scroll.setRowHeaderView(new TextLineNumber(jta));
				f.getContentPane().add(scroll);
				f.setSize(350, 350);
				f.setVisible(true);
			}
		});
	}

	public final static float LEFT = 0.0f;
	public final static float CENTER = 0.5f;
	public final static float RIGHT = 1.0f;

	private final static Border OUTER = new MatteBorder(0, 0, 0, 2, Color.GRAY);

	private final static int HEIGHT = Integer.MAX_VALUE - 1000000;

	// Text component this TextTextLineNumber component is in sync with

	private JTextComponent component;

	// Properties that can be changed

	private int borderGap;
	private Color currentLineForeground;
	private float digitAlignment;
	private int minimumDisplayDigits;

	// Keep history information to reduce the number of times the component
	// needs to be repainted

	private int lastDigits;
	private int lastHeight;
	private int lastLine;

	/**
	 * Create a line number component for a text component. This minimum display
	 * width will be based on 3 digits.
	 * 
	 * @param component
	 *            the related text component
	 */
	public TextLineNumber(final JTextComponent component) {
		this(component, 3);
	}

	/**
	 * Create a line number component for a text component.
	 * 
	 * @param component
	 *            the related text component
	 * @param minimumDisplayDigits
	 *            the number of digits used to calculate the minimum width of
	 *            the component
	 */
	public TextLineNumber(final JTextComponent component, final int minimumDisplayDigits) {
		this.component = component;

		setFont(component.getFont());

		setBorderGap(5);
		setCurrentLineForeground(Color.RED);
		setDigitAlignment(RIGHT);
		setMinimumDisplayDigits(minimumDisplayDigits);

		component.getDocument().addDocumentListener(this);
		component.addCaretListener(this);
	}

	/**
	 * Gets the border gap
	 * 
	 * @return the border gap in pixels
	 */
	public int getBorderGap() {
		return this.borderGap;
	}

	/**
	 * The border gap is used in calculating the left and right insets of the
	 * border. Default value is 5.
	 * 
	 * @param borderGap
	 *            the gap in pixels
	 */
	public void setBorderGap(final int borderGap) {
		this.borderGap = borderGap;
		Border inner = new EmptyBorder(0, borderGap, 0, borderGap);
		setBorder(new CompoundBorder(OUTER, inner));
		this.lastDigits = 0;
		setPreferredWidth();
	}

	/**
	 * Gets the current line rendering Color
	 * 
	 * @return the Color used to render the current line number
	 */
	public Color getCurrentLineForeground() {
		return this.currentLineForeground == null ? getForeground() : this.currentLineForeground;
	}

	/**
	 * The Color used to render the current line digits. Default is Coolor.RED.
	 * 
	 * @param currentLineForeground
	 *            the Color used to render the current line
	 */
	public void setCurrentLineForeground(final Color currentLineForeground) {
		this.currentLineForeground = currentLineForeground;
	}

	/**
	 * Gets the digit alignment
	 * 
	 * @return the alignment of the painted digits
	 */
	public float getDigitAlignment() {
		return this.digitAlignment;
	}

	/**
	 * Specify the horizontal alignment of the digits within the component.
	 * Common values would be:
	 * <ul>
	 * <li>TextLineNumber.LEFT
	 * <li>TextLineNumber.CENTER
	 * <li>TextLineNumber.RIGHT (default)
	 * </ul>
	 * 
	 * @param currentLineForeground
	 *            the Color used to render the current line
	 */
	public void setDigitAlignment(final float digitAlignment) {
		this.digitAlignment = digitAlignment > 1.0f ? 1.0f : digitAlignment < 0.0f ? -1.0f : digitAlignment;
	}

	/**
	 * Gets the minimum display digits
	 * 
	 * @return the minimum display digits
	 */
	public int getMinimumDisplayDigits() {
		return this.minimumDisplayDigits;
	}

	/**
	 * Specify the mimimum number of digits used to calculate the preferred
	 * width of the component. Default is 3.
	 * 
	 * @param minimumDisplayDigits
	 *            the number digits used in the preferred width calculation
	 */
	public void setMinimumDisplayDigits(final int minimumDisplayDigits) {
		this.minimumDisplayDigits = minimumDisplayDigits;
		setPreferredWidth();
	}

	/**
	 * Calculate the width needed to display the maximum line number
	 */
	private void setPreferredWidth() {
		Element root = this.component.getDocument().getDefaultRootElement();
		int lines = root.getElementCount();
		int digits = Math.max(String.valueOf(lines).length(), this.minimumDisplayDigits);

		// Update sizes when number of digits in the line number changes

		if (this.lastDigits != digits) {
			this.lastDigits = digits;
			FontMetrics fontMetrics = getFontMetrics(getFont());
			int width = fontMetrics.charWidth('0') * digits;
			Insets insets = getInsets();
			int preferredWidth = insets.left + insets.right + width;

			Dimension d = getPreferredSize();
			d.setSize(preferredWidth, HEIGHT);
			setPreferredSize(d);
			setSize(d);
		}
	}

	/**
	 *
	 */
	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		// Determine the width of the space available to draw the line number

		FontMetrics fontMetrics = getFontMetrics(getFont());
		Insets insets = getInsets();
		int availableWidth = getSize().width - insets.left - insets.right;

		// Determine the number of lines to draw within the clipped bounds.
		// and the starting "Y" offset of the first line

		Rectangle clip = g.getClipBounds();
		int lineHeight = fontMetrics.getHeight();
		int startOffset = this.component.getInsets().top + fontMetrics.getAscent();
		int linesToDraw = clip.height / lineHeight + 1;
		int y = (clip.y / lineHeight) * lineHeight + startOffset;

		// These fields are used to determine the actual line number to draw

		Point viewPoint = new Point(0, y);
		int preferredHeight = this.component.getPreferredSize().height;

		for (int i = 0; i <= linesToDraw; i++) {
			if (isCurrentLine(viewPoint))
				g.setColor(getCurrentLineForeground());
			else
				g.setColor(getForeground());

			// Get the line number as a string and then determine the "X"
			// offset for drawing the string.

			String lineNumber = getTextLineNumber(viewPoint, preferredHeight);
			int stringWidth = fontMetrics.stringWidth(lineNumber);
			int x = getOffsetX(availableWidth, stringWidth) + insets.left;
			g.drawString(lineNumber, x, y);

			// Update the "Y" offset for the next line to be drawn

			y += lineHeight;
			viewPoint.y = y;

			if (y > preferredHeight)
				break;
		}
	}

	/*
	 * We need to know if the caret is currently positioned on the line we are
	 * about to paint so the line number can be highlighted.
	 */
	private boolean isCurrentLine(final Point viewPoint) {
		// The viewPoint represents the model view of the first character
		// on the line.

		int offset = this.component.viewToModel(viewPoint);
		int caretPosition = this.component.getCaretPosition();
		Element root = this.component.getDocument().getDefaultRootElement();

		if (root.getElementIndex(offset) == root.getElementIndex(caretPosition))
			return true;
		else
			return false;
	}

	/*
	 * Get the line number to be drawn. The empty string will be returned when a
	 * line of text has wrapped.
	 */
	protected String getTextLineNumber(final Point viewPoint, final int preferredHeight) {
		// The viewPoint represents the model view of the first character
		// on the line. When the model offset of this view matches the
		// offset of the first character on the line we have a line number to
		// draw, otherwise the line has wrapped so there is nothing to draw.

		int offset = this.component.viewToModel(viewPoint);
		Element root = this.component.getDocument().getDefaultRootElement();
		int index = root.getElementIndex(offset);
		Element line = root.getElement(index);

		if (line.getStartOffset() == offset)
			return String.valueOf(index + 1);
		else
			return "";
	}

	/*
	 * Determine the X offset to properly align the line number when drawn
	 */
	private int getOffsetX(final int availableWidth, final int stringWidth) {
		return (int) ((availableWidth - stringWidth) * this.digitAlignment);
	}

	//
	// Implement CaretListener interface
	//
	@Override
	public void caretUpdate(final CaretEvent e) {
		// Get the line the caret is positioned on

		int caretPosition = this.component.getCaretPosition();
		Element root = this.component.getDocument().getDefaultRootElement();
		int currentLine = root.getElementIndex(caretPosition);

		// Need to repaint so the correct line number can be highlighted

		if (this.lastLine != currentLine) {
			repaint();
			this.lastLine = currentLine;
		}
	}

	//
	// Implement DocumentListener interface
	//
	@Override
	public void changedUpdate(final DocumentEvent e) {
	}

	@Override
	public void insertUpdate(final DocumentEvent e) {
		documentChanged();
	}

	@Override
	public void removeUpdate(final DocumentEvent e) {
		documentChanged();
	}

	/*
	 * A document change may affect the number of displayed lines of text.
	 * Therefore the lines numbers will also change.
	 */
	private void documentChanged() {
		// Preferred size of the component has not been updated at the time
		// the DocumentEvent is fired

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int preferredHeight = TextLineNumber.this.component.getPreferredSize().height;

				// Document change has caused a change in the number of lines.
				// Repaint to reflect the new line numbers

				if (TextLineNumber.this.lastHeight != preferredHeight) {
					setPreferredWidth();
					repaint();
					TextLineNumber.this.lastHeight = preferredHeight;
				}
			}
		});
	}
}
